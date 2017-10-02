package by.vkatz.screen

interface Screen<SELF : Screen<SELF>> {
    var parent: BackStack<SELF>?
    var storeInBackStack: Boolean
    var name: String?

    fun onOpen(navigation: Navigation) {}

    fun onClose(navigation: Navigation) {}

    fun onRelease() {}

    fun onLeave(screen: SELF): Boolean = true

    enum class Navigation {forward, backward }
}
