package by.vkatz.samples

import by.vkatz.katzilla.FragmentBackStack
import by.vkatz.katzilla.helpers.KotzillaActivity

class MainUI : KotzillaActivity() {

    override fun initContent(backStack: FragmentBackStack) {
        setContentView(R.layout.main)
        backStack.bind(supportFragmentManager, R.id.screen_layout, MainScreen::class)
    }
}
