package by.vkatz.katzext.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

/**
 * Created by V on 24.04.2018.
 */

open class AppLiveData<T>(initialValue: T) : MutableLiveData<T>() {
    init {
        value = initialValue
    }

    override fun getValue(): T = super.getValue()!!

    fun observe(owner: LifecycleOwner, observer: (T) -> Unit) {
        super.observe(owner, Observer { observer(it!!) })
    }
}