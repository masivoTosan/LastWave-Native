package com.lastwave.app.ui.newreleases

import com.lastwave.app.util.appText

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lastwave.app.data.music.YouTubeMusicTrack
import com.lastwave.app.data.newreleases.NewReleasesRepository
import com.lastwave.app.playback.MusicPlayer
import com.lastwave.app.playback.PlayableTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class NewReleasesUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val tracks: List<YouTubeMusicTrack> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
)

@HiltViewModel
class NewReleasesViewModel @Inject constructor(
    private val repository: NewReleasesRepository,
    private val musicPlayer: MusicPlayer,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewReleasesUiState())
    val uiState: StateFlow<NewReleasesUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadInitial()
    }

    fun loadInitial() = reload(refreshing = false)

    fun refresh() = reload(refreshing = true)

    private fun reload(refreshing: Boolean) {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = it.tracks.isEmpty(), isRefreshing = refreshing,
            isLoadingMore = false, error = null, endReached = false) }
        loadJob = viewModelScope.launch {
            try {
                val tracks = repository.fetchInitialBatch()
                _uiState.update { it.copy(isLoading = false, isRefreshing = false,
                    tracks = tracks, endReached = !repository.hasMore,
                    error = if (tracks.isEmpty()) appText(com.lastwave.app.R.string.ui_no_new_releases_available_right_now_ba89a8) else null) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false,
                    error = error.message ?: appText(com.lastwave.app.R.string.ui_couldn_t_load_new_releases_tap_retry_d67bae)) }
            }
        }
    }

    fun loadMore() {
        val current = _uiState.value
        if (loadJob?.isActive == true || current.tracks.isEmpty() || current.endReached) return
        _uiState.update { it.copy(isLoadingMore = true, error = null) }
        loadJob = viewModelScope.launch {
            try {
                val more = repository.fetchNextBatch()
                _uiState.update { it.copy(isLoadingMore = false,
                    endReached = !repository.hasMore,
                    tracks = (it.tracks + more).distinctBy { track -> track.videoId }) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoadingMore = false,
                    error = error.message ?: appText(com.lastwave.app.R.string.ui_couldn_t_load_more_releases_tap_retry_4daa6f)) }
            }
        }
    }

    fun playTrack(index: Int) {
        val tracks = _uiState.value.tracks
        if (tracks.isEmpty() || index !in tracks.indices) return
        musicPlayer.playQueue(
            tracks = tracks.map { it.toPlayableTrack() },
            startIndex = index,
            sourceLabel = appText(com.lastwave.app.R.string.ui_new_releases_8b2fa9),
        )
    }

    fun playAll() {
        val tracks = _uiState.value.tracks
        if (tracks.isEmpty()) return
        musicPlayer.playQueue(
            tracks = tracks.map { it.toPlayableTrack() },
            startIndex = 0,
            sourceLabel = appText(com.lastwave.app.R.string.ui_new_releases_8b2fa9),
        )
    }

    fun shuffle() {
        val tracks = _uiState.value.tracks
        if (tracks.isEmpty()) return
        musicPlayer.playQueue(
            tracks = tracks.shuffled().map { it.toPlayableTrack() },
            startIndex = 0,
            sourceLabel = appText(com.lastwave.app.R.string.ui_new_releases_8b2fa9),
        )
    }

    private fun YouTubeMusicTrack.toPlayableTrack(): PlayableTrack = PlayableTrack(
        title = title,
        artist = artist,
        album = album,
        artworkUrl = artworkUrl,
        videoId = videoId.takeIf(String::isNotBlank),
        durationMs = durationSeconds?.takeIf { it > 0 }?.times(1_000L),
    )
}
