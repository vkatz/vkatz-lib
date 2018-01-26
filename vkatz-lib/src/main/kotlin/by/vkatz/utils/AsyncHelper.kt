package by.vkatz.utils

import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.JobCancellationException
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlin.coroutines.experimental.CoroutineContext

/*
fun foo() {
    asyncUI {
        val t1 = async { 1 }.await()                                //Int?
        val t2 = async { 1 }.await()!!                              //Int
        val t3 = safeAsync { 1 }.onError { 2 }.run().await()              //Int
        val t4 = safeAsync<Int?> { 1 }.onError { null }.run().await()     //Int?
        val t5 = AsyncHelper(newSingleThreadContext("WorkThread")) { 1 }.onError { 2 }.run().await()
        val t6 = AsyncHelper<Int?>(newFixedThreadPoolContext(5, "WorkThread")) { 1 }.onError { 2 }.run().await()
    }
}*/


typealias AsyncResult<T> = Deferred<T>

open class AsyncHelper<T>(protected val context: CoroutineContext, private val action: suspend () -> T) {
    companion object {
        var DEFAULT_ERROR_HANDLER: suspend (Throwable) -> Unit? = { if (it !is JobCancellationException) Log.e("AsyncHelper", "async::", it) }
    }

    private var onError: (suspend (t: Throwable) -> T)? = null
    private var asyncAction: AsyncResult<T>? = null

    fun run(): AsyncResult<T> {
        asyncAction = async(context) {
            val result = if (onError != null) try {
                action()
            } catch (t: Throwable) {
                onError!!(t)
            } else action()
            result
        }
        return asyncAction!!
    }

    fun onError(onError: suspend (t: Throwable) -> T): AsyncHelper<T> {
        this.onError = onError
        return this
    }
}

fun <T> safeAsyncUI(action: suspend () -> T) = AsyncHelper(UI, action)

fun <T> safeAsync(action: suspend () -> T) = AsyncHelper(CommonPool, action)

fun <T> asyncUI(action: suspend () -> T) = AsyncHelper<T?>(UI, action).onError { e -> AsyncHelper.DEFAULT_ERROR_HANDLER(e) so null }.run()

fun <T> async(action: suspend () -> T) = AsyncHelper<T?>(CommonPool, action).onError { e -> AsyncHelper.DEFAULT_ERROR_HANDLER(e) so null }.run()