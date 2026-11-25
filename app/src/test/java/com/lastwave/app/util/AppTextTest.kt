package com.lastwave.app.util

import android.app.Application
import com.lastwave.app.data.local.AppLanguage
import com.lastwave.app.ui.stats.StatsTimeRange
import com.lastwave.app.ui.home.formatTimer
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class, sdk = [29])
class AppTextTest {
    private fun language(tag: String) {
        val application = RuntimeEnvironment.getApplication()
        application.getSharedPreferences(AppLanguage.SYNC_FILE, 0).edit()
            .putString(AppLanguage.SYNC_KEY, tag).commit()
        AppText.install(application)
    }

    @Test fun enumLabelsFollowLanguageChanges() {
        language("en")
        assertEquals("Week", StatsTimeRange.WEEK.label)
        language("zh-CN")
        assertEquals("周", StatsTimeRange.WEEK.label)
        language("en")
        assertEquals("Week", StatsTimeRange.WEEK.label)
    }

    @Test fun timerKeepsValuesAndLocalizesUnits() {
        language("zh-CN")
        assertEquals("1 小时 20 分钟 30 秒", formatTimer(4830))
        language("en")
        assertEquals("1h 20m 30s", formatTimer(4830))
    }
}
