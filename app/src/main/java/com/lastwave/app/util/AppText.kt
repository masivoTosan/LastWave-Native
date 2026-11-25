package com.lastwave.app.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.annotation.StringRes

/** Resource access for UI labels also used by non-composable models and callbacks. */
internal object AppText {
    private lateinit var context: Context
    private var cachedTag: String? = null
    private var cachedConfiguration: Configuration? = null
    private var cachedContext: Context? = null

    @Synchronized
    fun install(application: Context) {
        context = application
        cachedContext = null
    }

    @Synchronized
    fun get(@StringRes id: Int, vararg args: Any): String {
        val tag = AppLocaleManager.effectiveTag(context)
        val configuration = Configuration(context.resources.configuration).apply {
            if (tag == "system") setLocales(Resources.getSystem().configuration.locales)
        }
        // Reuse the locale context across labels, but invalidate it on language/configuration changes.
        val localized = cachedContext?.takeIf {
            tag == cachedTag && configuration == cachedConfiguration
        } ?: (if (tag == "system") context.createConfigurationContext(configuration)
              else AppLocaleManager.wrapWithTag(context, tag)).also {
            cachedContext = it
            cachedTag = tag
            cachedConfiguration = Configuration(configuration)
        }
        return if (args.isEmpty()) localized.getString(id) else localized.getString(id, *args)
    }
}

internal fun appText(@StringRes id: Int, vararg args: Any): String = AppText.get(id, *args)
