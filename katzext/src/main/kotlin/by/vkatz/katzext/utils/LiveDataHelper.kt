package by.vkatz.katzext.utils

import androidx.annotation.UiThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * Created by V on 24.04.2018.
 */

@Suppress("UNCHECKED_CAST", "unused")
open class AppLiveData<T>(initialValue: T) : MutableLiveData<T>() {

    init {
        value = initialValue
    }

    fun observe(owner: LifecycleOwner, observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { _, _, t -> observer(t) }
        super.observe(owner, obs)
        return obs
    }

    fun observeOnce(owner: LifecycleOwner, observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { s, _, t ->
            observer(t)
            removeObserver(s)
        }
        super.observe(owner, obs)
        return obs
    }

    fun observeLater(owner: LifecycleOwner, observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { _, c, t ->
            if (c != 0) {
                observer(t)
            }
        }
        super.observe(owner, obs)
        return obs
    }

    fun observeLaterOnce(owner: LifecycleOwner, observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { s, c, t ->
            if (c == 1) {
                observer(t)
                removeObserver(s)
            }
        }
        super.observe(owner, obs)
        return obs
    }

    fun observeUntil(owner: LifecycleOwner, condition: (T) -> Boolean, observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { s, _, t ->
            observer(t)
            if (condition(t)) removeObserver(s)
        }
        super.observe(owner, obs)
        return obs
    }

    fun observe(observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { _, _, t -> observer(t) }
        super.observeForever(obs)
        return obs
    }

    fun observeOnce(observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { s, _, t ->
            observer(t)
            removeObserver(s)
        }
        super.observeForever(obs)
        return obs
    }

    fun observeLater(observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { _, c, t ->
            if (c != 0) {
                observer(t)
            }
        }
        super.observeForever(obs)
        return obs
    }

    fun observeLaterOnce(observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { s, c, t ->
            if (c == 1) {
                observer(t)
                removeObserver(s)
            }
        }
        super.observeForever(obs)
        return obs
    }

    fun observeUntil(condition: (T) -> Boolean, observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { s, _, t ->
            observer(t)
            if (condition(t)) removeObserver(s)
        }
        super.observeForever(obs)
        return obs
    }

    override fun getValue(): T {
        return super.getValue() as T
    }

    @Suppress("RedundantOverride")
    override fun postValue(value: T) {
        super.postValue(value)
    }
}

open class LoadableLiveData<T>(initialValue: T) : AppLiveData<T>(initialValue) {
    private var task: AsyncResult<*>? = null

    val isLoading = AppLiveData(false)

    init {
        observe { isLoading.value = false }
    }

    @UiThread
    fun load(forceLoading: Boolean = true, loader: suspend () -> T) {
        asyncUI {
            if (isLoading.value && !forceLoading) return@asyncUI
            task?.cancel()
            isLoading.value = true
            task = async { postValue(loader()) }
        }
    }

    fun cancelLoading() {
        task?.cancel()
        task = null
    }

    fun observeLoaded(owner: LifecycleOwner, observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { _, _, t ->
            if (!isLoading.value) {
                observer(t)
            }
        }
        super.observe(owner, obs)
        return obs
    }

    fun observeLoadedOnce(owner: LifecycleOwner, observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { s, _, t ->
            if (!isLoading.value) {
                observer(t)
                removeObserver(s)
            }
        }
        super.observe(owner, obs)
        return obs
    }

    fun observeLoaded(observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { _, _, t ->
            if (!isLoading.value) {
                observer(t)
            }
        }
        super.observeForever(obs)
        return obs
    }

    fun observeLoadedOnce(observer: (T) -> Unit): Observer<T> {
        val obs = AppObserver<T> { s, _, t ->
            if (!isLoading.value) {
                observer(t)
                removeObserver(s)
            }
        }
        super.observeForever(obs)
        return obs
    }
}

open class AppObserver<T>(private val obsFunc: (sender: AppObserver<T>, version: Int, data: T) -> Unit) : Observer<T> {
    private var changes = 0

    @Suppress("UNCHECKED_CAST")
    override fun onChanged(t: T?) {
        obsFunc(this, changes++, t as T)
    }
}