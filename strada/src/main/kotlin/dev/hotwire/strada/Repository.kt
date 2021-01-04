package dev.hotwire.strada

import android.content.Context

internal class Repository {
    fun getUserScript(context: Context): String {
        return context.assets.open("js/strada.js").use {
            String(it.readBytes())
        }
    }
}
