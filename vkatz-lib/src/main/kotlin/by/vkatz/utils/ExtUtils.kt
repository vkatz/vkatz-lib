package by.vkatz.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import by.vkatz.widgets.ExtendEditText
import by.vkatz.widgets.ExtendImageView
import by.vkatz.widgets.ExtendTextView
import by.vkatz.widgets.SlideMenuLayout


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


