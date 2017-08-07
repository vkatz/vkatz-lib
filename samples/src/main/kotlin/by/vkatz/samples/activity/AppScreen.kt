package by.vkatz.samples.activity

import android.animation.Animator
import android.animation.AnimatorInflater
import by.vkatz.samples.R
import by.vkatz.screen.Screen

import by.vkatz.screen.fragments.FragmentScreen

/**
 * Created by Katz on 07.08.2017.
 */

abstract class AppScreen : FragmentScreen() {
    var forward = true

    override fun onOpen(navigation: Screen.Navigation) {
        super.onOpen(navigation)
        forward = navigation == Screen.Navigation.forward
    }

    override fun onClose(navigation: Screen.Navigation) {
        super.onClose(navigation)
        forward = navigation == Screen.Navigation.forward
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        val anim: Int
        if (forward) anim = if (enter) R.animator.screen_f_in else R.animator.screen_f_out //forward animation
        else anim = if (enter) R.animator.screen_b_in else R.animator.screen_b_out  //back animation
        return AnimatorInflater.loadAnimator(activity, anim)
    }
}
