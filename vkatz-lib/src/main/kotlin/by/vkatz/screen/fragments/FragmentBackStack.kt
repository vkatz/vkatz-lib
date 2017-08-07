package by.vkatz.screen.fragments

import android.app.Activity
import by.vkatz.screen.BackStack
import by.vkatz.screen.Screen

class FragmentBackStack(private val activity: Activity, private val containerId: Int) : BackStack<FragmentScreen>() {

    override fun openScreen(screen: FragmentScreen, navigation: Screen.Navigation) {
        val transaction = activity.fragmentManager.beginTransaction().replace(containerId, screen)
        screen.transactionConfig?.invoke(transaction)
        screen.transactionConfig = null
        transaction.commit()
    }

    override fun closeScreen(screen: FragmentScreen, navigation: Screen.Navigation) {}

    override fun back(): Boolean {
        return current != null && current!!.onBackPressed() || super.back()
    }
}
