package by.vkatz.katzext.utils

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Handler
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import by.vkatz.katzext.widgets.ExtendEditText
import by.vkatz.katzext.widgets.ExtendImageView
import by.vkatz.katzext.widgets.ExtendTextView
import by.vkatz.katzext.widgets.SlideMenuLayout


@Suppress("unused")
infix fun <T> Any?.so(t: T) = t

fun <T> List<T>.toArrayList() = ArrayList(this)

fun Handler.postDelayed(delay: Long, action: () -> Unit) = postDelayed(action, delay)

fun Context?.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    if (this != null) Toast.makeText(this, resId, duration).show()
}

fun Context?.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    if (this != null) Toast.makeText(this, text, duration).show()
}

fun Context.dp(amount: Float) = amount * resources.displayMetrics.density

fun Context.inflate(@LayoutRes rId: Int, parent: ViewGroup? = null, attachToParent: Boolean = false): View = LayoutInflater.from(this).inflate(rId, parent, attachToParent)

fun Fragment.inflate(@LayoutRes rId: Int, parent: ViewGroup? = null, attachToParent: Boolean = false) = activity!!.inflate(rId, parent, attachToParent)

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

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun ViewGroup.forEachChildren(action: (view: View) -> Unit) {
    for (i in 0 until childCount) action(getChildAt(i))
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


