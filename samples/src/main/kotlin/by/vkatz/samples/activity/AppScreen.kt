package by.vkatz.samples.activity

import by.vkatz.samples.R
import by.vkatz.screen.fragments.FragmentScreen

/**
 * Created by Katz on 07.08.2017.
 */

abstract class AppScreen : FragmentScreen() {
    override fun getTransactionAnimator(isForward: Boolean, isEntering: Boolean): Int? {
        return if (isForward) {
            if (isEntering) R.animator.screen_f_in else R.animator.screen_f_out
        } else {
            if (isEntering) R.animator.screen_b_in else R.animator.screen_b_out
        }
    }
}
