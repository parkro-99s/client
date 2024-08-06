package com.parkro.client.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

object ClipboardUtil {

    fun copyTextToClipboard(context: Context, text: String, message: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("주소", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}