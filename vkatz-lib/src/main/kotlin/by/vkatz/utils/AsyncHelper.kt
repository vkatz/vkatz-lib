package by.vkatz.utils

import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlin.coroutines.experimental.CoroutineContext

/*
fun foo() {
    asyncUI {
        val t1 = async { 1 }.await()                                //Int?
        val t2 = async { 1 }.await()!!                              //Int
        val t3 = safeAsync { 1 }.onError { 2 }.await()              //Int
        val t4 = safeAsync<Int?> { 1 }.onError { null }.await()     //Int?
    }
}
*/

typealias AsyncResult<T> = Deferred<T>

class AsyncHelper<T>(private val context: CoroutineContext, private val action: suspend () -> T) {
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
fun <T> asyncUI(action: suspend () -> T): AsyncResult<T?> = safeAsyncUI<T?>(action).onError { Log.e("AsyncHelper", "asyncUI", it); null }
fun <T> async(action: suspend () -> T): AsyncResult<T?> = safeAsync<T?>(action).onError { Log.e("AsyncHelper", "async", it); null }
