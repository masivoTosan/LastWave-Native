package com.lastwave.app.ui.settings

import com.lastwave.app.util.appText

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.SpeakerPhone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Categorization of different setting entry interaction types.
 */
enum class EntryType {
    ACTION,
    TOGGLE,
    SLIDER,
    THEME_SELECTOR,
    ACCENT_PICKER,
    CUSTOM,
}

/**
 * A searchable settings entry indexed for fuzzy search.
 *
 * @property id Unique stable identifier for this setting (e.g. "audio.streaming_quality")
 * @property title Primary human-readable title of the setting
 * @property subtitle Descriptive explanation or default state of the setting
 * @property keywords List of curated keywords, synonyms, and technical terms to match against
 * @property icon The icon vector representing this setting
 * @property iconContainer Composable color provider for the icon background
 * @property iconTint Composable color provider for the icon foreground
 * @property parentTab The [SettingsTab] where this setting is located
 * @property section The section header within the parent tab (e.g. "Output & Loudness")
 * @property type The interaction type of the setting
 */
data class SettingsEntry(
    val id: String,
    val title: String,
    val subtitle: String,
    val keywords: List<String>,
    val icon: ImageVector,
    val iconContainer: @Composable () -> Color,
    val iconTint: @Composable () -> Color,
    val parentTab: SettingsTab,
    val section: String,
    val type: EntryType = EntryType.ACTION,
)

/**
 * Static registry of all searchable settings entries across all tabs.
 */
object SettingsSearchIndex {

