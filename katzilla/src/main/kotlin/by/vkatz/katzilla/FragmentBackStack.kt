package by.vkatz.katzilla

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import kotlin.reflect.KClass

/**
 * Created by V on 22.04.2018.
 */
@Suppress("MemberVisibilityCanBePrivate")
open class FragmentBackStack {

    companion object {
        const val NAVIGATION_FORWARD_ENTER = -1
        const val NAVIGATION_FORWARD_EXIT = -2
        const val NAVIGATION_BACK_ENTER = -3
        const val NAVIGATION_BACK_EXIT = -4
        const val NAVIGATION_RESTORE = -5
    }

    val backStackEntries: ArrayList<BackStackEntry> = ArrayList()

    private var customEnterState: Int? = null
    private var customExitState: Int? = null

    //screen related data
    private var currentScreenClass: KClass<*>? = null
    private var currentModel: FragmentScreen.ScreenModel? = null

    //context related data, should be cleared on activity destroyed
    var currentScreen: FragmentScreen<*>? = null
        private set
    private var fragmentManager: FragmentManager? = null
    private var containerId = 0

    inline fun <reified ScreenModel : FragmentScreen.ScreenModel, ScreenClazz : FragmentScreen<ScreenModel>>
            bind(fragmentManager: FragmentManager, @IdRes containerId: Int, defaultScreen: KClass<ScreenClazz>) {
        bind(fragmentManager, containerId, defaultScreen, ScreenModel::class.java.newInstance())
    }

    @Suppress("UNCHECKED_CAST")
    fun <ScreenModel : FragmentScreen.ScreenModel, ScreenClazz : FragmentScreen<ScreenModel>>
            bind(fragmentManager: FragmentManager, @IdRes containerId: Int, defaultScreen: KClass<ScreenClazz>, defaultScreenModel: ScreenModel) {
        this.fragmentManager = fragmentManager
        this.containerId = containerId

        var restored = false
        fragmentManager.fragments.forEach {
            if (it::class == currentScreenClass)
                (it as? FragmentScreen<FragmentScreen.ScreenModel>)?.let {
                    it.internalModel = currentModel
                    it.internalParent = this
                    screenChanged(it)
                    restored = true
                }
        }
        if (!restored) {
            overridePendingTransitions(NAVIGATION_RESTORE, null)
            go(defaultScreen, defaultScreenModel)
        }
    }

    fun onActivityDestroyed() {
        currentScreen = null
        fragmentManager = null
        containerId = 0
    }

    fun overridePendingTransitions(enter: Int?, exit: Int?) {
        customEnterState = enter
        customExitState = exit
    }

    private fun checkNavigationPossibility() {
        if (fragmentManager == null || containerId == 0) {
            throw RuntimeException("FragmentBackStack must be binded before using navigation")
        }
    }

    private fun screenChanged(screen: FragmentScreen<*>) {
        currentScreen = screen
        currentModel = screen.internalModel
        currentScreenClass = screen::class
        overridePendingTransitions(null, null)
        System.gc()
    }

    inline fun <reified ScreenModel : FragmentScreen.ScreenModel, ScreenClazz : FragmentScreen<ScreenModel>>
            go(screen: KClass<ScreenClazz>, noinline transactionConfig: (FragmentTransaction.() -> Unit)? = null) {
        go(screen, ScreenModel::class.java.newInstance(), transactionConfig)
    }

    fun <ScreenModel : FragmentScreen.ScreenModel, ScreenClazz : FragmentScreen<ScreenModel>>
            go(screen: KClass<ScreenClazz>, model: ScreenModel, transactionConfig: (FragmentTransaction.() -> Unit)? = null) {
        checkNavigationPossibility()

        currentScreen?.apply {
            if (screenOptions.storeInBackStack) {
                backStackEntries.add(BackStackEntry(this::class, internalModel, fragmentManager!!.saveFragmentInstanceState(this)))
            }
            internalModel = null
            internalParent = null
            setNavigationState(customExitState ?: NAVIGATION_FORWARD_EXIT)
        }

        val pendingScreen = screen.java.newInstance()
        pendingScreen.internalModel = model
        pendingScreen.internalParent = this
        pendingScreen.setNavigationState(customEnterState ?: NAVIGATION_FORWARD_ENTER)

        fragmentManager!!.beginTransaction().apply { transactionConfig?.invoke(this) }.replace(containerId, pendingScreen).commit()

        screenChanged(pendingScreen)
    }

    @Suppress("UNCHECKED_CAST")
    fun back(transactionConfig: (FragmentTransaction.() -> Unit)? = null) {
        checkNavigationPossibility()

        currentScreen?.apply {
            internalModel = null
            internalParent = null
            setNavigationState(customExitState ?: NAVIGATION_BACK_EXIT)
        }

        val backStackEntry = backStackEntries.last()
        backStackEntries.remove(backStackEntry)
        val pendingScreen = backStackEntry.screen.java.newInstance() as FragmentScreen<FragmentScreen.ScreenModel>
        pendingScreen.internalModel = backStackEntry.data
        pendingScreen.internalParent = this
        pendingScreen.setNavigationState(customEnterState ?: NAVIGATION_BACK_ENTER)
        pendingScreen.setInitialSavedState(backStackEntry.state)

        fragmentManager!!.beginTransaction().apply { transactionConfig?.invoke(this) }.replace(containerId, pendingScreen).commit()

        screenChanged(pendingScreen)
    }

    fun hasScreen(screen: KClass<out FragmentScreen<*>>): Boolean {
        return backStackEntries.any { it.screen == screen }
    }

    fun backTo(screen: KClass<out FragmentScreen<*>>, transactionConfig: (FragmentTransaction.() -> Unit)? = null) {
        val index = backStackEntries.indexOfLast { it.screen == screen }
        if (index >= 0) {
            while (backStackEntries.size != index + 1) {
                backStackEntries.removeAt(index + 1)
            }
            back(transactionConfig)
        }
    }

    fun clearBackStack() {
        backStackEntries.clear()
    }

    fun isBackPossible() = backStackEntries.size > 0

    data class BackStackEntry(var screen: KClass<out FragmentScreen<*>>, var data: FragmentScreen.ScreenModel?, var state: Fragment.SavedState?)
}