package by.vkatz.katzext.utils

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Shared prefs delegate, used to control delegate access flow & prevent errors related to access & type
 * @param prefs prefs to delegate on
 * @param key element key
 * @param defaultValue default value of param (Supported types: [String], [Int], [Boolean], [Float], [Long])
 */
class SharedPrefsDelegate<T>(private val prefs: () -> SharedPreferences, private val key: String, val defaultValue: T) : ReadWriteProperty<Any?, T> {
    constructor(prefs: SharedPreferences, key: String, defaultValue: T) : this({ prefs }, key, defaultValue)

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        synchronized(prefs) {
            return when (defaultValue) {
                is String -> prefs().getString(key, defaultValue)
                is Int -> prefs().getInt(key, defaultValue)
                is Boolean -> prefs().getBoolean(key, defaultValue)
                is Float -> prefs().getFloat(key, defaultValue)
                is Long -> prefs().getLong(key, defaultValue)
                else -> throw UnsupportedOperationException("Not yet implemented")
            } as T
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        synchronized(prefs) {
            val editor = prefs().edit()
            when (value) {
                is String -> editor.putString(key, value)
                is Int -> editor.putInt(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Float -> editor.putFloat(key, value)
                is Long -> editor.putLong(key, value)
                else -> throw UnsupportedOperationException("Not yet implemented")
            }
            editor.apply()
        }
    }
}