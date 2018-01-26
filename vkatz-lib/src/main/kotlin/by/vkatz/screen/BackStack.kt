package by.vkatz.screen

import java.util.*

@Suppress("MemberVisibilityCanPrivate")
abstract class BackStack<ScreenType : Screen<ScreenType>> {
    val backStack: ArrayList<ScreenType> = ArrayList()
    var current: ScreenType? = null
        private set

    private fun ScreenType.release() {
        onRelease()
        parent = null
    }

    protected abstract fun openScreen(screen: ScreenType, navigation: Screen.Navigation)

    protected abstract fun closeScreen(screen: ScreenType, navigation: Screen.Navigation)

    val lastScreen: ScreenType?
        get() = if (canBack()) backStack[backStack.size - 1] else null

    fun getLastScreen(name: String): ScreenType? = backStack.firstOrNull { name == it.name }

    fun canBack(): Boolean = backStack.size > 0

    fun canBackTo(name: String): Boolean = getLastScreen(name) != null

    open fun go(screen: ScreenType) {
        if (current != null) {
            if (!current!!.onLeave(screen)) return
            current!!.onClose(Screen.Navigation.FORWARD)
            closeScreen(current!!, Screen.Navigation.FORWARD)
            if (current!!.storeInBackStack) backStack.add(current!!)
            else current!!.release()
        }
        current = screen
        current!!.parent = this
        openScreen(current!!, Screen.Navigation.FORWARD)
        screen.onOpen(Screen.Navigation.FORWARD)
    }

    open fun back(): Boolean {
        if (!canBack()) return false
        if (current != null) {
            current!!.onClose(Screen.Navigation.BACKWARD)
            closeScreen(current!!, Screen.Navigation.BACKWARD)
            current!!.release()
        }
        current = lastScreen
        current!!.parent = this
        backStack.remove(current!!)
        openScreen(current!!, Screen.Navigation.BACKWARD)
        current!!.onOpen(Screen.Navigation.BACKWARD)
        return true
    }

    fun backTo(name: String): Boolean {
        if (!canBackTo(name)) return false
        if (current != null) {
            current!!.onClose(Screen.Navigation.BACKWARD)
            closeScreen(current!!, Screen.Navigation.BACKWARD)
            current!!.release()
        }
        while (backStack.size > 0) {
            current = lastScreen
            backStack.remove(current)
            if (name == current!!.name) {
                current!!.parent = this
                openScreen(current!!, Screen.Navigation.BACKWARD)
                current!!.onOpen(Screen.Navigation.BACKWARD)
                return true
            } else current!!.release()
        }
        return false
    }

    fun clearBackStack() {
        for (screen in backStack) screen.release()
        backStack.clear()
    }

    fun clearBackStackUntil(name: String) {
        backStack.takeLastWhile { it.name != name }.forEach { remove(it) }
    }

    fun remove(screen: ScreenType) {
        if (backStack.contains(screen)) {
            screen.release()
            backStack.remove(screen)
        }
    }
}
