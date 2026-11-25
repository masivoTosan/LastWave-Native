package com.lastwave.app.ui.playlist

import com.lastwave.app.util.appText

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lastwave.app.data.artwork.ArtworkRepository
import com.lastwave.app.data.generate.GenerateRepository
import com.lastwave.app.data.generate.GeneratedTrack
import com.lastwave.app.data.generate.RECOMMENDATION_TRACK_COUNT
import com.lastwave.app.data.naming.PlaylistNamer
import com.lastwave.app.data.playlist.PlaylistRepository
import com.lastwave.app.data.playlist.PlaylistExportEvents
import com.lastwave.app.data.playlist.SavedPlaylist
import com.lastwave.app.data.playlist.LIKED_SONGS_MODE
import com.lastwave.app.data.playlist.isYouTubeOnly
import com.lastwave.app.data.repository.AuthRepository
import com.lastwave.app.util.FileExportHelper
import com.lastwave.app.util.PlaylistExportFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import com.lastwave.app.data.playlist.SpotifyUserPlaylist
import com.lastwave.app.data.playlist.SpotifyPlaylistImporter
import com.lastwave.app.data.spotify.SpotifyPreferences
import com.lastwave.app.data.local.SettingsPreferences

enum class ExportFormat { CSV, M3U }
enum class PlaylistSortMode { DATE_DESC, DATE_ASC, NAME, TRACK_COUNT }

@Immutable
data class PlaylistUiState(
    val isLoading: Boolean = true,
    val playlists: List<SavedPlaylist> = emptyList(),
    val spotifyPlaylists: List<SpotifyUserPlaylist> = emptyList(),
    val isSpotifySyncEnabled: Boolean = false,
    val isSpotifyLoading: Boolean = false,
    val sortMode: PlaylistSortMode = PlaylistSortMode.DATE_DESC,
    val expandedIds: Set<Long> = emptySet(),
    val newestId: Long? = null,
    val justSavedBannerVisible: Boolean = false,
    val exportSheetForPlaylistId: Long? = null,
    val deleteConfirmForPlaylistId: Long? = null,
    val regeneratingId: Long? = null,
    val toastMessage: String? = null,
    val deleteScrobbleAuthRequired: Boolean = false,
    val isGenerating: Boolean = false,
    val generatingMessage: String = "",
    val createDialogVisible: Boolean = false,
    val renamePlaylistId: Long? = null,
    val detailPlaylist: SavedPlaylist? = null,
    val isDetailLoading: Boolean = false,
)

/**
 * Full port of playlist.js's saved-playlist screen state: list + expand/
 * collapse + the "just generated" regenerate bar (§4.2) + export (§4.6) +
 * Generate Similar (§4.7) + delete. Reads/writes through PlaylistRepository
 * (Room), so anything GenerateViewModel saves shows up here automatically
 * on next load() — this ViewModel calls load() from init and whenever the
 * screen becomes visible again (the Composable re-triggers it via a
 * lifecycle-aware LaunchedEffect key, not polling).
 */
