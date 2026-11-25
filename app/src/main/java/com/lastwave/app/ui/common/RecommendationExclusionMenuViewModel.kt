package com.lastwave.app.ui.common

import com.lastwave.app.util.appText

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.lastwave.app.data.discover.DiscoverRepository
import com.lastwave.app.data.music.InnerTubeMusicApi
import com.lastwave.app.data.music.markNotInterested
import com.lastwave.app.data.ytmusic.YtMusicAuthManager
import com.lastwave.app.playback.PlayableTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class RecommendationExclusionMenuViewModel @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    private val innerTube: InnerTubeMusicApi,
    private val auth: YtMusicAuthManager,
    private val applicationScope: CoroutineScope,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    fun exclude(track: PlayableTrack) {
        val account = auth.connection.value
        applicationScope.launch(Dispatchers.IO) {
            var saved = false
            val message = try {
                saved = discoverRepository.excludeFromRecommendations(track.title, track.artist)
                when {
                    !saved -> appText(com.lastwave.app.R.string.ui_could_not_exclude_this_song_please_try_again_ef2c60)
                    !account.isConnected || track.videoId.isNullOrBlank() -> appText(com.lastwave.app.R.string.ui_song_excluded_from_recommendations_f0969b)
                    innerTube.markNotInterested(track.videoId, account) -> appText(com.lastwave.app.R.string.ui_song_excluded_here_and_on_youtube_music_21c886)
                    else -> appText(com.lastwave.app.R.string.ui_song_excluded_here_youtube_music_could_not_confi_af4481)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                android.util.Log.w("RecommendationExclusion", "Recommendation feedback failed", error)
                if (saved) appText(com.lastwave.app.R.string.ui_song_excluded_here_could_not_update_youtube_musi_ed4f44)
                else appText(com.lastwave.app.R.string.ui_could_not_exclude_this_song_please_try_again_ef2c60)
            }
            withContext(Dispatchers.Main) { Toast.makeText(context, message, Toast.LENGTH_LONG).show() }
        }
    }
}
