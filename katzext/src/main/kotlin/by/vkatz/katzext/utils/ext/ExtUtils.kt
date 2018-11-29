package by.vkatz.katzext.utils.ext

import java.text.NumberFormat
import java.util.*

@Suppress("unused")
infix fun <T> Any?.so(t: T) = t

fun <T : Comparable<T>> T.clamp(a: T, b: T): T {
    val max = maxOf(a, b)
    val min = minOf(a, b)
    if (this < min) return min
    if (this > max) return max
    return this
}

fun Float.closeTo(value: Float, range: Float = 0.001f) = Math.abs(this - value) <= range

fun Double.closeTo(value: Double, range: Double) = Math.abs(this - value) <= range

infix fun Long.hasFlag(flag: Long): Boolean = (this and flag) == flag

infix fun Int.hasFlag(flag: Int): Boolean = (this and flag) == flag

infix fun Long.unOr(flag: Long): Long = this and flag.inv()

infix fun Int.unOr(flag: Int): Int = this and flag.inv()

fun Int.times(action: () -> Unit) {
    for (i in 0 until this) action()
}

fun Double.toCurrency(currencyCode: String): String {
    val format = NumberFormat.getCurrencyInstance()
    format.currency = Currency.getInstance(currencyCode)
    format.minimumFractionDigits = 2
    return format.format(this)
}

fun Calendar.set(year: Int? = null,
                 month: Int? = null,
                 dayOfMonth: Int? = null,
                 dayOfWeek: Int? = null,
                 hrs: Int? = null,
                 min: Int? = null,
                 sec: Int? = null,
                 milis: Int? = null): Calendar {
    year?.let { set(Calendar.YEAR, it) }
    month?.let { set(Calendar.MONTH, it) }
    dayOfMonth?.let { set(Calendar.DAY_OF_MONTH, it) }
    dayOfWeek?.let { set(Calendar.DAY_OF_WEEK, it) }
    hrs?.let { set(Calendar.HOUR_OF_DAY, it) }
    min?.let { set(Calendar.MINUTE, it) }
    sec?.let { set(Calendar.SECOND, it) }
    milis?.let { set(Calendar.MILLISECOND, it) }
    return this
}

fun Date.toUTC(): Date = Date(time - TimeZone.getDefault().rawOffset)

fun <T> Iterable<T>.toArrayList() = ArrayList<T>().apply { addAll(this@toArrayList) }

fun <T> List<T>?.isNullOrEmpty() = this?.isEmpty() ?: true