    val allEntries: List<SettingsEntry> get() {
        return
        buildList {
            // ==========================================
            // TAB: AUDIO & PLAYBACK
            // ==========================================
            add(
                SettingsEntry(
                    id = "audio.wifi_streaming_quality",
                    title = appText(com.lastwave.app.R.string.ui_wi_fi_streaming_quality_2f111e),
                    subtitle = appText(com.lastwave.app.R.string.ui_choose_audio_quality_while_connected_to_wi_fi_2e0b85),
                    keywords = listOf(
                        "quality", "bitrate", "flac", "lossless", "hires", "hi-res", "dolby", "atmos",
                        "mp3", "aac", "opus", "streaming", "audio quality", "sound quality", "192khz",
                        "24-bit", "stream", "format", "wifi", "wireless"
                    ),
                    icon = Icons.Filled.HighQuality,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.cellular_streaming_quality",
                    title = appText(com.lastwave.app.R.string.ui_cellular_streaming_quality_a24de0),
                    subtitle = appText(com.lastwave.app.R.string.ui_choose_audio_quality_while_using_mobile_data_d9fa3f),
                    keywords = listOf("quality", "bitrate", "streaming", "cellular", "mobile data", "data usage", "audio", "auto", "adaptive"),
                    icon = Icons.Filled.HighQuality,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.download_quality",
                    title = appText(com.lastwave.app.R.string.settings_download_quality),
                    subtitle = appText(com.lastwave.app.R.string.ui_offline_download_format_bitrate_lossless_flac_or_e00229),
                    keywords = listOf(
                        "download", "offline", "storage", "bitrate", "flac", "lossless", "cache",
                        "download quality", "save songs", "music downloads"
                    ),
                    icon = Icons.Filled.CloudDownload,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.auto_upgrade_hi_res",
                    title = appText(com.lastwave.app.R.string.ui_playback_start_c39d34),
                    subtitle = appText(com.lastwave.app.R.string.ui_fast_start_best_quality_first_or_never_upgrade_8e582c),
                    keywords = listOf(
                        "auto upgrade", "hi-res", "hires", "lossless", "flac", "opus",
                        "upgrade", "streaming", "quality", "background upgrade", "start", "direct", "best quality"
                    ),
                    icon = Icons.Filled.HighQuality,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.bit_perfect",
                    title = appText(com.lastwave.app.R.string.ui_bit_perfect_mode_552e9e),
                    subtitle = appText(com.lastwave.app.R.string.ui_bypass_android_mixer_dsp_stream_bit_exact_audio__d66772),
                    keywords = listOf(
                        "bit-perfect", "bitperfect", "direct output", "bypass mixer", "dac", "usb dac",
                        "audiophile", "lossless", "exclusive", "sample rate match", "native playback",
                        "dsp bypass", "hi-res direct"
                    ),
                    icon = Icons.Filled.Tune,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.automix",
                    title = appText(com.lastwave.app.R.string.ui_automix_b42afb),
                    subtitle = appText(com.lastwave.app.R.string.ui_beat_matched_key_locked_seamless_dual_deck_mixin_44e0f3),
                    keywords = listOf(
                        "autodj", "automix", "camelot", "beat match", "dj blend",
                        "transition", "crossfade", "phrase loop"
                    ),
                    icon = Icons.Filled.Bolt,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.crossfade",
                    title = appText(com.lastwave.app.R.string.sig_crossfade),
                    subtitle = appText(com.lastwave.app.R.string.ui_blend_the_end_of_a_track_smoothly_into_the_next__18f697),
                    keywords = listOf(
                        "crossfade", "fade", "transition", "overlap", "gapless", "mix tracks",
                        "smooth playback", "dj fade"
                    ),
                    icon = Icons.Filled.GraphicEq,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.crossfade_duration",
                    title = appText(com.lastwave.app.R.string.ui_crossfade_duration_820ce1),
                    subtitle = appText(com.lastwave.app.R.string.ui_adjust_crossfade_duration_slider_in_seconds_betw_e91c67),
                    keywords = listOf(
                        "crossfade seconds", "crossfade duration", "fade length", "crossfade time",
                        "slider", "seconds", "transition time"
                    ),
                    icon = Icons.Filled.Timer,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.SLIDER,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.keep_lyrics_screen_awake",
                    title = appText(com.lastwave.app.R.string.settings_keep_lyrics_screen_awake),
                    subtitle = appText(com.lastwave.app.R.string.settings_keep_lyrics_screen_awake_desc),
                    keywords = listOf(
                        "keep screen awake", "keep screen on", "lyrics sleep", "screen timeout",
                        "display awake", "lyrics screen"
                    ),
                    icon = Icons.Filled.Lyrics,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.stop_on_recents",
                    title = appText(com.lastwave.app.R.string.settings_stop_on_recents),
                    subtitle = appText(com.lastwave.app.R.string.ui_stop_music_when_lastwave_is_removed_from_recents_873a05),
                    keywords = listOf(
                        "recents", "swipe", "swipe away", "stop playback", "background playback",
                        "continue playing", "task removed", "kill app", "close app"
                    ),
                    icon = Icons.Filled.StopCircle,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.pause_on_zero_volume",
                    title = appText(com.lastwave.app.R.string.settings_pause_on_zero_volume),
                    subtitle = appText(com.lastwave.app.R.string.settings_pause_on_zero_volume_desc),
                    keywords = listOf(
                        "pause", "volume", "zero volume", "mute", "silent",
                        "auto pause", "media volume", "volume 0"
                    ),
                    icon = Icons.AutoMirrored.Rounded.VolumeOff,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.download_lyrics",
                    title = appText(com.lastwave.app.R.string.dl_lyrics_row),
                    subtitle = appText(com.lastwave.app.R.string.ui_save_lrc_companion_files_embed_lyrics_in_downloa_03bad2),
                    keywords = listOf(
                        "lyrics", "lrc", "download lyrics", "embed lyrics", "karaoke offline",
                        "save lyrics", "text", "song words"
                    ),
                    icon = Icons.Filled.Lyrics,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.battery_optimization",
                    title = appText(com.lastwave.app.R.string.settings_battery_title),
                    subtitle = appText(com.lastwave.app.R.string.ui_exempt_app_from_device_battery_restrictions_slee_69fc80),
                    keywords = listOf(
                        "battery", "background", "sleep", "sleeping apps", "device care", "power saving",
                        "keep alive", "doze", "stop playing", "playback killed"
                    ),
                    icon = Icons.Filled.Bolt,
                    iconContainer = { MaterialTheme.colorScheme.errorContainer },
                    iconTint = { MaterialTheme.colorScheme.onErrorContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.set_cat_audio),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.usb_exclusive",
                    title = appText(com.lastwave.app.R.string.set_usb_title),
                    subtitle = appText(com.lastwave.app.R.string.ui_direct_usbdevfs_communication_with_external_usb__2064dd),
                    keywords = listOf(
                        "usb", "dac", "exclusive", "usb exclusive", "usbdevfs", "external dac",
                        "hardware volume", "headphone amp", "dongle dac"
                    ),
                    icon = Icons.Filled.Usb,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.ui_output_loudness_7fd025),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.internal_direct",
                    title = appText(com.lastwave.app.R.string.settings_internal_direct),
                    subtitle = appText(com.lastwave.app.R.string.ui_bypass_the_android_mixer_on_the_speaker_jack_wit_954705),
                    keywords = listOf(
                        "direct", "mixer", "bypass", "hires direct", "uapp", "aaudio", "mmap",
                        "exclusive", "sortie directe", "interne", "contourner", "speaker", "jack",
                    ),
                    icon = Icons.Filled.SpeakerPhone,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.ui_output_loudness_7fd025),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.force_software_volume",
                    title = appText(com.lastwave.app.R.string.settings_force_sw_volume),
                    subtitle = appText(com.lastwave.app.R.string.ui_scale_volume_in_the_app_instead_of_the_dac_s_har_037823),
                    keywords = listOf(
                        "volume", "software volume", "hardware volume", "dac", "logiciel", "matériel",
                    ),
                    icon = Icons.Filled.VolumeUp,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.ui_output_loudness_7fd025),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.loudness_normalization",
                    title = appText(com.lastwave.app.R.string.set_loud_title),
                    subtitle = appText(com.lastwave.app.R.string.ui_replaygain_volume_leveling_track_14_lufs_album_d_1ca477),
                    keywords = listOf(
                        "loudness", "normalization", "volume", "replaygain", "lufs", "track volume",
                        "leveling", "equal volume", "album loudness", "gain"
                    ),
                    icon = Icons.Filled.VolumeUp,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.ui_output_loudness_7fd025),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.autoeq",
                    title = appText(com.lastwave.app.R.string.signal_label_autoeq),
                    subtitle = appText(com.lastwave.app.R.string.ui_headphone_correction_from_the_autoeq_database_pa_5e2e48),
                    keywords = listOf(
                        "autoeq", "auto eq", "headphone", "casque", "iem", "harman", "parametric",
                        "peq", "correction", "oratory1990", "crinacle", "target",
                    ),
                    icon = Icons.Filled.GraphicEq,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.ui_output_loudness_7fd025),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.equalizer",
                    title = appText(com.lastwave.app.R.string.sig_eq),
                    subtitle = appText(com.lastwave.app.R.string.ui_shape_sound_across_15_frequency_bands_with_custo_870d44),
                    keywords = listOf(
                        "equalizer", "eq", "15-band", "presets", "bass", "treble", "midrange",
                        "bass boost", "sound profile", "frequencies", "audio shape"
                    ),
                    icon = Icons.Filled.GraphicEq,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.ui_output_loudness_7fd025),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.system_audio_effects",
                    title = appText(com.lastwave.app.R.string.set_sys_fx_title),
                    subtitle = appText(com.lastwave.app.R.string.ui_allow_external_equalizers_dolby_atmos_wavelet_to_dc2666),
                    keywords = listOf(
                        "system audio effects", "wavelet", "dolby system", "external eq", "audio fx",
                        "dsp apps", "system equalizer", "sound enhancer"
                    ),
                    icon = Icons.Filled.VolumeUp,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.ui_output_loudness_7fd025),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "audio.studio_clarity",
                    title = appText(com.lastwave.app.R.string.settings_studio_clarity),
                    subtitle = appText(com.lastwave.app.R.string.ui_crystal_clear_open_sound_airy_detail_and_deep_cl_969e32),
                    keywords = listOf(
                        "clarity", "studio master", "master clarity", "separation", "detail", "crisp",
                        "highs", "vocal clarity", "soundstage", "dsp clarity"
                    ),
                    icon = Icons.Filled.Waves,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.AUDIO,
                    section = appText(com.lastwave.app.R.string.ui_output_loudness_7fd025),
                    type = EntryType.TOGGLE,
                )
            )

            // ==========================================
            // TAB: APPEARANCE & VISUALS
            // ==========================================
            add(
                SettingsEntry(
                    id = "appearance.theme_mode",
                    title = appText(com.lastwave.app.R.string.ui_theme_mode_bb6b0b),
                    subtitle = appText(com.lastwave.app.R.string.ui_select_system_default_light_theme_or_dark_theme_f2f32a),
                    keywords = listOf(
                        "theme", "dark mode", "light mode", "system theme", "night mode", "day mode",
                        "appearance", "color scheme", "style"
                    ),
                    icon = Icons.Filled.Contrast,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.THEME_SELECTOR,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.app_icon",
                    title = appText(com.lastwave.app.R.string.settings_section_app_icon),
                    subtitle = appText(com.lastwave.app.R.string.settings_app_icon_sub),
                    keywords = listOf(
                        "app icon", "logo", "desktop logo", "white logo", "black logo", "launcher", "icon", "theme"
                    ),
                    icon = Icons.Filled.Apps,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.lastwave_theme",
                    title = appText(com.lastwave.app.R.string.settings_lastwave_theme),
                    subtitle = appText(com.lastwave.app.R.string.ui_bespoke_logo_palette_with_electric_lime_and_obsi_786921),
                    keywords = listOf(
                        "lastwave theme", "brand theme", "special theme", "logo theme", "lime",
                        "teal", "obsidian", "electric lime", "iconic theme", "brand colors"
                    ),
                    icon = Icons.Filled.AutoAwesome,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.amoled",
                    title = appText(com.lastwave.app.R.string.ui_pure_black_amoled_a303f6),
                    subtitle = appText(com.lastwave.app.R.string.ui_pitch_black_surfaces_for_oled_and_amoled_screens_a73567),
                    keywords = listOf(
                        "amoled", "oled", "pure black", "pitch black", "true black", "battery saver",
                        "dark mode", "contrast"
                    ),
                    icon = Icons.Filled.Contrast,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.legacy_colors",
                    title = appText(com.lastwave.app.R.string.ui_use_legacy_colours_a2c942),
                    subtitle = appText(com.lastwave.app.R.string.settings_legacy_colors_sub),
                    keywords = listOf(
                        "legacy colors", "legacy colours", "material you background", "base colour", "original background",
                        "bright background", "solid background", "monet"
                    ),
                    icon = Icons.Filled.Palette,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.legacy_track_menu",
                    title = appText(com.lastwave.app.R.string.ui_use_classic_song_menu_7fdaea),
                    subtitle = appText(com.lastwave.app.R.string.settings_legacy_track_menu_sub),
                    keywords = listOf(
                        "classic song menu", "legacy track menu", "old song menu", "3 dots menu", "context menu", "track menu"
                    ),
                    icon = Icons.Filled.MoreVert,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.dynamic_color",
                    title = appText(com.lastwave.app.R.string.ui_dynamic_color_material_you_519ae0),
                    subtitle = appText(com.lastwave.app.R.string.ui_match_ui_accent_colors_to_wallpaper_and_system_p_402840),
                    keywords = listOf(
                        "dynamic color", "material you", "monet", "wallpaper color", "system colors",
                        "adaptive color", "android 12 color"
                    ),
                    icon = Icons.Filled.Palette,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.liquid_glass",
                    title = appText(com.lastwave.app.R.string.settings_liquid_glass),
                    subtitle = appText(com.lastwave.app.R.string.ui_dynamic_translucent_frosted_glass_backgrounds_an_0081f8),
                    keywords = listOf(
                        "liquid glass", "glassmorphism", "blur", "frosted glass", "translucent",
                        "acrylic", "transparency", "visual effect"
                    ),
                    icon = Icons.Filled.BubbleChart,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.md3_nav_bar",
                    title = appText(com.lastwave.app.R.string.settings_md3_nav_bar),
                    subtitle = appText(com.lastwave.app.R.string.ui_compact_floating_dock_with_a_label_beside_the_se_af0dbf),
                    keywords = listOf(
                        "md3", "material 3", "navigation bar", "bottom bar", "nav bar",
                        "dock", "floating dock", "full width nav"
                    ),
                    icon = Icons.Filled.Apps,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.dynamic_now_playing",
                    title = appText(com.lastwave.app.R.string.ui_dynamic_now_playing_screen_f464cd),
                    subtitle = appText(com.lastwave.app.R.string.ui_adaptive_color_background_on_the_now_playing_scr_f07a71),
                    keywords = listOf(
                        "dynamic now playing", "player screen", "adaptive background", "album art color",
                        "gradient player", "now playing theme", "cover art color"
                    ),
                    icon = Icons.Filled.Album,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.app_font",
                    title = appText(com.lastwave.app.R.string.ui_app_font_6a5c3e),
                    subtitle = appText(com.lastwave.app.R.string.ui_toggle_between_system_font_and_custom_typography_a247ea),
                    keywords = listOf(
                        "font", "typeface", "typography", "text style", "custom font", "app font",
                        "lettering", "outfit font"
                    ),
                    icon = Icons.Filled.TextFields,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.ui_haptics",
                    title = appText(com.lastwave.app.R.string.settings_ui_haptics),
                    subtitle = appText(com.lastwave.app.R.string.settings_ui_haptics_sub),
                    keywords = listOf(
                        "haptic", "haptics", "vibration", "vibrate", "touch feedback",
                        "tap feedback", "buzz", "tactile"
                    ),
                    icon = Icons.Filled.Vibration,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.home_sections",
                    title = appText(com.lastwave.app.R.string.ui_home_screen_sections_dde164),
                    subtitle = appText(com.lastwave.app.R.string.ui_reorder_show_or_hide_sections_displayed_on_your__812d2e),
                    keywords = listOf(
                        "home", "home sections", "feed", "customize home", "reorder home",
                        "layout", "hide sections", "shelves", "front page"
                    ),
                    icon = Icons.Filled.Dashboard,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.settings_section_appearance),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.accent_preset",
                    title = appText(com.lastwave.app.R.string.ui_accent_color_palette_cae508),
                    subtitle = appText(com.lastwave.app.R.string.ui_pick_presets_monochrome_palette_or_custom_color__0107e5),
                    keywords = listOf(
                        "accent", "color", "palette", "monochrome", "color wheel", "hex",
                        "custom color", "theme color", "tint"
                    ),
                    icon = Icons.Filled.Palette,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.ui_accent_color_2bef52),
                    type = EntryType.ACCENT_PICKER,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.lyrics_animation",
                    title = appText(com.lastwave.app.R.string.ui_lyrics_animation_style_e99055),
                    subtitle = appText(com.lastwave.app.R.string.ui_karaoke_glow_sliding_fade_bounce_or_modern_full__6ebffc),
                    keywords = listOf(
                        "lyrics animation", "karaoke", "lyrics style", "modern lyrics", "lyrics ui",
                        "smooth scroll", "glow lyrics", "lyrics presentation"
                    ),
                    icon = Icons.Filled.Lyrics,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.ui_experimental_features_fbb63a),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.wavy_seekbar",
                    title = appText(com.lastwave.app.R.string.settings_seekbar_style),
                    subtitle = appText(com.lastwave.app.R.string.ui_choose_between_default_wavy_and_waveform_slider__a22626),
                    keywords = listOf(
                        "wavy seekbar", "wave slider", "progress bar", "fluid seekbar", "waveform",
                        "scrubber", "timeline", "squiggly line", "slider style", "seekbar style"
                    ),
                    icon = Icons.Filled.Waves,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.ui_experimental_features_fbb63a),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.music_video",
                    title = appText(com.lastwave.app.R.string.settings_music_video),
                    subtitle = appText(com.lastwave.app.R.string.ui_watch_exact_sync_music_video_of_currently_playin_5fc2c9),
                    keywords = listOf(
                        "video", "music video", "watch video", "mv", "synced video", "youtube video",
                        "fullscreen video", "exact ms sync", "now playing video"
                    ),
                    icon = Icons.Filled.SmartDisplay,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.ui_experimental_features_fbb63a),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.rotating_background",
                    title = appText(com.lastwave.app.R.string.settings_rotating_background),
                    subtitle = appText(com.lastwave.app.R.string.ui_animate_fluid_rotating_artwork_in_player_and_lyr_68d60d),
                    keywords = listOf(
                        "rotating background", "rotating artwork", "fluid background", "static background",
                        "disable rotation", "android 11", "spinning artwork", "player background",
                        "lyrics background", "album rotation", "static artwork"
                    ),
                    icon = Icons.Filled.RestartAlt,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.ui_experimental_features_fbb63a),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.lyrics_rotating_background",
                    title = appText(com.lastwave.app.R.string.settings_lyrics_rotating_background),
                    subtitle = appText(com.lastwave.app.R.string.ui_animate_fluid_rotating_artwork_in_lyrics_view_di_20a453),
                    keywords = listOf(
                        "lyrics background motion", "lyrics rotating background", "lyrics fluid background",
                        "lyrics motion", "lyrics background"
                    ),
                    icon = Icons.Filled.RestartAlt,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.ui_experimental_features_fbb63a),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.fluid_beat_sync",
                    title = appText(com.lastwave.app.R.string.settings_fluid_beat_sync),
                    subtitle = appText(com.lastwave.app.R.string.settings_fluid_beat_sync_on),
                    keywords = listOf(
                        "beat synced fluid motion", "beat sync", "fluid motion", "beat pulse",
                        "fluid beat", "audio reactive", "fluid animation", "per beat",
                        "beat animation", "kick pulse", "music beat"
                    ),
                    icon = Icons.Filled.Waves,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.ui_experimental_features_fbb63a),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.lyrics_provider",
                    title = appText(com.lastwave.app.R.string.ly_source_title),
                    subtitle = appText(com.lastwave.app.R.string.ui_choose_preferred_synced_lyrics_provider_lrclib_m_ff91c8),
                    keywords = listOf(
                        "lyrics provider", "lrclib", "musixmatch", "synced lyrics source",
                        "lyrics engine", "lyrics fetcher"
                    ),
                    icon = Icons.Filled.Lyrics,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.ui_experimental_features_fbb63a),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.lyrics_offset",
                    title = appText(com.lastwave.app.R.string.ui_lyrics_sync_offset_26dba2),
                    subtitle = appText(com.lastwave.app.R.string.ui_adjust_timing_offset_in_milliseconds_to_match_ly_9a860c),
                    keywords = listOf(
                        "lyrics offset", "lyrics sync", "delay lyrics", "early lyrics", "ms offset",
                        "timing", "sync adjustment", "vocals match"
                    ),
                    icon = Icons.Filled.Timer,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.ui_experimental_features_fbb63a),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.lyrics_size",
                    title = appText(com.lastwave.app.R.string.ui_lyrics_text_size_200aed),
                    subtitle = appText(com.lastwave.app.R.string.ui_scale_and_resize_lyrics_font_size_across_the_pla_4885b2),
                    keywords = listOf(
                        "lyrics size", "lyrics font size", "font size", "text size", "resize lyrics",
                        "bigger lyrics", "smaller lyrics", "lyrics scale", "lyrics zoom"
                    ),
                    icon = Icons.Filled.FormatSize,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.ui_experimental_features_fbb63a),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.lyrics_autohide",
                    title = appText(com.lastwave.app.R.string.ui_hide_lyrics_buttons_0bfbb3),
                    subtitle = appText(com.lastwave.app.R.string.ui_idle_time_before_the_floating_lyrics_buttons_fad_cffd99),
                    keywords = listOf(
                        "hide lyrics buttons", "auto hide", "idle", "fade out", "lyrics controls",
                        "font size button", "fullscreen button", "never", "seconds", "timeout"
                    ),
                    icon = Icons.Filled.VisibilityOff,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.ui_experimental_features_fbb63a),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.translation_language",
                    title = appText(com.lastwave.app.R.string.ui_translation_language_15fff1),
                    subtitle = appText(com.lastwave.app.R.string.ui_language_the_lyrics_translate_button_turns_songs_23e69f),
                    keywords = listOf(
                        "translation language", "translate lyrics", "lyrics language", "romanization",
                        "romanize", "language of lyrics", "translate to", "lyrics translate"
                    ),
                    icon = Icons.Filled.Translate,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.ui_experimental_features_fbb63a),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.canvas_enabled",
                    title = appText(com.lastwave.app.R.string.ui_canvas_video_backgrounds_11ae9f),
                    subtitle = appText(com.lastwave.app.R.string.ui_looping_visual_canvas_videos_during_playback_lik_3ede51),
                    keywords = listOf(
                        "canvas", "video", "looping video", "spotify canvas", "video background",
                        "visualizer", "moving artwork"
                    ),
                    icon = Icons.Filled.SmartDisplay,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.details_canvas_source),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.canvas_full_bleed",
                    title = appText(com.lastwave.app.R.string.ui_canvas_full_bleed_15374a),
                    subtitle = appText(com.lastwave.app.R.string.ui_expand_canvas_video_to_fill_entire_screen_behind_06a904),
                    keywords = listOf(
                        "canvas full bleed", "full screen canvas", "edge to edge", "immersion",
                        "canvas size", "fit screen"
                    ),
                    icon = Icons.Filled.Visibility,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.details_canvas_source),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.canvas_gradient_blur",
                    title = appText(com.lastwave.app.R.string.ui_gradient_blur_on_canvas_308953),
                    subtitle = appText(com.lastwave.app.R.string.settings_canvas_gradient_blur_sub),
                    keywords = listOf(
                        "canvas gradient blur", "canvas blur", "gradient blur", "hero canvas",
                        "ambient blur", "canvas edge to edge"
                    ),
                    icon = Icons.Filled.AutoAwesome,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.details_canvas_source),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "appearance.canvas_cellular",
                    title = appText(com.lastwave.app.R.string.ui_canvas_on_cellular_data_d84c6d),
                    subtitle = appText(com.lastwave.app.R.string.ui_allow_canvas_video_streaming_over_mobile_and_cel_f34d8a),
                    keywords = listOf(
                        "canvas cellular", "mobile data", "canvas data saver", "lte", "5g",
                        "stream canvas mobile", "data usage"
                    ),
                    icon = Icons.Filled.CloudDownload,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.APPEARANCE,
                    section = appText(com.lastwave.app.R.string.details_canvas_source),
                    type = EntryType.TOGGLE,
                )
            )

            // ==========================================
            // TAB: YOUTUBE & SYNC
            // ==========================================
            add(
                SettingsEntry(
                    id = "youtube.account",
                    title = appText(com.lastwave.app.R.string.ui_youtube_account_connection_9e2425),
                    subtitle = appText(com.lastwave.app.R.string.ui_connect_or_manage_your_google_youtube_music_acco_24889b),
                    keywords = listOf(
                        "youtube", "google account", "yt music", "login", "sign in", "oauth",
                        "connect youtube", "disconnect youtube", "account"
                    ),
                    icon = Icons.Filled.CloudSync,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.YOUTUBE,
                    section = appText(com.lastwave.app.R.string.set_cat_yt),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "youtube.sync",
                    title = appText(com.lastwave.app.R.string.ui_24_7_library_sync_8cfb92),
                    subtitle = appText(com.lastwave.app.R.string.ui_keep_playlists_and_favorites_continuously_synced_8d7523),
                    keywords = listOf(
                        "youtube sync", "library sync", "cloud sync", "auto sync", "mirror playlists",
                        "24/7 sync", "background sync", "sync interval"
                    ),
                    icon = Icons.Filled.CloudSync,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.YOUTUBE,
                    section = appText(com.lastwave.app.R.string.set_cat_yt),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "youtube.channel",
                    title = appText(com.lastwave.app.R.string.set_yt_channel),
                    subtitle = appText(com.lastwave.app.R.string.ui_switch_between_personal_and_brand_channels_linke_64ed53),
                    keywords = listOf(
                        "youtube channel", "brand account", "channel selector", "switch channel",
                        "yt profile", "sub channel"
                    ),
                    icon = Icons.Filled.SwitchAccount,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.YOUTUBE,
                    section = appText(com.lastwave.app.R.string.set_cat_yt),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "youtube.select_playlists",
                    title = appText(com.lastwave.app.R.string.settings_select_playlists),
                    subtitle = appText(com.lastwave.app.R.string.ui_choose_specific_playlists_to_mirror_with_your_yo_4726c7),
                    keywords = listOf(
                        "sync playlists", "playlist selection", "choose playlists", "mirror playlists",
                        "synced playlists", "filter sync"
                    ),
                    icon = Icons.Filled.FormatListBulleted,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.YOUTUBE,
                    section = appText(com.lastwave.app.R.string.set_cat_yt),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "youtube.shown_playlists",
                    title = appText(com.lastwave.app.R.string.ui_show_hide_account_playlists_c81413),
                    subtitle = appText(com.lastwave.app.R.string.ui_control_which_playlists_from_your_youtube_accoun_08491c),
                    keywords = listOf(
                        "show playlists", "hide playlists", "visibility", "account playlists",
                        "library visibility", "filter playlists"
                    ),
                    icon = Icons.Filled.Visibility,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.YOUTUBE,
                    section = appText(com.lastwave.app.R.string.set_cat_yt),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "youtube.import",
                    title = appText(com.lastwave.app.R.string.set_import_yt),
                    subtitle = appText(com.lastwave.app.R.string.ui_search_browse_or_paste_playlist_links_ids_to_imp_74feaf),
                    keywords = listOf(
                        "import youtube", "import playlist", "paste link", "yt import", "copy playlist",
                        "transfer youtube", "make local"
                    ),
                    icon = Icons.Filled.QueueMusic,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.YOUTUBE,
                    section = appText(com.lastwave.app.R.string.set_cat_yt),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "youtube.history",
                    title = appText(com.lastwave.app.R.string.ui_youtube_music_listening_history_b5295b),
                    subtitle = appText(com.lastwave.app.R.string.ui_record_plays_and_scrobbles_in_your_official_yout_26ab38),
                    keywords = listOf(
                        "youtube history", "watch history", "yt history sync", "scrobble to youtube",
                        "listening history", "sync plays"
                    ),
                    icon = Icons.Filled.History,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.YOUTUBE,
                    section = appText(com.lastwave.app.R.string.set_cat_yt),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "spotify.account",
                    title = appText(com.lastwave.app.R.string.ui_connect_spotify_account_e9e3f8),
                    subtitle = appText(com.lastwave.app.R.string.ui_sign_in_to_enable_official_spotify_canvas_video__5e24f2),
                    keywords = listOf(
                        "spotify", "spotify account", "connect spotify", "sp_dc", "spotify login",
                        "canvas login", "disconnect spotify", "logout spotify", "sign out"
                    ),
                    icon = Icons.Filled.PlayCircle,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.primary },
                    parentTab = SettingsTab.SPOTIFY,
                    section = appText(com.lastwave.app.R.string.spotify_playlists_section_title),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "spotify.import",
                    title = appText(com.lastwave.app.R.string.ui_your_spotify_playlists_ea794e),
                    subtitle = appText(com.lastwave.app.R.string.ui_import_your_spotify_playlists_and_liked_songs_in_a0627e),
                    keywords = listOf(
                        "spotify playlist", "import playlist", "my playlists", "liked songs",
                        "spotify library", "import from spotify"
                    ),
                    icon = Icons.Filled.QueueMusic,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.SPOTIFY,
                    section = appText(com.lastwave.app.R.string.spotify_playlists_section_title),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "spotify.canvas",
                    title = appText(com.lastwave.app.R.string.ui_spotify_canvas_looping_background_videos_bb0707),
                    subtitle = appText(com.lastwave.app.R.string.ui_fetch_and_play_spotify_s_looping_visual_videos_i_9e469f),
                    keywords = listOf(
                        "spotify canvas", "canvas", "video loop", "looping video", "moving background"
                    ),
                    icon = Icons.Filled.SmartDisplay,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.SPOTIFY,
                    section = appText(com.lastwave.app.R.string.spotify_playlists_section_title),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "spotify.prefer_canvas",
                    title = appText(com.lastwave.app.R.string.ui_prefer_spotify_canvas_033107),
                    subtitle = appText(com.lastwave.app.R.string.ui_ask_spotify_first_before_apple_music_tidal_and_c_2a6e9d),
                    keywords = listOf(
                        "spotify canvas", "prefer", "priority", "first", "apple music", "tidal"
                    ),
                    icon = Icons.Filled.AutoAwesome,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.SPOTIFY,
                    section = appText(com.lastwave.app.R.string.spotify_playlists_section_title),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "spotify.recommendations",
                    title = appText(com.lastwave.app.R.string.settings_recommendations_title),
                    subtitle = appText(com.lastwave.app.R.string.ui_seed_recommendations_from_youtube_music_or_spoti_74afee),
                    keywords = listOf(
                        "recommendation", "recommendations", "algorithm", "provider", "radio",
                        "discover", "mix", "mixes", "spotify recommendations", "youtube recommendations"
                    ),
                    icon = Icons.Filled.AutoAwesome,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.SPOTIFY,
                    section = appText(com.lastwave.app.R.string.settings_section_recommendations),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "spotify.token",
                    title = appText(com.lastwave.app.R.string.ui_import_sp_dc_token_8f640f),
                    subtitle = appText(com.lastwave.app.R.string.ui_paste_a_session_cookie_from_your_browser_instead_50b7ce),
                    keywords = listOf(
                        "sp_dc", "cookie", "token", "paste", "import", "session", "spotify login"
                    ),
                    icon = Icons.Filled.Key,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.SPOTIFY,
                    section = appText(com.lastwave.app.R.string.ui_session_token_12f1f7),
                    type = EntryType.ACTION,
                )
            )

            // ==========================================
            // TAB: SCROBBLERS
            // ==========================================
            for ((id, name, description, terms) in listOf(
                listOf("listenbrainz.connect", "ListenBrainz", "Connect with your user token; optional compatible server", "listenbrainz token maloja koito self-hosted"),
                listOf("librefm.connect", "Libre.fm", "Connect with your username and password; optional GNU FM server", "librefm libre.fm gnu fm password self-hosted"),
            )) {
                add(
                    SettingsEntry(
                        id = id,
                        title = appText(com.lastwave.app.R.string.ui_1_s_account_connection_7ed024, (name).toString()),
                        subtitle = description,
                        keywords = terms.split(" ") + listOf("scrobblers", "scrobble", "scrobbling", "connect", "login"),
                        icon = Icons.Filled.Album,
                        iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                        iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                        parentTab = SettingsTab.SCROBBLERS,
                        section = appText(com.lastwave.app.R.string.set_cat_scrobblers),
                        type = EntryType.ACTION,
                    )
                )
            }
            add(
                SettingsEntry(
                    id = "lastfm.connect",
                    title = appText(com.lastwave.app.R.string.ui_last_fm_account_connection_50961e),
                    subtitle = appText(com.lastwave.app.R.string.ui_connect_last_fm_account_for_global_scrobbling_lo_f6b684),
                    keywords = listOf(
                        "lastfm", "last.fm", "scrobble", "scrobbling", "login lastfm", "connect lastfm",
                        "auth", "stats", "loved tracks", "profile"
                    ),
                    icon = Icons.Filled.Album,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.SCROBBLERS,
                    section = appText(com.lastwave.app.R.string.set_cat_scrobblers),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "lastfm.api_credentials",
                    title = appText(com.lastwave.app.R.string.ui_last_fm_custom_api_credentials_256bb5),
                    subtitle = appText(com.lastwave.app.R.string.ui_configure_custom_last_fm_api_key_and_secret_to_b_149d72),
                    keywords = listOf(
                        "lastfm api key", "custom api", "api secret", "developer key", "credentials",
                        "rate limit", "last.fm token"
                    ),
                    icon = Icons.Filled.Code,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.SCROBBLERS,
                    section = appText(com.lastwave.app.R.string.set_cat_scrobblers),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "discord.presence",
                    title = appText(com.lastwave.app.R.string.set_discord_title),
                    subtitle = appText(com.lastwave.app.R.string.ui_show_the_playing_track_quality_and_cover_on_your_3dcf00),
                    keywords = listOf(
                        "discord", "rich presence", "discord rpc", "now playing discord",
                        "discord status", "listening to", "presence", "show what im playing"
                    ),
                    icon = Icons.Filled.Public,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.SCROBBLERS,
                    section = appText(com.lastwave.app.R.string.set_cat_scrobblers),
                    type = EntryType.TOGGLE,
                )
            )

            // ==========================================
            // TAB: NOTIFICATION SCROBBLER
            // ==========================================


            // ==========================================
            // TAB: LIBRARY & CONTENT
            // ==========================================
            add(
                SettingsEntry(
                    id = "library.home_sections",
                    title = appText(com.lastwave.app.R.string.ui_home_layout_sections_f71040),
                    subtitle = appText(com.lastwave.app.R.string.ui_configure_visible_feeds_shelves_and_quick_access_0fe1be),
                    keywords = listOf(
                        "home layout", "home sections", "feed", "organize home", "shelves",
                        "customize library", "home screen layout"
                    ),
                    icon = Icons.Filled.Dashboard,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.LIBRARY,
                    section = appText(com.lastwave.app.R.string.ui_home_organization_1c7c46),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "library.excluded_songs",
                    title = appText(com.lastwave.app.R.string.ui_recommendation_exclusions_e52017),
                    subtitle = appText(com.lastwave.app.R.string.ui_manage_songs_excluded_from_recommendations_and_s_b08a2e),
                    keywords = listOf(
                        "excluded songs", "recommendations", "blocked tracks", "hidden songs",
                        "don't recommend", "exclude from mixes", "reset exclusions"
                    ),
                    icon = Icons.Filled.RestartAlt,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.LIBRARY,
                    section = appText(com.lastwave.app.R.string.ui_home_organization_1c7c46),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "library.podcasts",
                    title = appText(com.lastwave.app.R.string.podcasts_title),
                    subtitle = appText(com.lastwave.app.R.string.ui_show_or_hide_podcast_shelves_show_pages_and_epis_9a5540),
                    keywords = listOf(
                        "podcast", "podcasts", "shows", "episodes", "hide podcasts",
                        "youtube music podcasts", "episode search"
                    ),
                    icon = Icons.Filled.Mic,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.LIBRARY,
                    section = appText(com.lastwave.app.R.string.settings_section_content),
                    type = EntryType.TOGGLE,
                )
            )
            add(
                SettingsEntry(
                    id = "library.import_yt",
                    title = appText(com.lastwave.app.R.string.set_import_yt),
                    subtitle = appText(com.lastwave.app.R.string.ui_search_browse_or_paste_playlist_links_ids_to_imp_74feaf),
                    keywords = listOf(
                        "import youtube", "import playlist", "yt music import", "paste playlist url",
                        "transfer youtube playlists"
                    ),
                    icon = Icons.Filled.QueueMusic,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.LIBRARY,
                    section = appText(com.lastwave.app.R.string.ui_imports_addons_68a3bc),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "library.import_external",
                    title = appText(com.lastwave.app.R.string.set_import_spotify),
                    subtitle = appText(com.lastwave.app.R.string.ui_paste_a_public_playlist_url_from_spotify_or_appl_44dbb6),
                    keywords = listOf(
                        "import spotify", "import apple music", "transfer playlist", "spotify to lastwave",
                        "apple to lastwave", "playlist converter", "public link"
                    ),
                    icon = Icons.Filled.QueueMusic,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.LIBRARY,
                    section = appText(com.lastwave.app.R.string.ui_imports_addons_68a3bc),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "library.import_file",
                    title = appText(com.lastwave.app.R.string.ui_import_playlists_from_file_csv_m3u_993ebd),
                    subtitle = appText(com.lastwave.app.R.string.ui_import_playlist_files_csv_listening_logs_or_m3u__7b9770),
                    keywords = listOf(
                        "import file", "csv", "m3u", "m3u8", "import local", "playlist file",
                        "local file import", "open playlist"
                    ),
                    icon = Icons.Filled.FileDownload,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.LIBRARY,
                    section = appText(com.lastwave.app.R.string.ui_imports_addons_68a3bc),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "library.modules",
                    title = appText(com.lastwave.app.R.string.ui_extensions_modules_86c278),
                    subtitle = appText(com.lastwave.app.R.string.ui_browse_and_configure_modular_plugins_and_extra_f_1cee66),
                    keywords = listOf(
                        "modules", "extensions", "plugins", "addons", "modularity", "extra features"
                    ),
                    icon = Icons.Filled.Extension,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.LIBRARY,
                    section = appText(com.lastwave.app.R.string.ui_imports_addons_68a3bc),
                    type = EntryType.ACTION,
                )
            )

            // ==========================================
            // TAB: DATA & STORAGE
            // ==========================================
            add(
                SettingsEntry(
                    id = "backup.create",
                    title = appText(com.lastwave.app.R.string.ui_backup_settings_playlists_b7a92e),
                    subtitle = appText(com.lastwave.app.R.string.ui_export_your_local_playlists_credentials_and_app__844f4c),
                    keywords = listOf(
                        "backup", "export data", "save backup", "export json", "backup playlists",
                        "backup settings", "save configuration"
                    ),
                    icon = Icons.Filled.Backup,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.DATA_BACKUP,
                    section = appText(com.lastwave.app.R.string.settings_section_backup),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "backup.restore",
                    title = appText(com.lastwave.app.R.string.ui_restore_from_backup_5df7f0),
                    subtitle = appText(com.lastwave.app.R.string.ui_import_a_previously_saved_lastwave_backup_file_t_554549),
                    keywords = listOf(
                        "restore", "import backup", "recover data", "load backup", "restore playlists",
                        "restore settings", "json restore"
                    ),
                    icon = Icons.Filled.CloudDownload,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.DATA_BACKUP,
                    section = appText(com.lastwave.app.R.string.settings_section_backup),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "backup.clear_all",
                    title = appText(com.lastwave.app.R.string.ui_reset_app_clear_all_data_02bdc9),
                    subtitle = appText(com.lastwave.app.R.string.ui_remove_credentials_playlists_cached_audio_and_re_4676e3),
                    keywords = listOf(
                        "clear all", "reset", "erase", "wipe data", "delete everything", "factory reset",
                        "danger", "remove data", "clear cache"
                    ),
                    icon = Icons.Filled.Delete,
                    iconContainer = { MaterialTheme.colorScheme.errorContainer },
                    iconTint = { MaterialTheme.colorScheme.onErrorContainer },
                    parentTab = SettingsTab.DATA_BACKUP,
                    section = appText(com.lastwave.app.R.string.settings_section_data),
                    type = EntryType.ACTION,
                )
            )

            // ==========================================
            // TAB: ABOUT & SYSTEM
            // ==========================================
            add(
                SettingsEntry(
                    id = "about.language",
                    title = appText(com.lastwave.app.R.string.settings_language_title),
                    subtitle = appText(com.lastwave.app.R.string.ui_change_app_language_english_spanish_russian_and__1ea784),
                    keywords = listOf(
                        "language", "locale", "translation", "english", "spanish", "russian",
                        "idioma", "sprache", "change language"
                    ),
                    icon = Icons.Filled.Language,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.ABOUT,
                    section = appText(com.lastwave.app.R.string.settings_section_language),
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "about.telegram_support",
                    title = appText(com.lastwave.app.R.string.ui_telegram_updates_support_a730db),
                    subtitle = appText(com.lastwave.app.R.string.ui_join_clashprojects_on_telegram_for_app_news_beta_f265cb),
                    keywords = listOf(
                        "telegram", "support", "community", "chat", "clashprojects", "help",
                        "updates channel", "news"
                    ),
                    icon = Icons.AutoMirrored.Filled.Send,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.ABOUT,
                    section = "About & Community",
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "about.discord_support",
                    title = appText(com.lastwave.app.R.string.set_discord_support),
                    subtitle = appText(com.lastwave.app.R.string.ui_join_our_discord_community_server_for_support_an_807953),
                    keywords = listOf(
                        "discord", "community", "chat", "support", "feedback", "server", "discussion"
                    ),
                    icon = Icons.Filled.Group,
                    iconContainer = { MaterialTheme.colorScheme.tertiaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onTertiaryContainer },
                    parentTab = SettingsTab.ABOUT,
                    section = "About & Community",
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "about.more_apps",
                    title = appText(com.lastwave.app.R.string.ui_more_apps_from_clash_projects_1a83d5),
                    subtitle = appText(com.lastwave.app.R.string.ui_join_materialyouapp_on_telegram_to_discover_othe_2e5cbd),
                    keywords = listOf(
                        "more apps", "material you app", "other apps", "clash projects", "developer"
                    ),
                    icon = Icons.Filled.AutoAwesome,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.ABOUT,
                    section = "About & Community",
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "about.diagnostics",
                    title = appText(com.lastwave.app.R.string.ui_export_diagnostics_logs_afee25),
                    subtitle = appText(com.lastwave.app.R.string.ui_export_diagnostic_report_crash_logs_and_hardware_657b0d),
                    keywords = listOf(
                        "diagnostics", "logs", "crash logs", "debug info", "report issue", "bug report",
                        "troubleshooting", "system info"
                    ),
                    icon = Icons.Filled.Code,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.ABOUT,
                    section = "About & Community",
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "about.check_updates",
                    title = appText(com.lastwave.app.R.string.settings_check_updates),
                    subtitle = appText(com.lastwave.app.R.string.ui_check_github_releases_for_the_latest_version_and_905e07),
                    keywords = listOf(
                        "update", "check updates", "new version", "github releases", "changelog",
                        "download update", "latest release", "upgrade"
                    ),
                    icon = Icons.Filled.CloudDownload,
                    iconContainer = { MaterialTheme.colorScheme.primaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onPrimaryContainer },
                    parentTab = SettingsTab.ABOUT,
                    section = "About & Community",
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "about.source_code",
                    title = appText(com.lastwave.app.R.string.ui_open_source_code_995802),
                    subtitle = appText(com.lastwave.app.R.string.ui_view_repository_at_github_com_clash_projects_las_aa815a),
                    keywords = listOf(
                        "source code", "github", "open source", "repository", "gpl", "license",
                        "contribute", "git"
                    ),
                    icon = Icons.Filled.Code,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.ABOUT,
                    section = "About & Community",
                    type = EntryType.ACTION,
                )
            )
            add(
                SettingsEntry(
                    id = "about.desktop_app",
                    title = appText(com.lastwave.app.R.string.settings_desktop_app),
                    subtitle = appText(com.lastwave.app.R.string.ui_view_repository_at_github_com_clash_projects_las_95c5fc),
                    keywords = listOf(
                        "desktop", "pc", "windows", "mac", "linux", "electron", "github", "clash-projects",
                        "download"
                    ),
                    icon = Icons.Filled.Download,
                    iconContainer = { MaterialTheme.colorScheme.secondaryContainer },
                    iconTint = { MaterialTheme.colorScheme.onSecondaryContainer },
                    parentTab = SettingsTab.ABOUT,
                    section = "About & Community",
                    type = EntryType.ACTION,
                )
            )
        }
    }
}
