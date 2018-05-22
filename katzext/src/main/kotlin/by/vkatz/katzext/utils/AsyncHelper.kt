package by.vkatz.katzext.utils

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.JobCancellationException
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlin.coroutines.experimental.CoroutineContext


/*fun foo() {
    asyncUI {
        val t1 = async { 1 }.await()   //Int? - ? due to task might be canceled
        val t2 = async { 1 }.await()!! //Int
        val t3 = AsyncHelper(null, newSingleThreadContext("WorkThread"), { 1 }).start().await()
    }
}*/


typealias AsyncResult<T> = Deferred<T>

open class AsyncHelper<out T>(private var lifecycle: Lifecycle? = null, private val context: CoroutineContext, private val action: suspend () -> T) : LifecycleObserver {
    companion object {
        var DEFAULT_ERROR_HANDLER: suspend (Throwable) -> Unit? = { if (it !is JobCancellationException) Log.e("AsyncHelper", "async::", it) }
        var DETACH_HANDLER = Handler(Looper.getMainLooper())
    }

    private var asyncAction: AsyncResult<T?>? = null

    fun start(): AsyncResult<T?> {
        lifecycle?.addObserver(this)
        asyncAction = async(context) {
            try {
                action()
            } catch (t: Throwable) {
                DEFAULT_ERROR_HANDLER(t)
                null
            } finally {
                detach()
            }
        }
        return asyncAction!!
    }

    private fun detach() {
        when {
            lifecycle == null -> return
            Looper.getMainLooper().thread == Thread.currentThread() -> {
                lifecycle?.removeObserver(this)
                lifecycle = null
            }
            else -> DETACH_HANDLER.post { detach() }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (asyncAction?.isActive == true)
            asyncAction?.cancel()
    }
}

fun <T> async(lifecycleOwner: LifecycleOwner? = null, coroutineContext: CoroutineContext = CommonPool, action: suspend () -> T): AsyncResult<T?> =
        AsyncHelper<T?>(lifecycleOwner?.lifecycle, coroutineContext, action).start()

fun <T> asyncUI(lifecycleOwner: LifecycleOwner? = null, action: suspend () -> T): AsyncResult<T?> = async(lifecycleOwner, UI, action)

fun <T> asyncResult(data: T) = async { data }

fun <T> asyncUIResult(data: T) = asyncUI { data }