package com.lastwave.app.ui.stats

import com.lastwave.app.util.appText

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lastwave.app.ui.common.adaptiveContentWidth
import com.lastwave.app.ui.shell.FloatingNavDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListeningStatsScreen(
    onBack: () -> Unit,
    viewModel: ListeningStatsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth().statusBarsPadding(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .adaptiveContentWidth(maxWidth = 860.dp)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = appText(com.lastwave.app.R.string.lyshare_back_cd),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = appText(com.lastwave.app.R.string.ui_listening_stats_9d8b97),
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = viewModel::refresh) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = appText(com.lastwave.app.R.string.ytimp_refresh),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .adaptiveContentWidth(maxWidth = 860.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = FloatingNavDefaults.contentBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── TIME RANGE CHIPS ──
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(StatsTimeRange.entries) { range ->
                        StatsFilterChip(
                            label = range.label,
                            selected = (uiState.selectedRange == range),
                            onClick = { viewModel.setTimeRange(range) },
                        )
                    }
                }
            }

            // ── TOP SUMMARY CARDS (Listening & Plays) ──
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SummaryStatCard(
                        label = "Listening",
                        value = uiState.totalListeningLabel,
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    )
                    SummaryStatCard(
                        label = "Plays",
                        value = uiState.totalPlays.toString(),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            // ── RHYTHM & CONSISTENCY (SLEEP TRACKER INSPIRED) ──
            uiState.consistency?.let { consistency ->
                item {
                    ConsistencyScoreCard(data = consistency)
                }
            }

            // ── LISTENING STAGES (DEEP FOCUS / FLOW / DISCOVERY) ──
            uiState.stages?.let { stages ->
                item {
                    ListeningStagesCard(stages = stages)
                }
            }

            // ── PERSONAL RECORDS ──
            uiState.records?.let { records ->
                item {
                    PersonalRecordsCard(records = records)
                }
            }

            // ── WEEKLY RHYTHM & MONTH COMPARISON ──
            uiState.weeklyRhythm?.let { rhythm ->
                item {
                    WeeklyRhythmCard(rhythm = rhythm)
                }
            }

            uiState.monthComparison?.let { comparison ->
                item {
                    MonthComparisonCard(comparison = comparison)
                }
            }

            // ── HOURLY LISTENING RHYTHM ──
            if (uiState.hourlyRhythm.isNotEmpty()) {
                item {
                    HourlyRhythmChart(bars = uiState.hourlyRhythm)
                }
            }

            // ── ACTIVITY HEATMAP (LAST 12 WEEKS) ──
            if (uiState.activityHeatmap.isNotEmpty()) {
                item {
                    ActivityHeatmapGrid(days = uiState.activityHeatmap)
                }
            }

            // ── SECTION 1: LISTENING TIMELINE ──
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = appText(com.lastwave.app.R.string.ui_listening_timeline_88101f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = when (uiState.selectedRange) {
                            StatsTimeRange.WEEK -> appText(com.lastwave.app.R.string.ui_total_listening_captured_this_week_daily_bars_su_13cd5b)
                            StatsTimeRange.MONTH_TO_DATE -> appText(com.lastwave.app.R.string.ui_total_listening_captured_this_month_daily_bars_s_9c9d5f)
                            StatsTimeRange.YEAR_TO_DATE -> appText(com.lastwave.app.R.string.ui_total_listening_captured_this_year_monthly_bars__bfcbb5)
                            StatsTimeRange.ALL_TIME -> appText(com.lastwave.app.R.string.ui_total_listening_captured_in_the_selected_range_y_800a1a)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    // Timeline metric chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        items(TimelineMetric.entries) { metric ->
                            StatsFilterChip(
                                label = metric.label,
                                selected = (uiState.selectedTimelineMetric == metric),
                                onClick = { viewModel.setTimelineMetric(metric) },
                            )
                        }
                    }

                    // Bar Chart Card
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(
                                        text = when (uiState.selectedRange) {
                                            StatsTimeRange.WEEK -> appText(com.lastwave.app.R.string.ui_current_week_progression_5b1feb)
                                            StatsTimeRange.MONTH_TO_DATE -> appText(com.lastwave.app.R.string.ui_current_month_progression_c0ed2d)
                                            StatsTimeRange.YEAR_TO_DATE -> appText(com.lastwave.app.R.string.ui_year_to_date_progression_615e4c)
                                            StatsTimeRange.ALL_TIME -> appText(com.lastwave.app.R.string.ui_all_time_progression_374f16)
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = when (uiState.selectedRange) {
                                            StatsTimeRange.WEEK -> appText(com.lastwave.app.R.string.ui_grouped_by_day_61a54a)
                                            StatsTimeRange.MONTH_TO_DATE -> appText(com.lastwave.app.R.string.ui_grouped_by_day_61a54a)
                                            StatsTimeRange.YEAR_TO_DATE -> appText(com.lastwave.app.R.string.ui_grouped_by_month_56c352)
                                            StatsTimeRange.ALL_TIME -> appText(com.lastwave.app.R.string.ui_grouped_by_year_ca6e28)
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                ) {
                                    Text(
                                        text = uiState.selectedTimelineMetric.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    )
                                }
                            }

                            Spacer(Modifier.height(18.dp))

                            ListeningStatsBarChart(
                                bars = uiState.timelineBars,
                                highlightColor = MaterialTheme.colorScheme.primary,
                                barColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }
                    }

                    // Peak segment card
                    if (uiState.peakSegmentName.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                Spacer(Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = appText(com.lastwave.app.R.string.ui_peak_segment_3bcb9c),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        text = uiState.peakSegmentName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = uiState.peakSegmentDuration,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── SECTION 2: TOP CATEGORIES ──
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = appText(com.lastwave.app.R.string.ui_top_categories_39960c),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = appText(com.lastwave.app.R.string.ui_compare_how_you_listen_across_genres_artists_alb_3b26bf),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    // Filter chips: Song / Album / Artist / Genre
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        items(CategoryFilter.entries) { cat ->
                            StatsFilterChip(
                                label = cat.label,
                                selected = (uiState.selectedCategory == cat),
                                onClick = { viewModel.setCategory(cat) },
                            )
                        }
                    }

                    // Content card
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = appText(com.lastwave.app.R.string.ui_listening_by_1_s_e96628, (uiState.selectedCategory.label.lowercase()).toString()),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(Modifier.height(14.dp))

                            if (uiState.categoryItems.isEmpty()) {
                                Text(
                                    text = appText(com.lastwave.app.R.string.ui_no_1_s_plays_captured_in_this_range_yet_4dce05, (uiState.selectedCategory.label.lowercase()).toString()),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 12.dp),
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    uiState.categoryItems.forEach { item ->
                                        RankedCategoryRow(item = item)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── SECTION 3: LISTENING HABITS ──
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = appText(com.lastwave.app.R.string.ui_listening_habits_a2c7a0),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    val habits = uiState.habits
                    if (habits != null) {
                        HabitItemRow(
                            title = appText(com.lastwave.app.R.string.ui_total_sessions_3d28aa),
                            value = habits.totalSessions.toString(),
                            icon = Icons.Filled.History,
                        )
                        HabitItemRow(
                            title = appText(com.lastwave.app.R.string.ui_avg_session_87fc8c),
                            value = habits.avgSessionLabel,
                            icon = Icons.Filled.Timer,
                        )
                        HabitItemRow(
                            title = appText(com.lastwave.app.R.string.ui_longest_session_d7c4b9),
                            value = habits.longestSessionLabel,
                            icon = Icons.Filled.Bolt,
                        )
                        HabitItemRow(
                            title = appText(com.lastwave.app.R.string.ui_sessions_day_27c068),
                            value = habits.sessionsPerDay,
                            icon = Icons.Filled.AutoAwesome,
                        )
                        HabitItemRow(
                            title = appText(com.lastwave.app.R.string.ui_most_active_day_e7f686),
                            value = habits.mostActiveDay,
                            icon = Icons.Filled.CalendarMonth,
                        )
                        HabitItemRow(
                            title = appText(com.lastwave.app.R.string.ui_peak_timeline_slot_916cfb),
                            value = habits.peakTimelineSlot,
                            icon = Icons.Filled.TrendingUp,
                        )
                    }
                }
            }

            // ── SECTION 4: TRACK CONCENTRATION ──
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = appText(com.lastwave.app.R.string.ui_track_concentration_f93065),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = appText(com.lastwave.app.R.string.ui_how_your_listening_time_is_distributed_across_yo_682196),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    val conc = uiState.concentration
                    if (conc != null) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                DonutConcentrationChart(
                                    percentage = conc.top3SharePercent,
                                    accentColor = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )

                                Spacer(Modifier.height(16.dp))

                                Text(
                                    text = appText(com.lastwave.app.R.string.ui_listening_concentration_876069),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = appText(com.lastwave.app.R.string.ui_top_3_tracks_account_for_1_s_of_your_listening_t_a311d7, (conc.top3SharePercent).toString()),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )

                                Spacer(Modifier.height(16.dp))

                                // Summary mini-cards
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                            Text(
                                                text = conc.avgPlaysPerTrack,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                            Text(
                                                text = appText(com.lastwave.app.R.string.ui_avg_plays_track_667062),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                            Text(
                                                text = conc.uniqueTracksCount.toString(),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                            Text(
                                                text = appText(com.lastwave.app.R.string.ui_unique_tracks_f662be),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                // Breakdown rows with colored dots
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    ConcentrationRow(
                                        label = "Top 1",
                                        percentage = conc.top1Percent,
                                        duration = conc.top1DurationLabel,
                                        dotColor = MaterialTheme.colorScheme.primary,
                                    )
                                    ConcentrationRow(
                                        label = "Top 2-3",
                                        percentage = conc.top2To3Percent,
                                        duration = conc.top2To3DurationLabel,
                                        dotColor = MaterialTheme.colorScheme.tertiary,
                                    )
                                    ConcentrationRow(
                                        label = "Others",
                                        percentage = conc.othersPercent,
                                        duration = conc.othersDurationLabel,
                                        dotColor = MaterialTheme.colorScheme.outlineVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── SECTION 5: TRACKS IN THIS RANGE ──
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = appText(com.lastwave.app.R.string.ui_tracks_in_this_range_e4c07d),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = appText(com.lastwave.app.R.string.ui_most_played_tracks_for_the_selected_time_range_00942a),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (uiState.tracksInRange.isEmpty()) {
                        Text(
                            text = appText(com.lastwave.app.R.string.ui_no_track_playback_recorded_for_this_range_49970b),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.tracksInRange.forEach { track ->
                                RankedTrackCard(track = track)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConcentrationRow(
    label: String,
    percentage: Int,
    duration: String,
    dotColor: androidx.compose.ui.graphics.Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${percentage}%",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = duration,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
