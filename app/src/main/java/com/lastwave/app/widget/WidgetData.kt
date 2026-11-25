package com.lastwave.app.widget

import com.lastwave.app.util.appText

import android.content.Context

/**
 * From-scratch widget state, deliberately dependency-free.
 *
 * The old Glance widget kept two identical snapshot classes (small + large)
 * each doing Hilt/Theme lookups inside provideGlance; any throw there left
 * the widget stuck on glance_default_loading_layout forever. This file is
 * the only state the new classic RemoteViews widget needs: plain
 * SharedPreferences, same STORE name + keys as before so an app update
 * keeps the currently-showing track instead of resetting to empty.
 */
data class WidgetSnapshot(
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val sourceApp: String = "",
    val sourcePackage: String = "",
    val artPath: String? = null,
    val isPlaying: Boolean = false,
    val hasSession: Boolean = false,
    /** Last known playback position 0..1. Refreshed on every publish and
     *  live-driven by the ticker while playing. */
    val progress: Float = 0f,
) {
    companion object {
        internal const val STORE = "lastwave_widget_now_playing"

        fun read(context: Context): WidgetSnapshot {
            val prefs = runCatching {
                context.getSharedPreferences(STORE, Context.MODE_PRIVATE)
            }.getOrNull() ?: return WidgetSnapshot()
            return WidgetSnapshot(
                title = prefs.getString(appText(com.lastwave.app.R.string.local_sort_title), "").orEmpty(),
                artist = prefs.getString(appText(com.lastwave.app.R.string.pd_sort_artist), "").orEmpty(),
                album = prefs.getString(appText(com.lastwave.app.R.string.art_type_album), "").orEmpty(),
                sourceApp = prefs.getString("source_app", "").orEmpty(),
                sourcePackage = prefs.getString("source_package", "").orEmpty(),
                artPath = prefs.getString("art_path", null),
                isPlaying = prefs.getBoolean("is_playing", false),
                hasSession = prefs.getBoolean("has_session", false),
                progress = prefs.getFloat("progress", 0f).coerceIn(0f, 1f),
            )
        }

        fun write(context: Context, value: WidgetSnapshot) {
            runCatching {
                context.getSharedPreferences(STORE, Context.MODE_PRIVATE).edit()
                    .putString(appText(com.lastwave.app.R.string.local_sort_title), value.title)
                    .putString(appText(com.lastwave.app.R.string.pd_sort_artist), value.artist)
                    .putString(appText(com.lastwave.app.R.string.art_type_album), value.album)
                    .putString("source_app", value.sourceApp)
                    .putString("source_package", value.sourcePackage)
                    .putString("art_path", value.artPath)
                    .putBoolean("is_playing", value.isPlaying)
                    .putBoolean("has_session", value.hasSession)
                    .putFloat("progress", value.progress.coerceIn(0f, 1f))
                    .apply()
            }
        }
    }
}

/**
 * Compatibility name kept so existing callers (SettingsViewModel
 * diagnostics) compile unchanged. One widget, one snapshot.
 */
typealias NowPlayingWidgetSnapshot = WidgetSnapshot
