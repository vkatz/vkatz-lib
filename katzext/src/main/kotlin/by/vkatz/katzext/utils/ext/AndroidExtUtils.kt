package by.vkatz.katzext.utils.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.AttrRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

fun Context?.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    if (this != null) Toast.makeText(this, resId, duration).show()
}

fun Context?.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    if (this != null) Toast.makeText(this, text, duration).show()
}

fun Handler.postDelayed(delay: Long, action: () -> Unit) = postDelayed(action, delay)

fun Context.dp(amount: Float) = amount * resources.displayMetrics.density

fun Context.getThemeColor(@AttrRes attr: Int, defaultColor: Int = 0): Int {
    val outValue = TypedValue()
    val wasResolved = theme.resolveAttribute(attr, outValue, true)
    return if (wasResolved) outValue.data else defaultColor
}

fun View.inflate(@LayoutRes rId: Int, parent: ViewGroup? = null, attachToParent: Boolean = false) = context.inflate(rId, parent, attachToParent)
fun Context.inflate(@LayoutRes rId: Int, parent: ViewGroup? = null, attachToParent: Boolean = false): View = LayoutInflater.from(this).inflate(rId, parent, attachToParent)
fun Fragment.inflate(@LayoutRes rId: Int, parent: ViewGroup? = null, attachToParent: Boolean = false) = activity!!.inflate(rId, parent, attachToParent)

fun Activity.hideKeyboard(focus: View? = null) {
    (focus ?: currentFocus)?.hideKeyboard()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun Context.launchGeo(lat: Double, lng: Double, query: String = "") {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lng?q=${if (query.isBlank()) "$lat,$lng" else query}")))
}

fun Context.launchPhone(phone: String) {
    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
}

fun Context.launchAppSettings() {
    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                          .addCategory(Intent.CATEGORY_DEFAULT)
                          .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
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

fun EditText.addOnTextChangedListener(listener: (String) -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener(s?.toString() ?: "")
        }
    }
    this.addTextChangedListener(watcher)
    return watcher
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

fun View.asViewGroup() = this as ViewGroup
fun View.asLinearLayout() = this as LinearLayout
fun View.asRelativeLayout() = this as RelativeLayout
fun View.asTextView() = this as TextView
fun View.asEditText() = this as EditText
fun View.asImageView() = this as ImageView