package com.lastwave.app.util

import android.content.Context
import androidx.annotation.StringRes

/** Resource access for UI labels also used by non-composable models and callbacks. */
internal object AppText {
    private lateinit var context: Context

    fun install(application: Context) {
        context = application
    }

    fun get(@StringRes id: Int, vararg args: Any): String {
        // Resolve the selected language on each read, including pre-33 locale changes.
        val localized = AppLocaleManager.wrap(context)
        return if (args.isEmpty()) localized.getString(id) else localized.getString(id, *args)
    }
}

internal fun appText(@StringRes id: Int, vararg args: Any): String = AppText.get(id, *args)