@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val generateRepository: GenerateRepository,
    private val artworkRepository: ArtworkRepository,
    private val authRepository: AuthRepository,
    private val fileExportHelper: FileExportHelper,
    private val generationStatus: com.lastwave.app.data.generate.GenerationStatus,
    private val exportEvents: PlaylistExportEvents,
    private val ytMusicPreferences: com.lastwave.app.data.ytmusic.YtMusicPreferences,
    private val ytMusicSyncManager: com.lastwave.app.data.ytmusic.YtMusicSyncManager,
    private val ytMusicLibraryManager: com.lastwave.app.data.ytmusic.YtMusicLibraryManager,
    private val trackDownloadManager: com.lastwave.app.data.download.TrackDownloadManager,
    private val routeNavigator: com.lastwave.app.ui.navigation.AppRouteNavigator,
    private val localMusicLibrary: com.lastwave.app.data.localmusic.LocalMusicLibrary,
    private val spotifyPlaylistImporter: SpotifyPlaylistImporter,
    private val spotifyPreferences: SpotifyPreferences,
    private val settingsPreferences: SettingsPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistUiState())
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    /** Songs saved for offline, for Library's Downloads card. */
    val downloadedCount: StateFlow<Int> = trackDownloadManager.downloadedTrackDao.getAll()
        .map { it.size }
        .catch { emit(0) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun openDownloads() {
        routeNavigator.navigateTo(com.lastwave.app.ui.navigation.Screen.Downloads.route)
    }

    private val trackMoveVersions = mutableMapOf<Long, Long>()
    private val pendingTrackMoves = mutableMapOf<Long, Int>()

    private fun keepDraggedOrder(id: Long, readVersions: Map<Long, Long>): Boolean =
        (pendingTrackMoves[id] ?: 0) > 0 || trackMoveVersions[id] != readVersions[id]

    val syncedPlaylistIds: StateFlow<Set<Long>?> = ytMusicPreferences.syncedPlaylistIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun toggleYtSync(playlistId: Long) {
        viewModelScope.launch {
            // A failed read must abort the toggle: an empty fallback here
            // would rewrite the sync selection from a partial list.
            val allIds = try {
                playlistRepository.getAll().map { it.id }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (e: Exception) {
                android.util.Log.e("PlaylistViewModel", "Couldn't read playlists for sync toggle", e)
                _uiState.update { it.copy(toastMessage = appText(com.lastwave.app.R.string.ui_couldn_t_read_playlists_try_again_8ca078)) }
                return@launch
            }
            val current = ytMusicPreferences.syncedPlaylistIds.first() ?: allIds.toSet()
            val isSynced = playlistId in current
            ytMusicPreferences.togglePlaylistSync(allIds, playlistId, !isSynced)
            if (ytMusicPreferences.syncEnabled.first()) {
                runCatching { ytMusicSyncManager.syncNow("selection_change") }
            }
            _uiState.update {
                it.copy(toastMessage = if (!isSynced) appText(com.lastwave.app.R.string.ui_playlist_added_to_youtube_music_sync_943caf) else appText(com.lastwave.app.R.string.ui_playlist_removed_from_youtube_music_sync_4f88ac))
            }
        }
    }

    init {
        load()
        viewModelScope.launch {
            combine(
                settingsPreferences.settings.map { it.spotifyPlaylistsSyncEnabled },
                spotifyPreferences.isConnected,
            ) { enabled, connected ->
                enabled && connected
            }.distinctUntilChanged().collect { active ->
                _uiState.update { it.copy(isSpotifySyncEnabled = active) }
                if (active) {
                    loadSpotifyPlaylists()
                } else {
                    _uiState.update { it.copy(spotifyPlaylists = emptyList(), isSpotifyLoading = false) }
                }
            }
        }
        viewModelScope.launch {
            var wasGenerating = false
            generationStatus.state.collect { progress ->
                _uiState.update { it.copy(isGenerating = progress.isGenerating, generatingMessage = progress.message) }
                if (wasGenerating && !progress.isGenerating) {
                    load()
                }
                wasGenerating = progress.isGenerating
            }
        }
        viewModelScope.launch {
            exportEvents.failures.collect { message ->
                _uiState.update { it.copy(toastMessage = message) }
            }
        }
        viewModelScope.launch {
            // Debounced: a single drag-reorder fires one change per crossed
            // row. Reloading on every one would yank the list mid-drag (full
            // re-read + YT refresh + sync pass per row) and race the
            // persists, so the order snaps back instead of sticking. The
            // optimistic detail update keeps the row under the finger; one
            // reload after the drag settles confirms the persisted order.
            playlistRepository.changes.debounce(750L).collect { load() }
        }
        viewModelScope.launch {
            ytMusicLibraryManager.playlists.collect { remote ->
                val openRemoteId = _uiState.value.detailPlaylist
                    ?.takeIf { it.isYouTubeOnly }
                    ?.id
                _uiState.update { current ->
                    val local = current.playlists.filterNot { it.isYouTubeOnly }
                    current.copy(playlists = mergeAndDeduplicatePlaylists(local, remote, current.sortMode))
                }
                if (openRemoteId != null && remote.any { it.id == openRemoteId }) {
                    ytMusicLibraryManager.loadDetail(openRemoteId)?.let { refreshed ->
                        _uiState.update { current ->
                            current.copy(
                                detailPlaylist = refreshed,
                                playlists = current.playlists.map { playlist ->
                                    if (playlist.id == refreshed.id) refreshed else playlist
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    /** Re-reads from Room. Called on first composition and again whenever
     *  the Playlist tab regains visibility (e.g. right after Generate
     *  saves a new playlist) — see PlaylistScreen's LaunchedEffect. */
    fun loadSpotifyPlaylists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSpotifyLoading = true) }
            val (raw, likedCount) = coroutineScope {
                val playlistsDeferred = async {
                    try {
                        spotifyPlaylistImporter.fetchUserPlaylists()
                    } catch (e: Exception) {
                        currentCoroutineContext().ensureActive()
                        emptyList()
                    }
                }
                val likedCountDeferred = async {
                    try {
                        spotifyPlaylistImporter.fetchLikedSongsCount()
                    } catch (e: Exception) {
                        currentCoroutineContext().ensureActive()
                        null
                    }
                }
                playlistsDeferred.await() to likedCountDeferred.await()
            }
            val likedSummary = SpotifyUserPlaylist(
                id = appText(com.lastwave.app.R.string.wave_widget_playlist_liked),
                name = appText(com.lastwave.app.R.string.spotify_liked_songs_title),
                description = appText(com.lastwave.app.R.string.spotify_liked_songs_sub),
                artworkUrl = null,
                trackCount = likedCount,
            )
            val fullList = listOf(likedSummary) + raw
            _uiState.update { it.copy(spotifyPlaylists = fullList, isSpotifyLoading = false) }
        }
    }

    fun load(justGeneratedId: Long? = null) {
        if (_uiState.value.isSpotifySyncEnabled) {
            loadSpotifyPlaylists()
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val readVersions = trackMoveVersions.toMap()
            val local = try {
                // The built-in "Local songs" playlist is always present.
                playlistRepository.getAll().let { all ->
                    if (all.any { it.mode == com.lastwave.app.data.playlist.LOCAL_SONGS_MODE }) all
                    else runCatching { playlistRepository.ensureLocalSongs() }
                        .map { playlistRepository.getAll() }
                        .getOrDefault(all)
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (e: Exception) {
                android.util.Log.e("PlaylistViewModel", "Error reading local playlists", e)
                emptyList()
            }
            val remote = runCatching { ytMusicLibraryManager.playlists.value }.getOrDefault(emptyList())
            val all = mergeAndDeduplicatePlaylists(local, remote, _uiState.value.sortMode)
            val newest = justGeneratedId ?: all.maxByOrNull { it.createdAtMillis }?.id
            _uiState.update { current ->
                val currentById = current.playlists.associateBy { it.id }
                val refreshed = all.map { playlist ->
                    if (keepDraggedOrder(playlist.id, readVersions)) currentById[playlist.id] ?: playlist else playlist
                }
                val currentDetailId = current.detailPlaylist?.id
                val updatedDetail = if (currentDetailId != null && currentDetailId >= 0L) {
                    if (keepDraggedOrder(currentDetailId, readVersions)) current.detailPlaylist
                    else refreshed.firstOrNull { pl -> pl.id == currentDetailId }
                } else {
                    current.detailPlaylist
                }
                current.copy(
                    isLoading = false,
                    playlists = refreshed,
                    detailPlaylist = updatedDetail ?: current.detailPlaylist,
                    newestId = newest,
                    expandedIds = if (justGeneratedId != null) setOf(justGeneratedId) else current.expandedIds,
                    justSavedBannerVisible = justGeneratedId != null,
                )
            }
            if (justGeneratedId != null) {
                all.firstOrNull { it.id == justGeneratedId }?.let { pl ->
                    try {
                        artworkRepository.enrichBatch(pl.tracks.take(6).map { it.name to it.artist })
                    } catch (cancellation: CancellationException) {
                        throw cancellation
                    } catch (e: Exception) {
                        android.util.Log.e("PlaylistViewModel", "Error pre-warming artwork", e)
                    }
                }
            }
            try {
                ytMusicLibraryManager.refresh()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (e: Exception) {
                android.util.Log.e("PlaylistViewModel", "Error refreshing YT library", e)
            }
            runCatching { ytMusicSyncManager.syncNow("playlist_open") }
        }
    }

    /** Loads a specific playlist by ID directly from Room for the detail screen. */
    fun loadDetail(playlistId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDetailLoading = true) }
            val readVersions = trackMoveVersions.toMap()
            val pl = if (playlistId < 0L) {
                ytMusicLibraryManager.loadDetail(playlistId) { updated ->
                    _uiState.update { current ->
                        current.copy(
                            isDetailLoading = updated.tracks.isEmpty() && current.isDetailLoading,
                            detailPlaylist = updated,
                            playlists = current.playlists.map { existing ->
                                if (existing.id == updated.id) updated else existing
                            },
                        )
                    }
                    if (updated.tracks.isNotEmpty()) {
                        viewModelScope.launch {
                            try {
                                artworkRepository.enrichBatch(updated.tracks.take(10).map { t -> t.name to t.artist })
                            } catch (cancellation: CancellationException) {
                                throw cancellation
                            } catch (e: Exception) {
                                android.util.Log.e("PlaylistViewModel", "Error pre-warming artwork", e)
                            }
                        }
                    }
                }
            } else {
                playlistRepository.getById(playlistId)
            }
            _uiState.update {
                if (keepDraggedOrder(playlistId, readVersions)) return@update it.copy(isDetailLoading = false)
                it.copy(
                    isDetailLoading = false,
                    detailPlaylist = pl ?: it.detailPlaylist,
                    playlists = if (pl != null) {
                        if (it.playlists.any { existing -> existing.id == pl.id }) {
                            it.playlists.map { existing -> if (existing.id == pl.id) pl else existing }
                        } else it.playlists + pl
                    } else it.playlists,
                )
            }
            if (pl != null && pl.tracks.isNotEmpty()) {
                try {
                    artworkRepository.enrichBatch(pl.tracks.take(10).map { t -> t.name to t.artist })
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (e: Exception) {
                    android.util.Log.e("PlaylistViewModel", "Error pre-warming artwork", e)
                }
            }
        }
    }

    fun setSortMode(mode: PlaylistSortMode) {
        _uiState.update { s ->
            val sorted = sortPlaylists(s.playlists, mode)
            s.copy(sortMode = mode, playlists = sorted)
        }
    }

    private fun sortPlaylists(playlists: List<SavedPlaylist>, mode: PlaylistSortMode): List<SavedPlaylist> =
        playlists.sortedWith(
            when (mode) {
                PlaylistSortMode.DATE_DESC -> compareByDescending<SavedPlaylist> { it.mode == LIKED_SONGS_MODE && it.isPinned }
                    .thenByDescending { it.isPinned }
                    .thenByDescending { it.createdAtMillis }
                PlaylistSortMode.DATE_ASC -> compareByDescending<SavedPlaylist> { it.mode == LIKED_SONGS_MODE && it.isPinned }
                    .thenByDescending { it.isPinned }
                    .thenBy { it.createdAtMillis }
                PlaylistSortMode.NAME -> compareByDescending<SavedPlaylist> { it.mode == LIKED_SONGS_MODE && it.isPinned }
                    .thenByDescending { it.isPinned }
                    .thenBy { it.title.lowercase() }
                PlaylistSortMode.TRACK_COUNT -> compareByDescending<SavedPlaylist> { it.mode == LIKED_SONGS_MODE && it.isPinned }
                    .thenByDescending { it.isPinned }
                    .thenByDescending { it.remoteTrackCount ?: it.tracks.size }
            },
        )

    private fun mergeAndDeduplicatePlaylists(
        local: List<SavedPlaylist>,
        remote: List<SavedPlaylist>,
        mode: PlaylistSortMode,
    ): List<SavedPlaylist> {
        val dedupedRemote = remote.filterNot { r ->
            local.any { l ->
                (l.remotePlaylistId != null && l.remotePlaylistId == r.remotePlaylistId) ||
                l.title.equals(r.title, ignoreCase = true)
            }
        }
        return sortPlaylists(local + dedupedRemote, mode).distinctBy { it.id }
    }

    fun regenerateLatest() {
        val newest = _uiState.value.playlists.maxByOrNull { it.createdAtMillis } ?: return
        regenerate(newest.id)
    }

    fun dismissJustSavedBanner() = _uiState.update { it.copy(justSavedBannerVisible = false) }

    fun toggleExpanded(id: Long) {
        _uiState.update { s ->
            val next = s.expandedIds.toMutableSet()
            if (id in next) next.remove(id) else next.add(id)
            s.copy(expandedIds = next)
        }
    }

    fun openCreateDialog() = _uiState.update { it.copy(createDialogVisible = true) }
    fun dismissCreateDialog() = _uiState.update { it.copy(createDialogVisible = false) }

    fun createCustomPlaylist(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val playlist = playlistRepository.createCustom(title)
            _uiState.update {
                it.copy(
                    createDialogVisible = false,
                    expandedIds = it.expandedIds + playlist.id,
                    toastMessage = appText(com.lastwave.app.R.string.ui_created_1_s_196a9b, (playlist.title).toString()),
                )
            }
            load()
        }
    }

    fun requestRename(id: Long) = _uiState.update { it.copy(renamePlaylistId = id) }
    fun dismissRename() = _uiState.update { it.copy(renamePlaylistId = null) }

    fun renamePlaylist(title: String) {
        val id = _uiState.value.renamePlaylistId ?: return
        if (title.isBlank()) return
        viewModelScope.launch {
            playlistRepository.rename(id, title)
            _uiState.update { it.copy(renamePlaylistId = null, toastMessage = appText(com.lastwave.app.R.string.ui_playlist_renamed_839cfc)) }
            load()
        }
    }

    fun makeLocal(id: Long) {
        viewModelScope.launch {
            val saved = try {
                ytMusicLibraryManager.makeLocal(id)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (e: Exception) {
                android.util.Log.e("PlaylistViewModel", "Make-local failed for $id", e)
                null
            }
            _uiState.update {
                it.copy(
                    toastMessage = if (saved != null) "${saved.title} is now available locally" else appText(com.lastwave.app.R.string.ui_couldn_t_make_playlist_local_76c0c2),
                    detailPlaylist = saved ?: it.detailPlaylist,
                )
            }
            if (saved != null) load(justGeneratedId = saved.id)
        }
    }

    fun setCustomCover(id: Long, uri: String?) {
        viewModelScope.launch {
            playlistRepository.setCustomCover(id, uri)
            _uiState.update {
                it.copy(toastMessage = if (uri.isNullOrBlank()) appText(com.lastwave.app.R.string.ui_using_automatic_playlist_cover_0a9fbb) else appText(com.lastwave.app.R.string.ui_playlist_cover_updated_002f60))
            }
            load()
        }
    }

    fun togglePinned(id: Long) {
        val playlist = _uiState.value.playlists.firstOrNull { it.id == id }
            ?: _uiState.value.detailPlaylist?.takeIf { it.id == id }
            ?: return
        viewModelScope.launch {
            val willBePinned = !playlist.isPinned
            if (id < 0L) {
                ytMusicLibraryManager.togglePinned(id, willBePinned)
            } else {
                playlistRepository.setPinned(id, willBePinned)
            }
            _uiState.update { current ->
                val updatedPlaylists = current.playlists.map { if (it.id == id) it.copy(isPinned = willBePinned) else it }
                val sorted = sortPlaylists(updatedPlaylists, current.sortMode)
                current.copy(
                    playlists = sorted,
                    detailPlaylist = if (current.detailPlaylist?.id == id) current.detailPlaylist?.copy(isPinned = willBePinned) else current.detailPlaylist,
                    toastMessage = if (willBePinned) appText(com.lastwave.app.R.string.ui_playlist_pinned_to_top_068d4e) else appText(com.lastwave.app.R.string.ui_playlist_unpinned_606ba5),
                )
            }
            if (id >= 0L) load()
        }
    }

    fun removeTrack(playlistId: Long, index: Int) {
        val before = _uiState.value.detailPlaylist?.takeIf { it.id == playlistId }
        if (playlistId < 0L && before != null && index in before.tracks.indices) {
            _uiState.update { current ->
                current.copy(
                    detailPlaylist = before.copy(
                        tracks = before.tracks.filterIndexed { trackIndex, _ -> trackIndex != index },
                        remoteTrackCount = before.remoteTrackCount?.let { maxOf(0, it - 1) },
                    ),
                )
            }
        }
        viewModelScope.launch {
            val updated = if (playlistId < 0L) {
                ytMusicLibraryManager.removeTrack(playlistId, index)
            } else {
                playlistRepository.removeTrack(playlistId, index)
            }
            _uiState.update { current ->
                val removed = before != null && updated != null && updated.tracks.size < before.tracks.size
                current.copy(
                    detailPlaylist = if (current.detailPlaylist?.id == playlistId) updated ?: before else current.detailPlaylist,
                    toastMessage = if (removed || playlistId >= 0L) appText(com.lastwave.app.R.string.ui_song_removed_from_playlist_e05c9f) else appText(com.lastwave.app.R.string.ui_couldn_t_remove_song_85f483),
                )
            }
            if (playlistId >= 0L) {
                load()
                localMusicLibrary.cleanupOrphansAsync()
            }
        }
    }

    /**
     * Permanent drag-reorder for local playlists. Updates the open detail
     * immediately (so the row stays under the finger) then persists the new
     * order to Room. YouTube-only playlists have no remote move API, so they
     * stay locked.
     */
    fun moveTrack(playlistId: Long, fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return
        if (playlistId < 0L) {
            _uiState.update { it.copy(toastMessage = appText(com.lastwave.app.R.string.ui_reorder_works_for_local_playlists_c73762)) }
            return
        }
        val current = _uiState.value.detailPlaylist?.takeIf { it.id == playlistId } ?: return
        if (fromIndex !in current.tracks.indices || toIndex !in current.tracks.indices) return
        val reordered = current.tracks.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }
        trackMoveVersions[playlistId] = (trackMoveVersions[playlistId] ?: 0L) + 1L
        pendingTrackMoves[playlistId] = (pendingTrackMoves[playlistId] ?: 0) + 1
        _uiState.update { state ->
            state.copy(
                detailPlaylist = current.copy(tracks = reordered),
                playlists = state.playlists.map { playlist ->
                    if (playlist.id == playlistId) playlist.copy(tracks = reordered) else playlist
                },
            )
        }
        viewModelScope.launch {
            withContext(NonCancellable) {
                var failed = false
                try {
                    val persisted = playlistRepository.moveTrack(playlistId, fromIndex, toIndex) ?: return@withContext
                    _uiState.update { state ->
                        state.copy(
                            // Earlier crossed-row writes must not roll back a newer drag position.
                            detailPlaylist = if (state.detailPlaylist?.id == playlistId && state.detailPlaylist.tracks == reordered) {
                                persisted
                            } else state.detailPlaylist,
                            playlists = state.playlists.map { playlist ->
                                if (playlist.id == playlistId && playlist.tracks == reordered) persisted else playlist
                            },
                        )
                    }
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    failed = true
                    _uiState.update { it.copy(toastMessage = appText(com.lastwave.app.R.string.ui_couldn_t_save_new_order_f6f221)) }
                } finally {
                    val pending = (pendingTrackMoves[playlistId] ?: 1) - 1
                    if (pending == 0) pendingTrackMoves.remove(playlistId) else pendingTrackMoves[playlistId] = pending
                    if (failed && pending == 0) loadDetail(playlistId)
                }
            }
        }
    }

    fun requestDelete(id: Long) = _uiState.update { it.copy(deleteConfirmForPlaylistId = id) }
    fun dismissDeleteConfirm() = _uiState.update { it.copy(deleteConfirmForPlaylistId = null) }

    fun confirmDelete() {
        val id = _uiState.value.deleteConfirmForPlaylistId ?: return
        viewModelScope.launch {
            val deleted = withContext(NonCancellable) { deletePlaylist(id) }
            if (id >= 0L) localMusicLibrary.cleanupOrphansAsync()

            if (deleted) {
                _uiState.update { state ->
                    state.copy(
                        deleteConfirmForPlaylistId = null,
                        playlists = state.playlists.filterNot { it.id == id },
                        detailPlaylist = state.detailPlaylist?.takeUnless { it.id == id },
                        toastMessage = appText(com.lastwave.app.R.string.ui_playlist_deleted_3cd30e),
                    )
                }
                if (id >= 0L) load()
            } else {
                _uiState.update {
                    it.copy(
                        deleteConfirmForPlaylistId = null,
                        toastMessage = appText(com.lastwave.app.R.string.ui_couldn_t_delete_playlist_try_again_8bb9df),
                    )
                }
            }
        }
    }

    /**
     * Local playlists are deleted from Room (their YouTube mirror follows via
     * the sync's orphan sweep); connected-account playlists (negative ids) via
     * the library manager. Returns whether it is gone.
     *
     * Callers run this under NonCancellable: the detail screen calls
     * confirmDelete() and pops itself at once, clearing this ViewModel, while
     * the delete still waits on the repository's startup sync and the network.
     */
    private suspend fun deletePlaylist(id: Long): Boolean = try {
        if (id < 0L) ytMusicLibraryManager.deleteLibraryPlaylist(id) else playlistRepository.delete(id).let { true }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Exception) {
        android.util.Log.e("PlaylistViewModel", "Couldn't delete playlist $id", error)
        false
    }

    fun deleteMultiple(ids: Set<Long>) {
        viewModelScope.launch {
            val deletedIds = withContext(NonCancellable) { ids.filterTo(mutableSetOf()) { deletePlaylist(it) } }
            val failed = ids.size - deletedIds.size
            localMusicLibrary.cleanupOrphansAsync()
            _uiState.update { state ->
                state.copy(
                    playlists = state.playlists.filterNot { it.id in deletedIds },
                    detailPlaylist = state.detailPlaylist?.takeUnless { it.id in deletedIds },
                    toastMessage = when {
                        failed > 0 -> appText(com.lastwave.app.R.string.ui_couldn_t_delete_1_s_playlist_2_s_try_again_1a0177, (failed).toString(), (if (failed == 1) "" else "s").toString())
                        deletedIds.isNotEmpty() -> appText(com.lastwave.app.R.string.ui_deleted_1_s_playlist_2_s_7a0f68, (deletedIds.size).toString(), (if (deletedIds.size == 1) "" else "s").toString())
                        else -> state.toastMessage
                    },
                )
            }
            load()
        }
    }

    fun openExportSheet(id: Long) = _uiState.update { it.copy(exportSheetForPlaylistId = id) }
    fun dismissExportSheet() = _uiState.update { it.copy(exportSheetForPlaylistId = null) }

    fun exportSave(id: Long, format: ExportFormat) {
        val playlist = _uiState.value.playlists.firstOrNull { it.id == id } ?: return
        val filename = exportFilename(playlist, format)
        val content = exportContent(playlist, format)
        fileExportHelper.saveToDocuments(filename, content)
        _uiState.update { it.copy(exportSheetForPlaylistId = null, toastMessage = appText(com.lastwave.app.R.string.ui_saved_1_s_52af3b, (filename).toString())) }
    }

    fun exportShare(id: Long, format: ExportFormat) {
        val playlist = _uiState.value.playlists.firstOrNull { it.id == id } ?: return
        val filename = exportFilename(playlist, format)
        val content = exportContent(playlist, format)
        val mime = if (format == ExportFormat.CSV) "text/csv" else "audio/x-mpegurl"
        fileExportHelper.shareFile(filename, content, mime)
        _uiState.update { it.copy(exportSheetForPlaylistId = null) }
    }

    private fun exportFilename(playlist: SavedPlaylist, format: ExportFormat): String {
        val safeTitle = fileExportHelper.sanitizeFilename(playlist.title)
        return when (format) {
            ExportFormat.CSV -> "$safeTitle.csv"
            ExportFormat.M3U -> "$safeTitle(${PlaylistExportFormat.templateLabelFor(playlist.mode)}).m3u"
        }
    }

    private fun exportContent(playlist: SavedPlaylist, format: ExportFormat): String = when (format) {
        ExportFormat.CSV -> PlaylistExportFormat.toCsv(playlist.tracks)
        ExportFormat.M3U -> PlaylistExportFormat.toM3u(playlist.title, playlist.tracks)
    }

    fun dismissToast() = _uiState.update { it.copy(toastMessage = null) }

    /** Queues a download for every playlist track that is not already on
     *  disk in Music/LastWave. Already-downloaded (or already-queued)
     *  tracks are skipped so tapping this twice never duplicates files. */
    fun downloadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            var playlist = _uiState.value.playlists.firstOrNull { it.id == playlistId }
                ?: _uiState.value.detailPlaylist?.takeIf { it.id == playlistId }
                ?: runCatching { playlistRepository.getById(playlistId) }.getOrNull()
            if (playlist == null && playlistId < 0L) {
                playlist = runCatching { ytMusicLibraryManager.loadDetail(playlistId) }.getOrNull()
            }
            if (playlist == null) {
                _uiState.update { it.copy(toastMessage = appText(com.lastwave.app.R.string.ui_playlist_could_not_be_loaded_14a6ac)) }
                return@launch
            }
            var tracks = playlist.tracks
            if (tracks.isEmpty() && playlist.isYouTubeOnly) {
                val refreshed = runCatching { ytMusicLibraryManager.loadDetail(playlistId) }.getOrNull()
                if (refreshed != null && refreshed.tracks.isNotEmpty()) {
                    tracks = refreshed.tracks
                    _uiState.update { current ->
                        current.copy(
                            detailPlaylist = if (current.detailPlaylist?.id == playlistId) refreshed else current.detailPlaylist,
                            playlists = current.playlists.map { existing ->
                                if (existing.id == refreshed.id) refreshed else existing
                            },
                        )
                    }
                }
            }
            if (tracks.isEmpty()) {
                _uiState.update { it.copy(toastMessage = appText(com.lastwave.app.R.string.ui_no_songs_to_download_in_this_playlist_8f6cf6)) }
                return@launch
            }
            var queued = 0
            var skipped = 0
            for (track in tracks) {
                // Imported files are already on the device.
                if (track.name.isBlank() || com.lastwave.app.data.generate.isLocalAudioPath(track.url)) {
                    skipped++
                    continue
                }
                if (trackDownloadManager.isDownloading(track.name, track.artist)) {
                    skipped++
                    continue
                }
                val alreadyDownloaded = runCatching {
                    trackDownloadManager.isTrackDownloaded(track.name, track.artist)
                }.getOrDefault(false)
                if (alreadyDownloaded) {
                    skipped++
                    continue
                }
                trackDownloadManager.downloadTrack(track.name, track.artist, track.album, track.artworkUrl)
                queued++
            }
            _uiState.update {
                it.copy(
                    toastMessage = when {
                        queued > 0 && skipped > 0 -> appText(com.lastwave.app.R.string.ui_downloading_1_s_songs_2_s_already_saved_2b71a6, (queued).toString(), (skipped).toString())
                        queued > 0 -> if (queued == 1) appText(com.lastwave.app.R.string.ui_downloading_1_song_75e402) else appText(com.lastwave.app.R.string.ui_downloading_1_s_songs_f16c02, (queued).toString())
                        else -> appText(com.lastwave.app.R.string.ui_all_songs_already_downloaded_0f7017)
                    },
                )
            }
            if (queued > 0) {
                routeNavigator.navigateTo(com.lastwave.app.ui.navigation.Screen.Downloads.route)
            }
        }
    }

    fun cancelPlaylistDownloads(playlistId: Long) {
        viewModelScope.launch {
            val playlist = _uiState.value.playlists.firstOrNull { it.id == playlistId }
                ?: _uiState.value.detailPlaylist?.takeIf { it.id == playlistId }
                ?: runCatching { playlistRepository.getById(playlistId) }.getOrNull()
            val tracks = playlist?.tracks.orEmpty()
            var cancelled = 0
            for (track in tracks) {
                if (trackDownloadManager.isDownloading(track.name, track.artist)) {
                    trackDownloadManager.cancelDownload(track.name, track.artist)
                    cancelled++
                }
            }
            _uiState.update {
                it.copy(
                    toastMessage = if (cancelled > 0) appText(com.lastwave.app.R.string.ui_cancelled_1_s_downloads_442da3, (cancelled).toString()) else appText(com.lastwave.app.R.string.ui_no_active_downloads_in_playlist_77d454),
                )
            }
        }
    }

    /** Port of §4.2's "Generate Fresh" — re-runs the same mode with the
     *  same inputs and saves a brand-new playlist inspired from the original. */
    fun regenerate(id: Long, onRegenerated: ((Long) -> Unit)? = null) {
        if (_uiState.value.regeneratingId != null) return
        _uiState.update {
            it.copy(regeneratingId = id, toastMessage = appText(com.lastwave.app.R.string.ui_regenerating_inspired_mix_213fde))
        }
        viewModelScope.launch {
            val playlist = _uiState.value.playlists.firstOrNull { it.id == id }
                ?: _uiState.value.detailPlaylist?.takeIf { it.id == id }
                ?: try { playlistRepository.getById(id) } catch (_: Exception) { null }
            if (playlist == null) {
                _uiState.update {
                    it.copy(regeneratingId = null, toastMessage = appText(com.lastwave.app.R.string.ui_playlist_could_not_be_loaded_14a6ac))
                }
                return@launch
            }

            try {
                val targetCount = (30..35).random()
                val candidateCount = (targetCount + 15).coerceAtMost(50)
                val candidates = if (playlist.tracks.isNotEmpty()) {
                    // fetchTasteMixForPlaylist already dedupes, filters and
                    // caps artists itself (relaxing the cap to protect the
                    // 20+ floor) — re-prechecking here would re-shrink the
                    // result, so the mix is taken as-is.
                    generateRepository.fetchTasteMixForPlaylist(playlist.tracks, candidateCount)
                } else {
                    val raw = when (playlist.mode) {
                        "top", "library" -> generateRepository.fetchTopTracks(candidateCount, appText(com.lastwave.app.R.string.fr_period_all))
                        "recent" -> generateRepository.fetchRecentTracks(candidateCount)
                        "recommendations" -> generateRepository.fetchRecommendations(targetCount)
                        else -> generateRepository.fetchMix(candidateCount)
                    }
                    generateRepository.precheck(raw).ifEmpty {
                        generateRepository.deduplicate(raw)
                    }
                }
                val finalTracks = generateRepository.preferPlaylistFreshness(
                    tracks = candidates,
                    limit = targetCount,
                    savedKeys = generateRepository.savedPlaylistTrackKeys(),
                )

                if (finalTracks.isEmpty()) {
                    throw IllegalStateException("No tracks found to mix for this playlist.")
                }

                val existingTitles = playlistRepository.titles().toSet()
                val baseTitle = if (playlist.title.endsWith(" Mix", ignoreCase = true) || playlist.title.contains("Inspired", ignoreCase = true)) {
                    playlist.title
                } else {
                    "${playlist.title} Mix"
                }
                var title = baseTitle
                var counter = 2
                while (title in existingTitles) {
                    title = "$baseTitle $counter"
                    counter++
                }

                val subtitle = appText(com.lastwave.app.R.string.ui_inspired_by_1_s_a29a72, (playlist.title).toString())
                val saved = playlistRepository.save(title, subtitle, playlist.mode, finalTracks)
                _uiState.update { it.copy(toastMessage = appText(com.lastwave.app.R.string.ui_created_1_s_2_s_tracks_ca8327, (title).toString(), (finalTracks.size).toString())) }
                load(justGeneratedId = saved.id)
                onRegenerated?.invoke(saved.id)
            } catch (error: CancellationException) {
                throw error
            } catch (e: Exception) {
                _uiState.update { it.copy(toastMessage = e.message ?: appText(com.lastwave.app.R.string.ui_couldn_t_regenerate_playlist_55c400)) }
            } finally {
                _uiState.update { current ->
                    if (current.regeneratingId == id) current.copy(regeneratingId = null) else current
                }
            }
        }
    }

    fun deleteScrobble(trackName: String, artistName: String) {
        viewModelScope.launch {
            when (val result = authRepository.deleteScrobble(trackName, artistName, timestampMillis = null)) {
                is AuthRepository.DeleteScrobbleResult.Success -> {
                    _uiState.update { it.copy(toastMessage = appText(com.lastwave.app.R.string.ui_scrobble_deleted_295cbc)) }
                }
                is AuthRepository.DeleteScrobbleResult.AuthorizationRequired -> {
                    _uiState.update { it.copy(deleteScrobbleAuthRequired = true) }
                }
                is AuthRepository.DeleteScrobbleResult.NoTimestamp -> {
                    _uiState.update { it.copy(toastMessage = appText(com.lastwave.app.R.string.ui_cannot_delete_scrobble_has_no_timestamp_9e5d0e)) }
                }
                is AuthRepository.DeleteScrobbleResult.Failed -> {
                    _uiState.update { it.copy(toastMessage = result.message) }
                }
            }
        }
    }

    fun dismissDeleteScrobbleAuthRequired() = _uiState.update { it.copy(deleteScrobbleAuthRequired = false) }

    fun refreshArtwork(name: String, artist: String) {
        viewModelScope.launch {
            artworkRepository.forceRefresh(name, artist)
            load()
        }
    }
}
