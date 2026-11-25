package com.lastwave.app.ui.generate

import com.lastwave.app.util.appText

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lastwave.app.data.generate.GenerateRepository
import com.lastwave.app.data.generate.GeneratedTrack
import com.lastwave.app.data.generate.GenerationStatus
import com.lastwave.app.data.generate.RECOMMENDATION_TRACK_COUNT
import com.lastwave.app.data.generate.youtubeVideoIdOrNull
import com.lastwave.app.data.naming.PlaylistNamer
import com.lastwave.app.data.playlist.PlaylistRepository
import com.lastwave.app.playback.toPlayableTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

private const val GENERATION_TIMEOUT_MS = 60_000L

/** Generator modes. YouTube Music is the primary engine; Last.fm only fills
 *  gaps when connected, and guest/offline falls back to local + charts. */
enum class GenerateMode(val labelRes: Int, val descriptionRes: Int, val storageValue: String) {
    TOP(com.lastwave.app.R.string.gen_mode_top, com.lastwave.app.R.string.gen_mode_top_desc, "top"),
    RECENT(com.lastwave.app.R.string.gen_mode_recent, com.lastwave.app.R.string.gen_mode_recent_desc, appText(com.lastwave.app.R.string.fr_tab_recent)),
    SIMILAR_TRACKS(com.lastwave.app.R.string.gen_mode_song_radio, com.lastwave.app.R.string.gen_mode_song_radio_desc, "similar-tracks"),
    SIMILAR_ARTISTS(com.lastwave.app.R.string.gen_mode_sim_artists, com.lastwave.app.R.string.gen_mode_sim_artists_desc, "similar-artists"),
    TAG(com.lastwave.app.R.string.gen_mode_tag, com.lastwave.app.R.string.gen_mode_tag_desc, "tag"),
    MIX(com.lastwave.app.R.string.gen_mode_mix, com.lastwave.app.R.string.gen_mode_mix_desc, appText(com.lastwave.app.R.string.home_mix)),
    RECOMMENDATIONS(com.lastwave.app.R.string.gen_mode_reco, com.lastwave.app.R.string.gen_mode_reco_desc, appText(com.lastwave.app.R.string.settings_section_recommendations)),
    NEVER_HEARD(com.lastwave.app.R.string.gen_mode_never, com.lastwave.app.R.string.gen_mode_never_desc, "never-heard"),
    LIBRARY(com.lastwave.app.R.string.gen_mode_library, com.lastwave.app.R.string.gen_mode_library_desc, appText(com.lastwave.app.R.string.pl_title)),
}

/** Exact 6 period chips shared by Top Tracks + My Library. */
val GENERATE_PERIODS = listOf(
    appText(com.lastwave.app.R.string.fr_period_all) to com.lastwave.app.R.string.gen_period_all,
    "12month" to com.lastwave.app.R.string.gen_period_12mo,
    "6month" to com.lastwave.app.R.string.gen_period_6mo,
    "3month" to com.lastwave.app.R.string.gen_period_3mo,
    "1month" to com.lastwave.app.R.string.gen_period_1mo,
    "7day" to com.lastwave.app.R.string.gen_period_7d,
)

/** Exact 12 quick-select genre chips from generator.html's tag-suggestions. */
val GENRE_QUICK_CHIPS = listOf(appText(com.lastwave.app.R.string.ui_pop_267a64), appText(com.lastwave.app.R.string.ui_rock_468d79), appText(com.lastwave.app.R.string.ui_hip_hop_2c8b31), appText(com.lastwave.app.R.string.ui_electronic_622585), appText(com.lastwave.app.R.string.ui_jazz_0f940a), "lofi", appText(com.lastwave.app.R.string.ui_metal_90acb4), appText(com.lastwave.app.R.string.ui_indie_c43a74), appText(com.lastwave.app.R.string.ui_classical_1dd412), appText(com.lastwave.app.R.string.ui_r_b_fa9af5), "ambient", "punk")

@Immutable
data class GenerateUiState(
    val selectedMode: GenerateMode? = null,
    val trackCount: Int = 25,
    val period: String = appText(com.lastwave.app.R.string.fr_period_all),
    val seedTrackName: String = "",
    val seedArtistName: String = "",
    val seedVideoId: String? = null,
    val seedArtistQuery: String = "",
    val tagInput: String = "",
    val seedTrackResults: List<GeneratedTrack> = emptyList(),
    val seedArtistResults: List<String> = emptyList(),
    val isSearchingSeed: Boolean = false,
    val isGenerating: Boolean = false,
    val loadingMessage: String = "",
    val error: String? = null,
)

