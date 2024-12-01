package com.example.compliment.data.sharedprefs;

import android.content.Context

class PrefsManager(context:Context) {

    private val prefs =
            context.getSharedPreferences(PREFERENCES_NAME_TOKEN, Context.MODE_PRIVATE)

    var enabledNotifications: Boolean
        get() = prefs.getBoolean(KEY_ENABLED_NOTIFICATIONS, false)
        set(value) {
            prefs.edit().putBoolean(KEY_ENABLED_NOTIFICATIONS, value).apply()
        }

    var selectedDays: Set<String>
        get() = prefs.getStringSet(KEY_SELECTED_DAYS, emptySet()) ?: emptySet()
        set(value) {
            prefs.edit().putStringSet(KEY_SELECTED_DAYS, value).apply()
        }

    var selectedTimes: Set<String>
        get() = prefs.getStringSet(KEY_SELECTED_TIMES, emptySet()) ?: emptySet()
        set(value) {
            prefs.edit().putStringSet(KEY_SELECTED_TIMES, value).apply()
        }

    var recentCompliments: Set<String>
        get() = prefs.getStringSet(KEY_RECENT_COMPLIMENTS, emptySet()) ?: emptySet()
        set(value) {
            prefs.edit().putStringSet(KEY_RECENT_COMPLIMENTS, value).apply()
        }

    private companion object{
        private const val PREFERENCES_NAME_TOKEN = "setting_storage_token"
        private const val KEY_ENABLED_NOTIFICATIONS = "key_enabled_notifications"
        private const val KEY_SELECTED_DAYS = "key_selected_days"
        private const val KEY_SELECTED_TIMES = "key_selected_times"
        private const val KEY_RECENT_COMPLIMENTS = "key_recent_compliments"
    }
}