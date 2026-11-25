package com.lastwave.app.ui.stats

import com.lastwave.app.util.appText

import androidx.compose.runtime.Immutable

enum class StatsTimeRange(private val labelProvider: () -> String) {
    ALL_TIME({ appText(com.lastwave.app.R.string.gen_period_all) }),
    WEEK({ appText(com.lastwave.app.R.string.ui_week_f82be6) }),
    MONTH_TO_DATE({ appText(com.lastwave.app.R.string.ui_month_082bc3) }),
    YEAR_TO_DATE({ appText(com.lastwave.app.R.string.ui_year_879e32) });
    val label: String get() = labelProvider()
}

enum class TimelineMetric(private val labelProvider: () -> String) {
    LISTENING_TIME({ appText(com.lastwave.app.R.string.ui_listening_time_f3c051) }),
    PLAY_COUNT({ appText(com.lastwave.app.R.string.ui_play_count_50a957) }),
    AVG_SESSION({ appText(com.lastwave.app.R.string.ui_avg_session_6d49c6) });
    val label: String get() = labelProvider()
}

enum class CategoryFilter(private val labelProvider: () -> String) {
    SONG({ appText(com.lastwave.app.R.string.lyshare_mode_song) }),
    ALBUM({ appText(com.lastwave.app.R.string.art_type_album) }),
    ARTIST({ appText(com.lastwave.app.R.string.pd_sort_artist) }),
    GENRE({ appText(com.lastwave.app.R.string.details_genre) });
    val label: String get() = labelProvider()
}

@Immutable
data class TimelineBar(
    val label: String,
    val value: Float,
    val valueLabel: String,
    val isPeak: Boolean = false,
)

@Immutable
data class RankedCategoryItem(
    val rank: Int,
    val title: String,
    val subtitle: String,
    val durationLabel: String,
    val progress: Float,
    val artworkUrl: String? = null,
)

@Immutable
data class ListeningHabitsData(
    val totalSessions: Int,
    val avgSessionLabel: String,
    val longestSessionLabel: String,
    val sessionsPerDay: String,
    val mostActiveDay: String,
    val peakTimelineSlot: String,
)

@Immutable
data class TrackConcentrationData(
    val top3SharePercent: Int,
    val avgPlaysPerTrack: String,
    val uniqueTracksCount: Int,
    val top1Percent: Int,
    val top1DurationLabel: String,
    val top2To3Percent: Int,
    val top2To3DurationLabel: String,
    val othersPercent: Int,
    val othersDurationLabel: String,
)

@Immutable
data class RankedTrackItem(
    val rank: Int,
    val title: String,
    val artist: String,
    val artworkUrl: String?,
    val playCount: Int,
    val durationLabel: String,
    val progress: Float,
)

@Immutable
data class PersonalRecordsData(
    val bestDayPlays: Int,
    val bestDayDurationLabel: String,
    val bestDayDate: String,
    val bestWeekPlays: Int,
    val bestWeekDurationLabel: String,
    val bestMonthPlays: Int,
    val bestMonthDurationLabel: String,
    val longestStreakDays: Int,
)

@Immutable
data class WeeklyRhythmData(
    val weekdayAvgLabel: String,
    val weekendAvgLabel: String,
)

@Immutable
data class MonthComparisonData(
    val thisMonthLabel: String,
    val lastMonthLabel: String,
    val progress: Float,
)

@Immutable
data class HourlyRhythmBar(
    val hour: Int,
    val label: String,
    val count: Int,
    val fraction: Float,
    val isPeak: Boolean = false,
)

@Immutable
data class HeatmapDay(
    val dateString: String,
    val dayOfWeek: Int,
    val weekIndex: Int,
    val intensity: Int,
    val durationLabel: String,
)

@Immutable
data class ConsistencyScoreData(
    val score: Int,
    val rating: String,
    val subtitle: String,
    val avgSessionMinutes: Int,
    val activeDaysThisWeek: Int,
    val streakDays: Int,
)

@Immutable
data class ListeningStage(
    val name: String,
    val percent: Int,
    val durationLabel: String,
    val description: String,
)

@Immutable
data class ListeningStagesData(
    val deepFocus: ListeningStage,
    val flowState: ListeningStage,
    val discovery: ListeningStage,
)

@Immutable
data class ListeningStatsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val selectedRange: StatsTimeRange = StatsTimeRange.ALL_TIME,
    val selectedTimelineMetric: TimelineMetric = TimelineMetric.LISTENING_TIME,
    val selectedCategory: CategoryFilter = CategoryFilter.SONG,
    val totalListeningLabel: String = "0m",
    val totalPlays: Int = 0,
    val timelineBars: List<TimelineBar> = emptyList(),
    val peakSegmentName: String = "",
    val peakSegmentDuration: String = "",
    val categoryItems: List<RankedCategoryItem> = emptyList(),
    val habits: ListeningHabitsData? = null,
    val concentration: TrackConcentrationData? = null,
    val tracksInRange: List<RankedTrackItem> = emptyList(),
    val records: PersonalRecordsData? = null,
    val weeklyRhythm: WeeklyRhythmData? = null,
    val monthComparison: MonthComparisonData? = null,
    val hourlyRhythm: List<HourlyRhythmBar> = emptyList(),
    val activityHeatmap: List<HeatmapDay> = emptyList(),
    val consistency: ConsistencyScoreData? = null,
    val stages: ListeningStagesData? = null,
)
