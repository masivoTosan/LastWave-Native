package com.lastwave.app.ui.navigation

import com.lastwave.app.util.appText

/**
 * Route constants for the whole app. Declaring the full set up front — even
 * though only [Login] has a screen behind it yet — means NavGraph.kt's
 * shape doesn't change on every future module; each module just adds its
 * composable() block under the route that's already named here.
 */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object MainShell : Screen("main_shell")

    // Bottom-nav swipeable tabs (Home module wires these + the pager together)
    data object Home : Screen(appText(com.lastwave.app.R.string.feed_home))
    data object Create : Screen(appText(com.lastwave.app.R.string.common_create))
    data object Playlist : Screen(appText(com.lastwave.app.R.string.ui_playlist_cd95b4))

    // Pushed screens, slide-in from the right (matches PAGE_TRANSITIONS in nav.js)
    data object Discover : Screen(appText(com.lastwave.app.R.string.ui_discover_4827ea))
    data object Genres : Screen(appText(com.lastwave.app.R.string.ui_genres_96817a))
    data object ListeningStats : Screen("listening_stats")
    data object Search : Screen("search?q={q}") {
        fun createRoute(query: String = "") = if (query.isBlank()) appText(com.lastwave.app.R.string.search_cd) else "search?q=${encodeArg(query)}"
    }
    data object Settings : Screen(appText(com.lastwave.app.R.string.settings))
    data object Storage : Screen(appText(com.lastwave.app.R.string.settings_storage_title))
    data object Friends : Screen(appText(com.lastwave.app.R.string.ui_friends_c11d5e))
    data object Downloads : Screen(appText(com.lastwave.app.R.string.ui_downloads_a862c2))
    data object ProviderModules : Screen("provider_modules")
    data object HomeSections : Screen("home_sections")
    data object ExcludedSongs : Screen("excluded_songs")
    data object YouTubeImport : Screen("youtube_import")
    data object YouTubeLogin : Screen("youtube_login")
    data object SpotifyLogin : Screen("spotify_login")

    /** The session cookie pasted in by hand, without the browser sign-in. */
    data object SpotifyTokenImport : Screen("spotify_token_import")
    data object Jam : Screen("jam?code={code}") {
        fun createRoute(code: String? = null) = if (code.isNullOrBlank()) appText(com.lastwave.app.R.string.menu_quick_jam) else "jam?code=${encodeArg(code)}"
    }
    data object ExternalPlaylistImport : Screen("external_playlist_import")
    data object NewReleases : Screen("new_releases")
    data object Podcasts : Screen(appText(com.lastwave.app.R.string.podcasts_title))
    data object PodcastShow : Screen("podcast_show/{showId}") {
        fun createRoute(showId: String) = "podcast_show/${encodeArg(showId)}"
    }
    data object FeedPlaylistDetail : Screen("feed_playlist/{playlistId}") {
        fun createRoute(playlistId: String) = "feed_playlist/${encodeArg(playlistId)}"
    }
    data object PlaylistDetail : Screen("playlist_detail/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist_detail/$playlistId"
    }
    data object ArtistDetail : Screen("artist_detail/{artistName}?browseId={browseId}") {
        fun createRoute(artistName: String, browseId: String? = null): String {
            val encName = encodeArg(artistName)
            val encBrowseId = if (!browseId.isNullOrBlank()) encodeArg(browseId) else ""
            return "artist_detail/$encName?browseId=$encBrowseId"
        }
    }
    data object AlbumDetail : Screen("album_detail/{albumTitle}?artistName={artistName}&browseId={browseId}") {
        fun createRoute(albumTitle: String, artistName: String = "", browseId: String? = null): String {
            val encTitle = encodeArg(albumTitle)
            val encArtist = encodeArg(artistName)
            val encBrowseId = if (!browseId.isNullOrBlank()) encodeArg(browseId) else ""
            return "album_detail/$encTitle?artistName=$encArtist&browseId=$encBrowseId"
        }
    }
    data object FriendProfile : Screen("friend_profile/{username}?displayName={displayName}&avatarUrl={avatarUrl}") {
        fun createRoute(username: String, displayName: String? = null, avatarUrl: String? = null): String {
            val encName = encodeArg(username)
            val encDisplay = if (!displayName.isNullOrBlank()) encodeArg(displayName) else ""
            val encAvatar = if (!avatarUrl.isNullOrBlank()) encodeArg(avatarUrl) else ""
            return "friend_profile/$encName?displayName=$encDisplay&avatarUrl=$encAvatar"
        }
    }

    companion object {
        /**
         * Encodes one route argument (path segment or query value) so names
         * with `/`, `?`, `&` or `#` — e.g. "AC/DC", "Simon & Garfunkel",
         * "What If...?" — can never split the route into extra segments /
         * params (which crashed navigation or opened the wrong page).
         * Navigation decodes the value once on read, so this round-trips.
         */
        fun encodeArg(raw: String): String =
            android.net.Uri.encode(raw)
                ?.replace("/", "%2F")
                ?.replace("?", "%3F")
                ?.replace("&", "%26")
                ?.replace("#", "%23")
                .orEmpty()
    }
}

