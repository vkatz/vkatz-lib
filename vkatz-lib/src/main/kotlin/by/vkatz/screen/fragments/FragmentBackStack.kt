package by.vkatz.screen.fragments

import android.app.Activity
import by.vkatz.screen.BackStack
import by.vkatz.screen.Screen

/**
 * In case u want to avoid transactions error (due to activity stopped) - please attach instance of this class to lifecycle (onPause and onResume functions)
 */
class FragmentBackStack(private val activity: Activity, private val containerId: Int) : BackStack<FragmentScreen>() {

    private var transactionAllowed = true
    private var pendingTransaction = ArrayList<FragmentScreen>()

    override fun openScreen(screen: FragmentScreen, navigation: Screen.Navigation) {
        val transaction = activity.fragmentManager.beginTransaction().replace(containerId, screen)
        screen.onTransaction(transaction, navigation)
        transaction.commit()
    }

    override fun closeScreen(screen: FragmentScreen, navigation: Screen.Navigation) {}

    override fun back(): Boolean = current != null && current!!.onBackPressed() || super.back()

    override fun go(screen: FragmentScreen) {
        if (transactionAllowed) super.go(screen)
        else pendingTransaction.add(screen)
    }

    fun onPause() {
        transactionAllowed = false
    }

    fun onResume() {
        transactionAllowed = true
        pendingTransaction.forEach { go(it) }
        pendingTransaction.clear()
    }
}
