package by.vkatz.utils

import android.util.Log

object LogUtils {
    var enabled = true

    fun i(tag: String, message: String) {
        if (enabled) Log.i(tag, message)
    }

    fun v(tag: String, message: String) {
        if (enabled) Log.v(tag, message)
    }

    fun w(tag: String, message: String) {
        if (enabled) Log.w(tag, message)
    }

    fun wtf(tag: String, message: String) {
        if (enabled) Log.wtf(tag, message)
    }

    fun e(tag: String, message: String) {
        if (enabled) Log.e(tag, message)
    }

    fun e(tag: String, e: Throwable) {
        e(tag, "", e)
    }

    fun e(tag: String, message: String, e: Throwable) {
        if (enabled) Log.e(tag, message, e)
    }
}
