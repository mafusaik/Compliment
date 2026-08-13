package com.glazer.compliment.models

import androidx.annotation.StringRes
import com.glazer.compliment.R

enum class Language(
    val code: String,
    @StringRes val nameResId: Int,
    val flagEmoji: String
) {
    ENGLISH("en", R.string.lang_english, "🇺🇸"),
    RUSSIAN("ru", R.string.lang_russian, "🇷🇺"),
    FRENCH("fr", R.string.lang_french, "🇫🇷"),
    GERMAN("de", R.string.lang_german, "🇩🇪"),
    ITALIAN("it", R.string.lang_italian, "🇮🇹"),
    HINDI("hi", R.string.lang_hindi, "🇮🇳"),
    KAZAKH("kk", R.string.lang_kazakh, "🇰🇿"),
    SPANISH("es", R.string.lang_spanish, "🇪🇸"),
    BELARUSIAN("be", R.string.lang_belarusian, "🇧🇾"),
    UKRAINIAN("uk", R.string.lang_ukrainian, "🇺🇦");

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code == code } ?: ENGLISH
        }
    }
}
