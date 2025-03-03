package com.glazer.compliment.data.sharedprefs;

import android.content.Context
import com.glazer.compliment.utils.Constants
import java.util.Locale

class PrefsManager(context:Context) {

    private val prefs =
            context.getSharedPreferences(PREFERENCES_NAME_TOKEN, Context.MODE_PRIVATE)

    var recentCompliments: Set<String>
        get() = prefs.getStringSet(KEY_RECENT_COMPLIMENTS, emptySet()) ?: emptySet()
        set(value) {
            prefs.edit().putStringSet(KEY_RECENT_COMPLIMENTS, value).apply()
        }

    var isExactTime: Boolean
        get() = prefs.getBoolean(KEY_EXACT_TIME, false)
        set(value) {
            prefs.edit().putBoolean(KEY_EXACT_TIME, value).apply()
        }

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, false)
        set(value) {
            prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()
        }

    var currentLanguage: String
        get() = prefs.getString(KEY_LANGUAGE, Locale.getDefault().language) ?: "en"
        set(value) {
            prefs.edit().putString(KEY_LANGUAGE, value).apply()
        }

    var currentGender: String
        get() = prefs.getString(KEY_GENDER, Constants.GENDER_WOMEN) ?: Constants.GENDER_WOMEN
        set(value) {
            prefs.edit().putString(KEY_GENDER, value).apply()
        }

    private companion object{
        private const val PREFERENCES_NAME_TOKEN = "setting_storage_token"
        private const val KEY_RECENT_COMPLIMENTS = "key_recent_compliments"
        private const val KEY_EXACT_TIME = "key_exact_time"
        private const val KEY_DARK_THEME = "key_dark_theme"
        private const val KEY_LANGUAGE = "key_current_language"
        private const val KEY_GENDER = "key_gender"
    }
}