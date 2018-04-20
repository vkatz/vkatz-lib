package by.vkatz.samples

import android.app.Activity
import android.os.Bundle
import by.vkatz.katzilla.screen.fragments.FragmentBackStack

class MainUI : Activity() {

    private var backStack: FragmentBackStack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        backStack = FragmentBackStack(this, R.id.screen_layout)
        backStack!!.go(MainScreen())
    }

    override fun onBackPressed() {
        if (!backStack!!.back()) super.onBackPressed()
    }
}
