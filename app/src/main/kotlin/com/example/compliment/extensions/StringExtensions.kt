package com.example.compliment.extensions

import android.content.Context
import com.example.compliment.R

fun String.langToLangCode(context: Context): String {
    return when(this){
        context.getString(R.string.lang_english) -> "en"
       // context.getString(R.string.lang_russian) -> "ru"
        else -> "ru"
    }
}

fun String.langCodeToLang(context: Context): String {
    return when(this){
        "en" -> context.getString(R.string.lang_english)
        else -> context.getString(R.string.lang_russian)
    }
}