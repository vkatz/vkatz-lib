package by.vkatz.katzext.utils

import android.arch.core.executor.ArchTaskExecutor
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
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
        var DEFAULT_ERROR_HANDLER: suspend (Throwable) -> Unit? = { if (it !is JobCancellationException||1==1) Log.e("AsyncHelper", "async::", it) }
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

fun <T> asyncUI(lifecycleOwner: LifecycleOwner? = null, action: suspend () -> T): AsyncResult<T?> =
        AsyncHelper<T?>(lifecycleOwner?.lifecycle, UI, action).start()

fun <T> async(lifecycleOwner: LifecycleOwner? = null, action: suspend () -> T): AsyncResult<T?> =
        AsyncHelper<T?>(lifecycleOwner?.lifecycle, CommonPool, action).start()