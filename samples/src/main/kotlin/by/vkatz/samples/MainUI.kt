package by.vkatz.samples

import android.os.Bundle
import by.vkatz.katzilla.FragmentBackStack
import by.vkatz.katzilla.helpers.KatzillaActivity

class MainUI : KatzillaActivity() {

    override fun initContent(backStack: FragmentBackStack, savedInstanceState: Bundle?) {
        setContentView(R.layout.main)
        backStack.bind(supportFragmentManager, R.id.screen_layout, MainScreen::class)
    }
}
