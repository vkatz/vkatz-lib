@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package by.vkatz.katzext.utils

import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import by.vkatz.katzext.utils.ext.isNullOrEmpty
import by.vkatz.katzext.utils.ext.so
import by.vkatz.katzext.utils.ext.toArrayList

//usual live data observers

/**
 * priority order: canceling->filter->skip->count
 *
 * @param owner lifecycle to bind onto (on case  owner is Fragment -> fragment.viewLifecycleOwner will be extracted)
 * @param cancelOn declare observer self removal condition
 * @param observeCanceling should we observe the value that cause canceling by #cancelOn
 * @param filter skip condition
 * @param after trigger - if provided: default state of observer will be 'disabled' until this trigger and all obs will be ignored
 * @param skip how many items (passed #filter if presented) should be skipped
 * @param count max amount of successful observes before self removal
 * @param tag tag line, useful for debug
 * @param observer observer itself
 */
fun <T> LiveData<T>.observeWith(owner: LifecycleOwner? = null,
                                cancelOn: ((T) -> Boolean)? = null,
                                observeCanceling: Boolean = true,
                                filter: ((T) -> Boolean)? = null,
                                after: ((T) -> Boolean)? = null,
                                skip: Int = 0,
                                count: Int = 0,
                                tag: String? = null,
                                observer: (T) -> Unit): Observer<T> {
    val obs = AppObserver<T> { s, data ->
        if (after != null && !s.enabled) s.enabled = after(data)
        when {
            !s.enabled -> AppObserver.State.IGNORED
            cancelOn != null && cancelOn(data) -> {
                if (observeCanceling) observer(data)
                removeObserver(s)
                if (observeCanceling) AppObserver.State.OBSERVED
                else AppObserver.State.SKIPPED
            }
            filter != null && !filter(data) -> AppObserver.State.SKIPPED
            skip > s.skipped -> AppObserver.State.SKIPPED
            else -> {
                observer(data)
                if (count == s.observed + 1) removeObserver(s)
                AppObserver.State.OBSERVED
            }
        }
    }
    obs.enabled = after == null
    obs.tag = tag
    val lifecycle = (owner as? Fragment)?.viewLifecycleOwner ?: owner
    if (lifecycle != null) this.observe(lifecycle, obs)
    else this.observeForever(obs)
    return obs
}


fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit) = observeWith(owner, observer = observer)
fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) = observeWith(owner, count = 1, observer = observer)
fun <T> LiveData<T>.observeLater(owner: LifecycleOwner, observer: (T) -> Unit) = observeWith(owner, skip = 1, observer = observer)
fun <T> LiveData<T>.observeLaterOnce(owner: LifecycleOwner, observer: (T) -> Unit) = observeWith(owner, skip = 1, count = 1, observer = observer)
fun <T> LiveData<T>.observeUntil(owner: LifecycleOwner, cancelOn: (T) -> Boolean, observeCanceling: Boolean = true, observer: (T) -> Unit) =
        observeWith(owner, cancelOn = cancelOn, observeCanceling = observeCanceling, observer = observer)


fun <T> LiveData<T>.observeAnywhere(observer: (T) -> Unit) = observeWith(observer = observer)
fun <T> LiveData<T>.observeAnywhereOnce(observer: (T) -> Unit) = observeWith(count = 1, observer = observer)
fun <T> LiveData<T>.observeAnywhereLater(observer: (T) -> Unit) = observeWith(skip = 1, observer = observer)
fun <T> LiveData<T>.observeAnywhereLaterOnce(observer: (T) -> Unit) = observeWith(skip = 1, count = 1, observer = observer)
fun <T> LiveData<T>.observeAnywhereUntil(cancelOn: (T) -> Boolean, observeCanceling: Boolean = true, observer: (T) -> Unit) =
        observeWith(cancelOn = cancelOn, observeCanceling = observeCanceling, observer = observer)


// loadable live data observers

