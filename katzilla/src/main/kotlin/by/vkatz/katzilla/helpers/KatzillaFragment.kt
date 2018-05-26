package by.vkatz.katzilla.helpers

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.os.Build
import android.transition.Slide
import android.view.Gravity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import by.vkatz.katzilla.FragmentBackStack
import by.vkatz.katzilla.FragmentScreen

/**
 * Created by V on 24.04.2018.
 */
open class KatzillaFragment<Model : FragmentScreen.ScreenModel> : FragmentScreen<Model>() {
    private var isForwardNavigationState = true

    fun isPermissionNeedExplanation(permission: String): Boolean =
            if (ContextCompat.checkSelfPermission(context!!, permission) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)
            else false

    fun requestPermissions(code: Int, vararg permissions: String) {
        if (activity == null) {
            return
        }
        val requiredPermissions = permissions.filter { ContextCompat.checkSelfPermission(activity!!, it) != PackageManager.PERMISSION_GRANTED }.toTypedArray()
        if (requiredPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity!!, requiredPermissions, code)
        } else {
            onRequestPermissionsResult(code, true)
        }
    }

    open fun onRequestPermissionsResult(code: Int, granted: Boolean) {}

    final override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })
    }

    override fun setNavigationState(state: Int) {
        super.setNavigationState(state)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            when (state) {
                FragmentBackStack.NAVIGATION_FORWARD_ENTER -> setEnterTransition(Slide(Gravity.RIGHT))
                FragmentBackStack.NAVIGATION_FORWARD_EXIT -> setExitTransition(Slide(Gravity.LEFT))
                FragmentBackStack.NAVIGATION_BACK_ENTER -> setEnterTransition(Slide(Gravity.LEFT))
                FragmentBackStack.NAVIGATION_BACK_EXIT -> setExitTransition(Slide(Gravity.RIGHT))
                else -> {
                    enterTransition = null
                    exitTransition = null
                }
            }
        } else {
            isForwardNavigationState = when (state) {
                FragmentBackStack.NAVIGATION_FORWARD_ENTER -> true
                FragmentBackStack.NAVIGATION_FORWARD_EXIT -> true
                FragmentBackStack.NAVIGATION_BACK_ENTER -> false
                FragmentBackStack.NAVIGATION_BACK_EXIT -> false
                else -> true
            }
        }
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return super.onCreateAnimator(transit, enter, nextAnim)
        } else {
            val scale = 2f
            val from: Float
            val to: Float
            if (isForwardNavigationState) {
                from = if (enter) 100f else 0f
                to = if (enter) 0f else -30f
            } else {
                from = if (enter) -100f else 0f
                to = if (enter) 0f else 30f
            }
            val anim1 = ObjectAnimator.ofFloat(view, "translationX", scale * from, scale * to)
            val anim2 = ObjectAnimator.ofFloat(view, "alpha", if (enter) 0f else 1f, if (enter) 1f else 0f)
            val anim = AnimatorSet()
            anim.playTogether(anim1, anim2)
            anim.duration = 250
            return anim
        }
    }

    open fun onBackPressed() = false
}