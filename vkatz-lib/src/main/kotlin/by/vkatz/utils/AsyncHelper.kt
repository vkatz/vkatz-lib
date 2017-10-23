package by.vkatz.utils

import android.util.Log
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import kotlin.coroutines.experimental.CoroutineContext

/*
fun foo() {
    asyncUI {
        val t1 = async { 1 }.await()                                //Int?
        val t2 = async { 1 }.await()!!                              //Int
        val t3 = safeAsync { 1 }.onError { 2 }.await()              //Int
        val t4 = safeAsync<Int?> { 1 }.onError { null }.await()     //Int?
        val t5 = AsyncHelper(newSingleThreadContext("WorkThread")) { 1 }.onError { 2 }.await()
        val t6 = AsyncHelper<Int?>(newFixedThreadPoolContext(5, "WorkThread")) { 1 }.onError { 2 }.await()
    }
}*/


typealias AsyncResult<T> = Deferred<T>

class AsyncHelper<T>(private val context: CoroutineContext, private val action: suspend () -> T) {
    companion object {
        var DEFAULT_ERROR_HANDLER: suspend (Throwable) -> Unit? = { if (it !is JobCancellationException) Log.e("AsyncHelper", "async::", it) }
    }

    fun run() = async(context) { action() }

    fun onError(onError: suspend (t: Throwable) -> T) = async(context) {
        try {
            action()
        } catch (t: Throwable) {
            onError(t)
        }
    }
}

fun <T> safeAsyncUI(action: suspend () -> T) = AsyncHelper(UI, action)
fun <T> safeAsync(action: suspend () -> T) = AsyncHelper(CommonPool, action)
fun <T> asyncUI(action: suspend () -> T): AsyncResult<T?> = safeAsyncUI<T?>(action).onError { AsyncHelper.DEFAULT_ERROR_HANDLER(it); null }
fun <T> async(action: suspend () -> T): AsyncResult<T?> = safeAsync<T?>(action).onError { AsyncHelper.DEFAULT_ERROR_HANDLER(it); null }
