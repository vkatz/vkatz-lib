package by.vkatz.katzext.utils

import android.content.SharedPreferences
import android.os.Bundle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class SharedPrefDelegate<T>(private val sharedPrefs: () -> SharedPreferences,
                                 private val default: T,
                                 private val reader: SharedPreferences.(field: String, default: T) -> T,
                                 private val writer: SharedPreferences.Editor.(field: String, value: T) -> SharedPreferences.Editor
                                ) : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T = sharedPrefs().reader(property.name, default)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = sharedPrefs().edit().writer(property.name, value).apply()
}

class SharedPrefsStringDelegate(sharedPrefs: () -> SharedPreferences, default: String)
    : SharedPrefDelegate<String>(sharedPrefs, default, SharedPreferences::getString, SharedPreferences.Editor::putString)

class SharedPrefsStringSetDelegate(sharedPrefs: () -> SharedPreferences, default: Set<String>)
    : SharedPrefDelegate<Set<String>>(sharedPrefs, default, SharedPreferences::getStringSet, SharedPreferences.Editor::putStringSet)

class SharedPrefsIntDelegate(sharedPrefs: () -> SharedPreferences, default: Int)
    : SharedPrefDelegate<Int>(sharedPrefs, default, SharedPreferences::getInt, SharedPreferences.Editor::putInt)

class SharedPrefsLongDelegate(sharedPrefs: () -> SharedPreferences, default: Long)
    : SharedPrefDelegate<Long>(sharedPrefs, default, SharedPreferences::getLong, SharedPreferences.Editor::putLong)

class SharedPrefsFloatDelegate(sharedPrefs: () -> SharedPreferences, default: Float)
    : SharedPrefDelegate<Float>(sharedPrefs, default, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat)

class SharedPrefsBooleanDelegate(sharedPrefs: () -> SharedPreferences, default: Boolean)
    : SharedPrefDelegate<Boolean>(sharedPrefs, default, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)

open class BundleDelegate<T>(private val bundle: () -> Bundle,
                             private val reader: Bundle.(field: String) -> T,
                             private val writer: Bundle.(field: String, value: T) -> Unit) : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T = bundle().reader(property.name)
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = bundle().writer(property.name, value)
}

open class BundleWithDefaultDelegate<T>(private val bundle: () -> Bundle,
                                        private val default: T,
                                        private val reader: Bundle.(field: String, default: T) -> T,
                                        private val writer: Bundle.(field: String, value: T) -> Unit
                                       ) : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T = bundle().reader(property.name, default)
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = bundle().writer(property.name, value)
}