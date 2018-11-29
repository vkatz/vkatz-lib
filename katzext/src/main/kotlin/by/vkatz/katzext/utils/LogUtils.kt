package by.vkatz.katzext.utils

import android.util.Log
import by.vkatz.katzext.BuildConfig

/**
 * Log utils, no-tag messages will log file & line of code where it was called
 *
 * Non force(force == function name start with 'f') function will make log only for DEBUG model
 */
object LogUtils {

    private val ENABLED = BuildConfig.DEBUG

    fun i(message: String, error: Throwable? = null) {
        logTaggedMessage(message, error, ::i)
    }

    fun i(tag: String, message: String, error: Throwable? = null) {
        if (!ENABLED) return
        fi(tag, message, error)
    }

    fun e(message: String, error: Throwable? = null) {
        logTaggedMessage(message, error, ::e)
    }

    fun e(tag: String, message: String, error: Throwable? = null) {
        if (!ENABLED) return
        fe(tag, message, error)
    }

    fun fi(message: String, error: Throwable? = null) {
        logTaggedMessage(message, error, ::fi)
    }

    fun fi(tag: String, message: String, error: Throwable? = null) {
        if (error != null) Log.i(tag, message, error)
        else Log.i(tag, message)
    }

    fun fe(message: String, error: Throwable? = null) {
        logTaggedMessage(message, error, ::fe)
    }

    fun fe(tag: String, message: String, error: Throwable? = null) {
        if (error != null) Log.e(tag, message, error)
        else Log.e(tag, message)

    }

    fun wtf(message: String, error: Throwable? = null) {
        logTaggedMessage(message, error, ::wtf)
    }

    fun wtf(tag: String, message: String, error: Throwable? = null) {
        if (error != null) Log.wtf(tag, message, error)
        else Log.wtf(tag, message)
    }

    private fun getTraceItem(): StackTraceElement? =
            try {
                val stackTrace = Thread.currentThread().stackTrace
                val index = stackTrace.indexOfLast { it.className == LogUtils::class.java.canonicalName }
                stackTrace[index + 1]
            } catch (e: Throwable) {
                null
            }

    private fun buildTag(item: StackTraceElement?) = item?.fileName?.substringBeforeLast('.') ?: "LogUtils"

    private fun buildMessage(item: StackTraceElement?, message: String) = item?.let { "(${it.fileName}:${it.lineNumber}) -> $message" } ?: message

    private fun logTaggedMessage(message: String, error: Throwable? = null, logFun: (String, String, Throwable?) -> Unit) {
        val traceItem = getTraceItem()
        logFun(buildTag(traceItem), buildMessage(traceItem, message), error)
    }
}