/** One-shot navigation signal. */
sealed interface GenerateNavEvent {
    data class NavigateToPlaylistLoading(val playlistId: Long? = null) : GenerateNavEvent
}

@HiltViewModel
class GenerateViewModel @Inject constructor(
    private val repository: GenerateRepository,
    private val playlistRepository: PlaylistRepository,
    private val artworkRepository: com.lastwave.app.data.artwork.ArtworkRepository,
    private val generationStatus: GenerationStatus,
    private val mixLauncher: MixLauncher,
    private val musicPlayer: com.lastwave.app.playback.MusicPlayer,
) : ViewModel() {

    /** Set by a "Start Mix" launch: the generated mix also becomes the queue. */
    private var queueNextResult = false

    private val _uiState = MutableStateFlow(GenerateUiState())
    val uiState: StateFlow<GenerateUiState> = _uiState.asStateFlow()

    private val _navEvents = MutableSharedFlow<GenerateNavEvent>(extraBufferCapacity = 1)
    val navEvents: SharedFlow<GenerateNavEvent> = _navEvents

    val lastSavedPlaylistId = MutableStateFlow<Long?>(null)
    private var seedSearchJob: Job? = null

    init {
        // "Start Mix with this Song" landing here from any screen's
        // track menu: pre-fill Similar Tracks with the tapped song and
        // generate immediately — a real one-tap mix.
        viewModelScope.launch {
            mixLauncher.pendingSeed.collect { seed ->
                if (seed != null) {
                    val consumed = mixLauncher.consume() ?: seed
                    clearSeedSearch()
                    _uiState.update {
                        it.copy(
                            selectedMode = GenerateMode.SIMILAR_TRACKS,
                            seedTrackName = consumed.trackName,
                            seedArtistName = consumed.artistName,
                            seedVideoId = consumed.videoId,
                            trackCount = 28,
                            error = null,
                        )
                    }
                    queueNextResult = true
                    generate()
                }
            }
        }
    }

    private fun clearSeedSearch() {
        seedSearchJob?.cancel()
        seedSearchJob = null
        _uiState.update {
            it.copy(isSearchingSeed = false, seedTrackResults = emptyList(), seedArtistResults = emptyList())
        }
    }

    fun selectMode(mode: GenerateMode) {
        if (_uiState.value.isGenerating) return
        clearSeedSearch()
        _uiState.update {
            it.copy(selectedMode = if (it.selectedMode == mode) null else mode, error = null)
        }
    }

    fun setTrackCount(value: Int) { if (!_uiState.value.isGenerating) _uiState.update { it.copy(trackCount = value.coerceIn(5, 35)) } }
    fun setPeriod(value: String) = _uiState.update { it.copy(period = value) }
    fun setTagInput(value: String) = _uiState.update { it.copy(tagInput = value) }
    fun setSeedArtistQuery(value: String) {
        clearSeedSearch()
        _uiState.update { it.copy(seedArtistQuery = value) }
    }
    fun setSeedTrackName(value: String) {
        clearSeedSearch()
        _uiState.update { it.copy(seedTrackName = value, seedVideoId = null) }
    }
    fun setSeedArtistName(value: String) {
        clearSeedSearch()
        _uiState.update { it.copy(seedArtistName = value, seedVideoId = null) }
    }

    private fun <T> loadSeedResults(load: suspend () -> T, apply: (GenerateUiState, T) -> GenerateUiState) {
        clearSeedSearch()
        _uiState.update { it.copy(isSearchingSeed = true, error = null) }
        seedSearchJob = viewModelScope.launch {
            try {
                val results = load()
                // A cancelled provider may still return; never publish into an edited seed or another mode.
                coroutineContext.ensureActive()
                _uiState.update { apply(it, results).copy(isSearchingSeed = false) }
            } catch (e: Exception) {
                coroutineContext.ensureActive()
                _uiState.update { it.copy(isSearchingSeed = false, error = e.message) }
            }
        }
    }

    fun loadTopTracksForSeed() = loadSeedResults(
        load = { repository.topTracksForSeed() },
        apply = { state, results -> state.copy(seedTrackResults = results) },
    )

    fun loadTopArtistsForSeed() = loadSeedResults(
        load = { repository.topArtistsForSeed() },
        apply = { state, results -> state.copy(seedArtistResults = results) },
    )

    fun searchSeedTrack() {
        val state = _uiState.value
        if (state.seedTrackName.isBlank()) return
        loadSeedResults(
            load = { repository.searchTracks(state.seedTrackName, state.seedArtistName.ifBlank { null }) },
            apply = { current, results -> current.copy(seedTrackResults = results) },
        )
    }

    fun searchSeedArtist() {
        val state = _uiState.value
        if (state.seedArtistQuery.isBlank()) return
        loadSeedResults(
            load = { repository.searchArtists(state.seedArtistQuery) },
            apply = { current, results -> current.copy(seedArtistResults = results) },
        )
    }

    fun pickSeedTrack(track: GeneratedTrack) {
        clearSeedSearch()
        _uiState.update {
            it.copy(seedTrackName = track.name, seedArtistName = track.artist, seedVideoId = track.youtubeVideoIdOrNull())
        }
    }
    fun pickSeedArtist(name: String) {
        clearSeedSearch()
        _uiState.update { it.copy(seedArtistQuery = name) }
    }
    fun setGenreChip(tag: String) = _uiState.update { it.copy(tagInput = tag) }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun generate() {
        // Taken first, so a launch that fails validation or is refused here
        // can never make a later manual generate re-queue.
        val queueMix = queueNextResult
        queueNextResult = false
        val state = _uiState.value
        if (state.isGenerating) return
        val mode = state.selectedMode ?: return

        when (mode) {
            GenerateMode.SIMILAR_TRACKS -> if (state.seedTrackName.isBlank() || state.seedArtistName.isBlank()) {
                _uiState.update { it.copy(error = appText(com.lastwave.app.R.string.ui_enter_a_seed_track_and_artist_7f69b8)) }; return
            }
            GenerateMode.SIMILAR_ARTISTS -> if (state.seedArtistQuery.isBlank()) {
                _uiState.update { it.copy(error = appText(com.lastwave.app.R.string.ui_enter_a_seed_artist_a0f1b0)) }; return
            }
            GenerateMode.TAG -> if (state.tagInput.isBlank()) {
                _uiState.update { it.copy(error = appText(com.lastwave.app.R.string.ui_enter_a_genre_tag_d3451b)) }; return
            }
            else -> Unit
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null, loadingMessage = appText(com.lastwave.app.R.string.ui_analyzing_taste_profile_6829ba)) }
            generationStatus.update(isGenerating = true, message = appText(com.lastwave.app.R.string.ui_analyzing_taste_profile_6829ba))
            try {
                val onProgress: (String) -> Unit = { msg ->
                    _uiState.update { s -> s.copy(loadingMessage = msg) }
                    generationStatus.update(isGenerating = true, message = msg)
                }

                if (mode == GenerateMode.SIMILAR_TRACKS) {
                    onProgress(appText(com.lastwave.app.R.string.ui_finding_similar_songs_with_youtube_music_a1047d))
                } else {
                    onProgress(appText(com.lastwave.app.R.string.ui_blending_last_fm_and_youtube_music_6a4064))
                }

                val targetCount = if (mode == GenerateMode.RECOMMENDATIONS) {
                    RECOMMENDATION_TRACK_COUNT
                } else {
                    state.trackCount
                }
                // Pull a wider ranked pool so saved songs can be softly
                // deprioritized without shrinking the requested playlist.
                val candidateCount = if (mode == GenerateMode.RECOMMENDATIONS) {
                    targetCount
                } else {
                    (targetCount + maxOf(10, targetCount / 2)).coerceAtMost(60)
                }

                val raw: List<GeneratedTrack> = withTimeout(GENERATION_TIMEOUT_MS) {
                    when (mode) {
                        GenerateMode.TOP -> repository.fetchTopTracks(candidateCount, state.period)
                        GenerateMode.LIBRARY -> repository.fetchTopTracks(candidateCount, state.period)
                        GenerateMode.RECENT -> repository.fetchRecentTracks(candidateCount)
                        GenerateMode.SIMILAR_TRACKS -> repository.fetchSimilarTracks(
                            track = state.seedTrackName,
                            artist = state.seedArtistName,
                            limit = candidateCount,
                            seedVideoId = state.seedVideoId,
                        )
                        GenerateMode.SIMILAR_ARTISTS -> repository.fetchSimilarArtistTracks(state.seedArtistQuery, candidateCount)
                        GenerateMode.TAG -> repository.fetchTagTracks(state.tagInput, candidateCount)
                        GenerateMode.MIX -> repository.fetchMix(candidateCount, onProgress)
                        GenerateMode.RECOMMENDATIONS -> repository.fetchRecommendations(targetCount, onProgress)
                        GenerateMode.NEVER_HEARD -> repository.fetchNeverHeardTracks(candidateCount, onProgress)
                    }
                }

                onProgress(appText(com.lastwave.app.R.string.ui_pre_checking_availability_ece3f4))
                val preparedTracks = if (mode == GenerateMode.RECOMMENDATIONS) {
                    // RecommendationEngine already applies its own diversity
                    // stages. Do not discard fresh tracks afterward via the
                    // generic per-artist cap and accidentally save under 35.
                    repository.deduplicate(raw)
                } else {
                    repository.precheck(raw)
                }
                val finalTracks = if (mode == GenerateMode.NEVER_HEARD) {
                    preparedTracks.take(targetCount)
                } else {
                    repository.preferPlaylistFreshness(
                        tracks = preparedTracks,
                        limit = targetCount,
                        savedKeys = repository.savedPlaylistTrackKeys(),
                    )
                }
                if (finalTracks.isEmpty()) {
                    val message = if (mode == GenerateMode.SIMILAR_TRACKS && state.seedTrackName.isNotBlank()) {
                        appText(com.lastwave.app.R.string.ui_no_similar_songs_found_to_mix_for_1_s_d6ed13, (state.seedTrackName).toString())
                    } else if (mode == GenerateMode.SIMILAR_ARTISTS && state.seedArtistQuery.isNotBlank()) {
                        appText(com.lastwave.app.R.string.ui_no_similar_artists_found_for_1_s_0e4b39, (state.seedArtistQuery).toString())
                    } else if (mode == GenerateMode.TAG && state.tagInput.isNotBlank()) {
                        appText(com.lastwave.app.R.string.ui_no_songs_found_for_tag_1_s_a87759, (state.tagInput).toString())
                    } else {
                        appText(com.lastwave.app.R.string.ui_no_songs_found_to_create_this_mix_af6eba)
                    }
                    throw IllegalStateException(message)
                }

                onProgress(appText(com.lastwave.app.R.string.ui_saving_playlist_89bc51))
                val existingTitles = playlistRepository.titles()
                val title = PlaylistNamer.generateUniqueName(existingTitles)
                val subtitle = PlaylistNamer.subtitleFor(
                    mode = mode.storageValue,
                    tagInput = state.tagInput,
                    seedTrackName = state.seedTrackName,
                    seedArtistInput = state.seedArtistQuery,
                )
                val saved = playlistRepository.save(title, subtitle, mode.storageValue, finalTracks)
                lastSavedPlaylistId.value = saved.id
                // Start Mix: the playing song stays, the mix takes over the queue after it.
                if (queueMix) musicPlayer.replaceUpcoming(finalTracks.map { it.toPlayableTrack() })

                // Playlist is persisted — done from the user's point of view.
                // Cover-art enrichment keeps running in the background and
                // updates rows reactively via ArtworkRepository's own flow,
                // so we don't make the user wait on it before showing the result.
                viewModelScope.launch {
                    try {
                        artworkRepository.enrichBatch(finalTracks.take(8).map { it.name to it.artist })
                    } catch (e: Exception) { }
                }

                _navEvents.tryEmit(GenerateNavEvent.NavigateToPlaylistLoading(saved.id))
            } catch (_: TimeoutCancellationException) {
                _uiState.update { it.copy(error = appText(com.lastwave.app.R.string.ui_playlist_generation_timed_out_check_your_connect_40ad6e)) }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: appText(com.lastwave.app.R.string.ui_couldn_t_generate_a_playlist_c17e2d)) }
            } finally {
                _uiState.update { it.copy(isGenerating = false) }
                generationStatus.update(isGenerating = false)
            }
        }
    }
}
