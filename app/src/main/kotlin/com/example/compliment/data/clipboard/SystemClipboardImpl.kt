package com.example.compliment.data.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class SystemClipboardImpl(
    private val context: Context
) : SystemClipboard {
    override fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }
}