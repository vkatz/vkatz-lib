package by.vkatz.samples.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import by.vkatz.screen.Screen
import by.vkatz.screen.fragments.CompatFragmentScreen

/**
 * Created by Katz on 07.08.2017.
 */
abstract class AppScreen : CompatFragmentScreen() {

    override fun onOpen(navigation: Screen.Navigation) {
        super.onOpen(navigation)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            enterTransition = Slide(if (navigation == Screen.Navigation.FORWARD) Gravity.END else Gravity.START)
    }

    override fun onClose(navigation: Screen.Navigation) {
        super.onClose(navigation)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            exitTransition = Slide(if (navigation == Screen.Navigation.FORWARD) Gravity.START else Gravity.END)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            view?.requestApplyInsets()
    }

    override fun getTransactionAnimator(isForward: Boolean, isEntering: Boolean): Animator? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) return null
        else {
            val scale = 2f
            val from: Float
            val to: Float
            if (isForward) {
                from = if (isEntering) 100f else 0f
                to = if (isEntering) 0f else -30f
            } else {
                from = if (isEntering) -100f else 0f
                to = if (isEntering) 0f else 30f
            }
            val anim1 = ObjectAnimator.ofFloat(view, "translationX", scale * from, scale * to)
            val anim2 = ObjectAnimator.ofFloat(view, "alpha", if (isEntering) 0f else 1f, if (isEntering) 1f else 0f)
            val anim = AnimatorSet()
            anim.playTogether(anim1, anim2)
            anim.duration = 250
            return anim
        }
    }
}