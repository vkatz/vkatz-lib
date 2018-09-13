@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package by.vkatz.katzext.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

//usual live data observers

fun <T> LiveData<T>.observe(fragment: Fragment, observer: (T) -> Unit): Observer<T> = observe(fragment.viewLifecycleOwner, observer)
fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit): Observer<T> {
    val obs = AppObserver<T> { _, _, t -> observer(t) }
    this.observe(owner, obs)
    return obs
}

fun <T> LiveData<T>.observeOnce(fragment: Fragment, observer: (T) -> Unit) = observeOnce(fragment.viewLifecycleOwner, observer)
fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit): Observer<T> {
    val obs = AppObserver<T> { s, _, t ->
        observer(t)
        removeObserver(s)
    }
    this.observe(owner, obs)
    return obs
}

fun <T> LiveData<T>.observeLater(fragment: Fragment, observer: (T) -> Unit) = observeLater(fragment.viewLifecycleOwner, observer)
fun <T> LiveData<T>.observeLater(owner: LifecycleOwner, observer: (T) -> Unit): Observer<T> {
    val obs = AppObserver<T> { _, c, t -> if (c != 0) observer(t) }
    this.observe(owner, obs)
    return obs
}

fun <T> LiveData<T>.observeLaterOnce(fragment: Fragment, observer: (T) -> Unit) = observeLaterOnce(fragment.viewLifecycleOwner, observer)
fun <T> LiveData<T>.observeLaterOnce(owner: LifecycleOwner, observer: (T) -> Unit): Observer<T> {
    val obs = AppObserver<T> { s, c, t ->
        if (c == 1) {
            observer(t)
            removeObserver(s)
        }
    }
    this.observe(owner, obs)
    return obs
}

fun <T> LiveData<T>.observeUntil(fragment: Fragment, condition: (T) -> Boolean, observer: (T) -> Unit) = observeUntil(fragment.viewLifecycleOwner, condition, observer)
fun <T> LiveData<T>.observeUntil(owner: LifecycleOwner, condition: (T) -> Boolean, observer: (T) -> Unit): Observer<T> {
    val obs = AppObserver<T> { s, _, t ->
        observer(t)
        if (condition(t)) removeObserver(s)
    }
    this.observe(owner, obs)
    return obs
}

fun <T> LiveData<T>.observeAnywhere(observer: (T) -> Unit): Observer<T> {
    val obs = AppObserver<T> { _, _, t -> observer(t) }
    this.observeForever(obs)
    return obs
}

fun <T> LiveData<T>.observeAnywhereOnce(observer: (T) -> Unit): Observer<T> {
    val obs = AppObserver<T> { s, _, t ->
        observer(t)
        removeObserver(s)
    }
    this.observeForever(obs)
    return obs
}

fun <T> LiveData<T>.observeAnywhereLater(observer: (T) -> Unit): Observer<T> {
    val obs = AppObserver<T> { _, c, t -> if (c != 0) observer(t) }
    this.observeForever(obs)
    return obs
}

fun <T> LiveData<T>.observeAnywhereLaterOnce(observer: (T) -> Unit): Observer<T> {
    val obs = AppObserver<T> { s, c, t ->
        if (c == 1) {
            observer(t)
            removeObserver(s)
        }
    }
    this.observeForever(obs)
    return obs
}

fun <T> LiveData<T>.observeAnywhereUntil(condition: (T) -> Boolean, observer: (T) -> Unit): Observer<T> {
    val obs = AppObserver<T> { s, _, t ->
        observer(t)
        if (condition(t)) removeObserver(s)
    }
    this.observeForever(obs)
    return obs
}


// loadable live data observers

fun <T> LiveData<LoadableLiveDataState<T>>.observeLoaded(fragment: Fragment, observer: (T) -> Unit) = observeLoaded(fragment.viewLifecycleOwner, observer)
fun <T> LiveData<LoadableLiveDataState<T>>.observeLoaded(owner: LifecycleOwner, observer: (T) -> Unit): Observer<LoadableLiveDataState<T>> {
    val obs = AppObserver<LoadableLiveDataState<T>> { _, _, t -> if (!t.isLoading) observer(t.data) }
    this.observe(owner, obs)
    return obs
}

fun <T> LiveData<LoadableLiveDataState<T>>.observeLoadedOnce(fragment: Fragment, observer: (T) -> Unit) = observeLoadedOnce(fragment.viewLifecycleOwner, observer)
fun <T> LiveData<LoadableLiveDataState<T>>.observeLoadedOnce(owner: LifecycleOwner, observer: (T) -> Unit): Observer<LoadableLiveDataState<T>> {
    val obs = AppObserver<LoadableLiveDataState<T>> { s, _, t ->
        if (!t.isLoading) {
            observer(t.data)
            removeObserver(s)
        }
    }
    this.observe(owner, obs)
    return obs
}

fun <T> LiveData<LoadableLiveDataState<T>>.observeLoadedAnywhere(observer: (T) -> Unit): Observer<LoadableLiveDataState<T>> {
    val obs = AppObserver<LoadableLiveDataState<T>> { _, _, t -> if (!t.isLoading) observer(t.data) }
    this.observeForever(obs)
    return obs
}

fun <T> LiveData<LoadableLiveDataState<T>>.observeLoadedAnywhereOnce(observer: (T) -> Unit): Observer<LoadableLiveDataState<T>> {
    val obs = AppObserver<LoadableLiveDataState<T>> { s, _, t ->
        if (!t.isLoading) {
            observer(t.data)
            removeObserver(s)
        }
    }
    this.observeForever(obs)
    return obs
}


//helper classes

/**
 * Live data ext that have fixed initial value and null safe
 */
open class AppLiveData<T>(initialValue: T) : MutableLiveData<T>() {
    init {
        value = initialValue
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(): T = super.getValue() as T

    open fun asImmutable() = this as LiveData<T>
}

/**
 * LiveData ext that provide ability to load data async & observe "loading" state
 */
open class LoadableLiveData<T> : AppLiveData<LoadableLiveDataState<T>> {
    private var task: AsyncResult<*>? = null
    private var name: String? = null

    val data get() = value.data
    val isLoading get() = value.isLoading

    constructor(initialValue: T) : super(LoadableLiveDataState(initialValue, false))

    constructor(name: String, initialValue: T) : super(LoadableLiveDataState(initialValue, false)) {
        this.name = name
    }

    fun load(forceLoading: Boolean = true, loader: suspend () -> T) {
        if (value.isLoading && !forceLoading) return//@asyncUI
        task?.cancel()
        value = LoadableLiveDataState(value.data, true)
        task = async { postValue(LoadableLiveDataState(loader(), false)) }
    }

    fun cancelLoading() {
        task?.cancel()
        task = null
    }

    fun setValue(value: T, isLoading: Boolean = false) {
        setValue(LoadableLiveDataState(value, isLoading))
    }

    override fun setValue(value: LoadableLiveDataState<T>) {
        cancelLoading()
        super.setValue(value)
    }

    fun postValue(value: T, isLoading: Boolean = false) {
        postValue(LoadableLiveDataState(value, isLoading))
    }
}

/**
 * LoadableLiveData state class
 */
data class LoadableLiveDataState<T>(val data: T, val isLoading: Boolean)

/**
 * Wrapped live data observer to use as lambda
 */
open class AppObserver<T>(private val obsFunc: (sender: AppObserver<T>, version: Int, data: T) -> Unit) : Observer<T> {
    private var changes = 0

    @Suppress("UNCHECKED_CAST")
    override fun onChanged(t: T?) {
        obsFunc(this, changes++, t as T)
    }
}