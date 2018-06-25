package by.vkatz.katzilla.helpers

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.Gravity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.transition.Slide
import by.vkatz.katzilla.FragmentBackStack
import by.vkatz.katzilla.FragmentScreen

/**
 * Created by V on 24.04.2018.
 *
 * [FragmentScreen] realization with useful improvements
 *
 * + permission helper via [isPermissionNeedExplanation], [requestPermissions], [onRequestPermissionsResult]
 *
 * + predefined transitions (see [setNavigationState])
 */
open class KatzillaFragment<Model : FragmentScreen.ScreenModel> : FragmentScreen<Model>() {
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

    @SuppressLint("RtlHardcoded")
    override fun setNavigationState(state: Int) {
        super.setNavigationState(state)
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
    }

    open fun onBackPressed() = false
}