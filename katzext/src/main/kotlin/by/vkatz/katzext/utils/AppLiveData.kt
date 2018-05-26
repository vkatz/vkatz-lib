package by.vkatz.katzext.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * Created by V on 24.04.2018.
 */

@Suppress("UNCHECKED_CAST")
open class AppLiveData<T>(initialValue: T) : MutableLiveData<T>() {
    init {
        value = initialValue
    }

    @Suppress("UNCHECKED_CAST")
    fun observe(owner: LifecycleOwner, observer: (T) -> Unit) {
        super.observe(owner, Observer { observer(it as T) })
    }

    override fun getValue(): T {
        return super.getValue() as T
    }

    @Suppress("RedundantOverride")
    override fun postValue(value: T) {
        super.postValue(value)
    }
}