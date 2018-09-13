package by.vkatz.katzext.utils

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.JobCancellationException
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.suspendCoroutine

typealias AsyncResult<T> = Deferred<T>

open class AsyncHelper<out T>(private var lifecycle: Lifecycle? = null, private val context: CoroutineContext, private val action: suspend () -> T) : LifecycleObserver {
    companion object {
        var DEFAULT_ERROR_HANDLER: suspend (Throwable) -> Unit? = { if (it !is JobCancellationException) LogUtils.fe("AsyncHelper", "async::", it) }
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

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (asyncAction?.isActive == true)
            asyncAction?.cancel()
    }
}

fun <T> async(fragment: Fragment, coroutineContext: CoroutineContext = CommonPool, action: suspend () -> T) =
        async(fragment.viewLifecycleOwner, coroutineContext, action)

fun <T> async(lifecycleOwner: LifecycleOwner? = null, coroutineContext: CoroutineContext = CommonPool, action: suspend () -> T): AsyncResult<T?> =
        AsyncHelper<T?>(lifecycleOwner?.lifecycle, coroutineContext, action).start()

fun <T> asyncUI(fragment: Fragment, action: suspend () -> T) = asyncUI(fragment.viewLifecycleOwner, action)

fun <T> asyncUI(lifecycleOwner: LifecycleOwner? = null, action: suspend () -> T): AsyncResult<T?> = async(lifecycleOwner, UI, action)

fun <T> suspendAsync(coroutineContext: CoroutineContext = CommonPool, resume: ((T) -> Unit) -> Unit): AsyncResult<T?> =
        async(null as LifecycleOwner?, coroutineContext) { suspendCoroutine<T> { out -> resume(out::resume) } }

fun <T> suspendAsyncUI(resume: ((T) -> Unit) -> Unit): AsyncResult<T?> = suspendAsync(UI, resume)