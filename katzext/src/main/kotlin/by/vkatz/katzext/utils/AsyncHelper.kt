@file:Suppress("unused")

package by.vkatz.katzext.utils

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

typealias AsyncResult<T> = Deferred<T>
typealias SuspendFun<T> = suspend CoroutineScope.() -> T

open class AsyncHelper<out T>(private var lifecycle: Lifecycle? = null, private val context: CoroutineContext, private val action: SuspendFun<T>) : LifecycleObserver {
    companion object {
        var DEFAULT_ERROR_HANDLER = { e: Throwable -> if (e !is CancellationException) LogUtils.fe("AsyncHelper", "async::", e) }
        var DETACH_HANDLER = Handler(Looper.getMainLooper())

        fun <T> run(lifecycleOwner: LifecycleOwner?, context: CoroutineContext, block: SuspendFun<T>) =
                AsyncHelper(lifecycleOwner?.lifecycle, context, block).start()
    }

    private var asyncAction: AsyncResult<T?>? = null

    fun start(): AsyncResult<T?> {
        lifecycle?.addObserver(this)
        asyncAction = GlobalScope.async(context) {
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

fun <T> runAsync(lifecycleOwner: LifecycleOwner? = null, block: SuspendFun<T>) = AsyncHelper.run(lifecycleOwner, Dispatchers.Default, block)
fun <T> runAsync(fragment: Fragment, block: SuspendFun<T>) = runAsync(fragment.viewLifecycleOwner, block)

fun <T> runAsyncUI(lifecycleOwner: LifecycleOwner? = null, block: SuspendFun<T>) = AsyncHelper.run(lifecycleOwner, Dispatchers.Main, block)
fun <T> runAsyncUI(fragment: Fragment, block: SuspendFun<T>) = runAsyncUI(fragment.viewLifecycleOwner, block)

fun launchAsync(lifecycleOwner: LifecycleOwner? = null, block: SuspendFun<Unit>) = runAsync(lifecycleOwner, block) as Job
fun launchAsync(fragment: Fragment, block: SuspendFun<Unit>) = launchAsync(fragment.viewLifecycleOwner, block)

fun launchAsyncUI(lifecycleOwner: LifecycleOwner? = null, block: SuspendFun<Unit>) = runAsyncUI(lifecycleOwner, block) as Job
fun launchAsyncUI(fragment: Fragment, block: SuspendFun<Unit>) = launchAsyncUI(fragment.viewLifecycleOwner, block)

fun CoroutineScope.bg(block: SuspendFun<Unit>) = launch(Dispatchers.Default, block = block)
fun CoroutineScope.ui(block: SuspendFun<Unit>) = launch(Dispatchers.Main, block = block)

fun <T> CoroutineScope.bgTask(block: SuspendFun<T>) = async(Dispatchers.Default, block = block)
fun <T> CoroutineScope.uiTask(block: SuspendFun<T>) = async(Dispatchers.Main, block = block)

suspend fun <T> suspendAsync(timeout: Long = -1, resume: ((T) -> Unit) -> Unit): AsyncResult<T?> = coroutineScope {
    async {
        if (timeout > 0) withTimeoutOrNull(timeout) { suspendCancellableCoroutine<T> { out -> resume(out::resume) } }
        else suspendCoroutine<T> { out -> resume(out::resume) }
    }
}