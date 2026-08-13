package com.glazer.compliment.extensions

import android.content.Context
import com.glazer.compliment.models.Language

fun String.langToLangCode(context: Context): String {
    return Language.entries.find { 
        val name = context.getString(it.nameResId)
        this == "${it.flagEmoji} $name" || this == name
    }?.code ?: "en"
}

fun String.langCodeToLang(context: Context): String {
    val language = Language.fromCode(this)
    return "${language.flagEmoji} ${context.getString(language.nameResId)}"
}
