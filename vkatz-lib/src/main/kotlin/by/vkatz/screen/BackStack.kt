package by.vkatz.screen

import java.util.*

abstract class BackStack<ScreenType : Screen<ScreenType>> {
    val backStack: ArrayList<ScreenType> = ArrayList()
    var current: ScreenType? = null
        private set

    protected abstract fun openScreen(screen: ScreenType, navigation: Screen.Navigation)

    protected abstract fun closeScreen(screen: ScreenType, navigation: Screen.Navigation)

    val lastScreen: ScreenType?
        get() {
            if (canBack()) return backStack[backStack.size - 1]
            else return null
        }

    fun getLastScreen(name: String): ScreenType? {
        return backStack.firstOrNull { strEquals(name, it.name) }
    }

    fun canBack(): Boolean {
        return backStack.size > 0
    }

    fun canBackTo(name: String): Boolean {
        return getLastScreen(name) != null
    }

    fun go(screen: ScreenType) {
        if (current != null) {
            if (!current!!.onLeave(screen)) return
            current!!.onClose(Screen.Navigation.forward)
            closeScreen(current!!, Screen.Navigation.forward)
            if (current!!.storeInBackStack)
                backStack.add(current!!)
            else {
                current!!.onRelease()
                current!!.parent = null
            }
        }
        current = screen
        current!!.parent = this
        openScreen(current!!, Screen.Navigation.forward)
        screen.onOpen(Screen.Navigation.forward)
    }

    open fun back(): Boolean {
        if (!canBack()) return false
        if (current != null) {
            current!!.onClose(Screen.Navigation.backward)
            closeScreen(current!!, Screen.Navigation.backward)
            current!!.onRelease()
            current!!.parent = null
        }
        current = lastScreen
        current!!.parent = this
        backStack.remove(current!!)
        openScreen(current!!, Screen.Navigation.backward)
        current!!.onOpen(Screen.Navigation.backward)
        return true
    }

    fun backTo(name: String): Boolean {
        if (!canBackTo(name)) return false
        if (current != null) {
            current!!.onClose(Screen.Navigation.backward)
            closeScreen(current!!, Screen.Navigation.backward)
            current!!.onRelease()
            current!!.parent = null
        }
        while (backStack.size > 0) {
            current = lastScreen
            backStack.remove(current)
            if (strEquals(name, current!!.name)) {
                current!!.parent = this
                openScreen(current!!, Screen.Navigation.backward)
                current!!.onOpen(Screen.Navigation.backward)
                return true
            } else {
                current!!.onRelease()
                current!!.parent = null
            }
        }
        return false
    }

    fun clearBackStack() {
        for (screen in backStack) {
            screen.onRelease()
            screen.parent = null
        }
        backStack.clear()
    }

    fun clearBackStackUntil(name: String) {
        var index = 0
        for (i in backStack.indices.reversed()) {
            if (strEquals(name, backStack[i].name)) return
            index = i
        }
        while (backStack.size > index) backStack.removeAt(index)
    }

    private fun strEquals(a: String?, b: String?): Boolean {
        return (a == null && b == null) || (a != null && b != null && a == b)
    }
}
