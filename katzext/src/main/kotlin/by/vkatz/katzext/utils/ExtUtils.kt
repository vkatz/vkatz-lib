package by.vkatz.katzext.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.AttrRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*
import kotlin.reflect.KClass

@Suppress("unused")
infix fun <T> Any?.so(t: T) = t

fun <T : Comparable<T>> T.clamp(a: T, b: T): T {
    val max = maxOf(a, b)
    val min = minOf(a, b)
    if (this < min) return min
    if (this > max) return max
    return this
}

fun Float.closeTo(value: Float, range: Float = 0.001f): Boolean {
    return Math.abs(this - value) <= range
}

fun Double.closeTo(value: Double, range: Double): Boolean {
    return Math.abs(this - value) <= range
}

infix fun Long.hasFlag(flag: Long): Boolean = (this and flag) == flag

infix fun Int.hasFlag(flag: Int): Boolean = (this and flag) == flag

infix fun Long.unOr(flag: Long): Long = this and flag.inv()

infix fun Int.unOr(flag: Int): Int = this and flag.inv()

fun Int.times(action: () -> Unit) {
    for (i in 0 until this) action()
}

fun <T : ViewModel> ViewModelProvider.get(clazz: KClass<T>) = get(clazz.java)

fun <T> Iterable<T>.toArrayList() = ArrayList<T>().apply { addAll(this@toArrayList) }

fun <T> List<T>?.isNullOrEmpty() = this?.isEmpty() ?: true

fun Handler.postDelayed(delay: Long, action: () -> Unit) = postDelayed(action, delay)

fun Context?.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    if (this != null) Toast.makeText(this, resId, duration).show()
}

fun Context?.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    if (this != null) Toast.makeText(this, text, duration).show()
}

fun Context.dp(amount: Float) = amount * resources.displayMetrics.density

fun Context.getThemeColor(@AttrRes attr: Int, defaultColor: Int = 0): Int {
    val outValue = TypedValue()
    val wasResolved = theme.resolveAttribute(attr, outValue, true)
    return if (wasResolved) outValue.data else defaultColor
}

fun Context.inflate(@LayoutRes rId: Int, parent: ViewGroup? = null, attachToParent: Boolean = false): View = LayoutInflater.from(this).inflate(rId, parent, attachToParent)

fun Fragment.inflate(@LayoutRes rId: Int, parent: ViewGroup? = null, attachToParent: Boolean = false) = activity!!.inflate(rId, parent, attachToParent)

fun LayoutInflater.inflate(@LayoutRes rId: Int): View = inflate(rId, null, false)

fun View.inflate(@LayoutRes rId: Int, parent: ViewGroup? = null, attachToParent: Boolean = false) = context.inflate(rId, parent, attachToParent)

fun Activity.hideKeyboard(focus: View? = null) {
    val view = focus ?: this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Activity.showKeyboard(focus: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(focus, 0)
}

fun Context.launchGeoIntent(lat: Double, lng: Double) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lng")))
}

fun Context.launchPhoneIntent(phone: String) {
    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
}

fun Context.launchShareIntent(title: String, subject: String? = null, extra: String? = null) {
    startActivity(Intent().apply {
        type = "text/plain"
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TITLE, title)
        if (subject != null) putExtra(Intent.EXTRA_SUBJECT, subject)
        if (extra != null) putExtra(Intent.EXTRA_TEXT, extra)
    })
}

operator fun View.get(id: Int): View = findViewById<View>(id)

fun View.setAsyncOnClickListener(listener: suspend (view: View) -> Unit) = setOnClickListener { view -> asyncUI { listener(view) } }

fun View.setLockableOnClickListener(lockTime: Long = 500, listener: (View) -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        var lockTimer = 0L

        override fun onClick(sender: View) {
            if (System.currentTimeMillis() > lockTimer) {
                listener(sender)
                lockTimer = System.currentTimeMillis() + lockTime
            }
        }
    })
}

fun View.postRequestLayout() = post { requestLayout() }

fun <T : View> T.makeVisible(): T {
    visibility = View.VISIBLE
    return this
}

fun <T : View> T.makeInvisible(): T {
    visibility = View.INVISIBLE
    return this
}

fun <T : View> T.makeGone(): T {
    visibility = View.GONE
    return this
}

fun <T : View> T.makeVisibleOrGone(visible: Boolean): T {
    visibility = if (visible) View.VISIBLE else View.GONE
    return this
}

fun <T : View> T.makeVisibleOrInvisible(visible: Boolean): T {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
    return this
}

fun ViewGroup.forEachChildren(action: (view: View) -> Unit) {
    for (i in 0 until childCount) action(getChildAt(i))
}

fun ViewGroup.forEachChildrenRecursive(action: (view: View) -> Unit) {
    for (i in 0 until childCount) {
        val view = getChildAt(i)
        action(view)
        (view as? ViewGroup)?.forEachChildrenRecursive(action)
    }
}

fun ViewGroup.forEachChildrenRecursiveConditional(action: (view: View) -> Boolean) {
    for (i in 0 until childCount) {
        val view = getChildAt(i)
        if (action(view) && view is ViewGroup) {
            view.forEachChildrenRecursiveConditional(action)
        }
    }
}

fun ViewGroup.forEachChildrenIndexed(action: (view: View, pos: Int) -> Unit) {
    for (i in 0 until childCount) action(getChildAt(i), i)
}

fun <T> T?.or(compute: () -> T): T = this ?: compute()

fun View.asViewGroup() = this as ViewGroup
fun View.asLinearLayout() = this as LinearLayout
fun View.asRelativeLayout() = this as RelativeLayout
fun View.asTextView() = this as TextView
fun View.asEditText() = this as EditText
fun View.asImageView() = this as ImageView