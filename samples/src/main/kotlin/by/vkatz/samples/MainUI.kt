package by.vkatz.samples

import by.vkatz.katzilla.FragmentBackStack
import by.vkatz.katzilla.helpers.KatzillaActivity

class MainUI : KatzillaActivity() {

    override fun initContent(backStack: FragmentBackStack) {
        setContentView(R.layout.main)
        backStack.bind(supportFragmentManager, R.id.screen_layout, MainScreen::class)
    }
}
