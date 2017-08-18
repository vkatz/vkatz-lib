package by.vkatz.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable

import java.io.Serializable

/**
 * Created by Katz on 17.06.2016.
 */

@Suppress("MemberVisibilityCanPrivate")
class ActivityNavigator private constructor(private val activity: Activity) {
    companion object {

        fun forActivity(activity: Activity): ActivityNavigator = ActivityNavigator(activity)

        fun getData(activity: Activity): Bundle? {
            return try {
                activity.intent.extras
            } catch (e: Exception) {
                null
            }
        }
    }

    private var flags = 0
    private var animationIn = -1
    private var animationOut = -1
    private var finishCurrent = false
    private val bundle: Bundle = Bundle()
    private var options: Bundle? = null
    private var intentConfigurator: ((Intent) -> Unit)? = null

    fun finishCurrent(): ActivityNavigator {
        finishCurrent = true
        return this
    }

    fun noHistory(): ActivityNavigator {
        flags = flags or Intent.FLAG_ACTIVITY_NO_HISTORY
        return this
    }

    fun newTask(): ActivityNavigator {
        flags = flags or Intent.FLAG_ACTIVITY_NEW_TASK
        return this
    }

    fun clearTask(): ActivityNavigator {
        flags = flags or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return this
    }

    fun clearTop(): ActivityNavigator {
        flags = flags or Intent.FLAG_ACTIVITY_CLEAR_TOP
        return this
    }

    fun renewTask(): ActivityNavigator {
        newTask()
        clearTask()
        clearTop()
        return this
    }

    fun reorderToFront(): ActivityNavigator {
        flags = flags or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        return this
    }

    fun animation(animationIn: Int, animationOut: Int): ActivityNavigator {
        this.animationIn = animationIn
        this.animationOut = animationOut
        return this
    }

    fun options(options: Bundle): ActivityNavigator {
        this.options = options
        return this
    }

    fun withData(data: Bundle): ActivityNavigator {
        bundle.putAll(data)
        return this
    }

    fun withData(key: String, value: String): ActivityNavigator {
        bundle.putString(key, value)
        return this
    }

    fun withData(key: String, value: Int): ActivityNavigator {
        bundle.putInt(key, value)
        return this
    }

    fun withData(key: String, value: Float): ActivityNavigator {
        bundle.putFloat(key, value)
        return this
    }

    fun withData(key: String, value: Double): ActivityNavigator {
        bundle.putDouble(key, value)
        return this
    }

    fun withData(key: String, value: Serializable): ActivityNavigator {
        bundle.putSerializable(key, value)
        return this
    }

    fun withData(key: String, value: Parcelable): ActivityNavigator {
        bundle.putParcelable(key, value)
        return this
    }

    fun withFillData(fillBundle: (Bundle) -> Unit): ActivityNavigator {
        fillBundle(bundle)
        return this
    }

    fun configureIntent(intentConfigurator: (Intent) -> Unit): ActivityNavigator {
        this.intentConfigurator = intentConfigurator
        return this
    }

    private fun getGoIntent(activity: Class<out Activity>): Intent {
        val intent = Intent(this.activity, activity)
        intent.putExtras(bundle)
        intent.addFlags(flags)
        intentConfigurator?.invoke(intent)
        return intent
    }

    private fun applyAnimation() {
        if (animationIn != -1 || animationOut != -1)
            this.activity.overridePendingTransition(animationIn, animationOut)
    }

    fun <T : Activity> go(activity: Class<T>) {
        go(activity, null)
    }

    fun <T : Activity> go(activity: Class<T>, options: Bundle?) {
        this.activity.startActivity(getGoIntent(activity), options)
        applyAnimation()
        if (finishCurrent) this.activity.finish()
    }

    fun goForResult(activity: Class<out Activity>, requestCode: Int) {
        this.activity.startActivityForResult(getGoIntent(activity), requestCode, options)
        applyAnimation()
        if (finishCurrent) this.activity.finish()
    }

    fun back() {
        activity.finish()
        applyAnimation()
    }

    fun backWithResult(resultCode: Int) {
        val intent = Intent()
        intent.putExtras(bundle)
        activity.setResult(resultCode, intent)
        activity.finish()
    }

    fun finishActivities(requestCode: Int) {
        activity.finishActivity(requestCode)
    }
}
