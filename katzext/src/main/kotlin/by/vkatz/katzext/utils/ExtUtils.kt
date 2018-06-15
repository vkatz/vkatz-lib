package by.vkatz.katzext.utils

import android.app.Activity
import android.content.Context
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
import by.vkatz.katzext.widgets.ExtendEditText
import by.vkatz.katzext.widgets.ExtendImageView
import by.vkatz.katzext.widgets.ExtendTextView
import by.vkatz.katzext.widgets.SlideMenuLayout
import kotlin.reflect.KClass

typealias Callback = () -> Unit

typealias ValueCallback<T> = (t: T) -> Unit

@Suppress("unused")
infix fun <T> Any?.so(t: T) = t

infix fun <T> Any?.ifnn(t: T) = if (this == null) null else t

operator fun <T> Boolean.get(onTrue: T, onFalse: T) = if (this) onTrue else onFalse
operator fun <T> Boolean.get(onTrue: T) = if (this) onTrue else null

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

fun <T : ViewModel> ViewModelProvider.get(clazz: KClass<T>) = get(clazz.java)

fun <T> List<T>.toArrayList() = ArrayList(this)

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

fun LayoutInflater.inflate(@LayoutRes rId: Int) = inflate(rId, null, false)

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

operator fun View.get(id: Int): View = findViewById<View>(id)

fun View.setAsyncOnClickListener(listener: suspend (view: View) -> Unit) = setOnClickListener { view -> asyncUI { listener(view) } }

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
        if (view is ViewGroup) {
            view.forEachChildrenRecursive(action)
        }
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

fun View.asViewGroup() = this as ViewGroup
fun View.asLinearLayout() = this as LinearLayout
fun View.asRelativeLayout() = this as RelativeLayout
fun View.asTextView() = this as TextView
fun View.asEditText() = this as EditText
fun View.asImageView() = this as ImageView
fun View.asExtTextView() = this as ExtendTextView
fun View.asExtEditText() = this as ExtendEditText
fun View.asExtImageView() = this as ExtendImageView
fun View.asSlideMenu() = this as SlideMenuLayout