fun <T> LiveData<LoadableLiveDataState<T>>.observeLoaded(owner: LifecycleOwner, observer: (T) -> Unit) =
        observeWith(owner, filter = { !it.isLoading }, observer = { observer(it.data) })

fun <T> LiveData<LoadableLiveDataState<T>>.observeLoadedOnce(owner: LifecycleOwner, observer: (T) -> Unit) =
        observeWith(owner, filter = { !it.isLoading }, count = 1, observer = { observer(it.data) })

fun <T> LiveData<LoadableLiveDataState<T>>.observeLoadedAnywhere(observer: (T) -> Unit) =
        observeWith(filter = { !it.isLoading }, observer = { observer(it.data) })

fun <T> LiveData<LoadableLiveDataState<T>>.observeLoadedAnywhereOnce(observer: (T) -> Unit) =
        observeWith(filter = { !it.isLoading }, count = 1, observer = { observer(it.data) })

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
 * Live data object, will read initial value from prefs & write into prefs on change
 */
open class SharedPrefsLiveData<T>(private val prefs: () -> SharedPreferences,
                                  private val key: String,
                                  private val reader: (prefs: SharedPreferences, key: String) -> T,
                                  private val writer: (prefs: SharedPreferences, key: String, value: T) -> Unit) : MutableLiveData<T>() {

    companion object {
        private fun <T> ofPrimitive(prefs: () -> SharedPreferences, key: String, defaultValue: T,
                                    reader: SharedPreferences.(String, T) -> T,
                                    writer: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor) =
                SharedPrefsLiveData(prefs, key,
                                    { p, k -> p.reader(k, defaultValue) },
                                    { p, k, v -> p.edit().apply { writer(k, v) so apply() } })

        fun ofInt(prefs: SharedPreferences, key: String, defaultValue: Int) = ofInt({ prefs }, key, defaultValue)
        fun ofInt(prefs: () -> SharedPreferences, key: String, defaultValue: Int) =
                ofPrimitive(prefs, key, defaultValue, SharedPreferences::getInt, SharedPreferences.Editor::putInt)

        fun ofString(prefs: SharedPreferences, key: String, defaultValue: String) = ofString({ prefs }, key, defaultValue)
        fun ofString(prefs: () -> SharedPreferences, key: String, defaultValue: String) =
                ofPrimitive(prefs, key, defaultValue, SharedPreferences::getString, SharedPreferences.Editor::putString)

        fun ofBool(prefs: SharedPreferences, key: String, defaultValue: Boolean) = ofBool({ prefs }, key, defaultValue)
        fun ofBool(prefs: () -> SharedPreferences, key: String, defaultValue: Boolean) =
                ofPrimitive(prefs, key, defaultValue, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)

        fun ofFloat(prefs: SharedPreferences, key: String, defaultValue: Float) = ofFloat({ prefs }, key, defaultValue)
        fun ofFloat(prefs: () -> SharedPreferences, key: String, defaultValue: Float) =
                ofPrimitive(prefs, key, defaultValue, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat)

        fun ofLong(prefs: SharedPreferences, key: String, defaultValue: Long) = ofLong({ prefs }, key, defaultValue)
        fun ofLong(prefs: () -> SharedPreferences, key: String, defaultValue: Long) =
                ofPrimitive(prefs, key, defaultValue, SharedPreferences::getLong, SharedPreferences.Editor::putLong)

        fun <T> ofObj(prefs: SharedPreferences, key: String, serializer: (T) -> String, deserializer: (String) -> T, defaultValueProvider: () -> T) =
                ofObj({ prefs }, key, serializer, deserializer, defaultValueProvider)

        fun <T> ofObj(prefs: () -> SharedPreferences, key: String, serializer: (T) -> String, deserializer: (String) -> T, defaultValueProvider: () -> T) =
                SharedPrefsLiveData(prefs, key,
                                    { p, k -> p.getString(k, null)?.let { deserializer(it) } ?: defaultValueProvider() },
                                    { p, k, v -> p.edit().apply { putString(k, serializer(v)) so apply() } })
    }

    private var initialized = false

    init {
        sync()
        initialized = true
    }

    final override fun setValue(value: T) {
        if (initialized) synchronized(prefs) { writer(prefs(), key, value) }
        super.setValue(value)
    }

    fun sync() {
        value = reader(prefs(), key)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(): T = super.getValue() as T

    open fun asImmutable() = this as LiveData<T>
}

/**
 * LiveData ext that provide ability to load data async & observe "loading" state
 */
open class LoadableLiveData<T>(initialValue: T) : AppLiveData<LoadableLiveDataState<T>>(LoadableLiveDataState(initialValue, false)) {
    private var task: AsyncResult<*>? = null

    val data get() = value.data
    val isLoading get() = value.isLoading

    fun load(forceLoading: Boolean = true, loader: SuspendFun<T>) {
        if (value.isLoading && !forceLoading) return//@asyncUI
        task?.cancel()
        value = LoadableLiveDataState(value.data, true)
        task = runAsync { postValue(LoadableLiveDataState(loader(), false)) }
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
 * LiveDataHelper for pagination
 *
 * @param itemsPerPage optional param to validate 'hasMorePages' value as "loadedItems.size<itemsPerPage"
 * @param totalItems optional param to validate 'hasMorePages' value as "allItems.size<totalItems"
 */
open class PaginationLiveData<T>(initialValue: List<T>, var itemsPerPage: Int? = null, var totalItems: Int? = null) : AppLiveData<PaginationLiveDataState<T>>(
        PaginationLiveDataState(initialValue.toArrayList(), false, false, false, 0, true)) {

    private var task: AsyncResult<*>? = null

    val data get() = value.data
    val isLoading get() = value.isLoading
    val error get() = value.error
    val reset get() = value.reset
    val pagesCount get() = value.pagesCount
    val hasMorePages get() = value.hasMorePages


    fun load(forceLoading: Boolean = true, reset: Boolean = false, loader: SuspendFun<List<T>?>) {
        if (value.isLoading && !forceLoading) return//@asyncUI
        task?.cancel()
        value = PaginationLiveDataState(value.data, true, false, reset, value.pagesCount, value.hasMorePages)
        task = runAsync {
            val items = loader()
            val newData = ArrayList<T>()
            if (!reset) newData.addAll(data)
            if (items != null) newData.addAll(items)
            val newHasMorePages = when {
                totalItems != null -> newData.size < totalItems!!
                itemsPerPage != null -> (items?.size ?: 0) < itemsPerPage!!
                else -> !items.isNullOrEmpty()
            }
            val newPagesCount = (if (reset) 0 else pagesCount) + (if (newHasMorePages) 1 else 0)
            postValue(PaginationLiveDataState(newData, false, items == null, false, newPagesCount, newHasMorePages))
        }
    }

    fun cancelLoading() {
        task?.cancel()
        task = null
    }

    fun reset() {
        cancelLoading()
        value = PaginationLiveDataState(ArrayList(), false, false, false, 0, true)
    }
}

/**
 * PaginationLiveData state
 */
data class PaginationLiveDataState<T>(val data: ArrayList<T>, val isLoading: Boolean, val error: Boolean, val reset: Boolean, val pagesCount: Int, val hasMorePages: Boolean)


/**
 * Wrapped live data observer to use as lambda
 */
open class AppObserver<T>(private val obsFunc: (sender: AppObserver<T>, data: T) -> State) : Observer<T> {
    var skipped: Int = 0
    var observed: Int = 0
    var enabled: Boolean = true
    var tag: String? = null

    @Suppress("UNCHECKED_CAST")
    override fun onChanged(t: T?) {
        when (obsFunc(this, t as T)) {
            State.SKIPPED -> skipped++
            State.OBSERVED -> observed++
            State.IGNORED -> Unit
        }
    }

    enum class State { SKIPPED, OBSERVED, IGNORED }
}