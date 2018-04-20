package by.vkatz.samples

import android.app.ActivityOptions
import android.view.LayoutInflater
import android.view.View
import by.vkatz.samples.activity.ActivityA
import by.vkatz.samples.activity.AppScreen
import by.vkatz.samples.activity.AppViewScreen
import by.vkatz.katzext.utils.ActivityNavigator
import by.vkatz.katzext.utils.get

/**
 * Created by vKatz on 08.03.2015.
 */
class MainScreen : AppScreen() {

    init {
        holdView = true
    }

    override fun createView(): View {
        val view = LayoutInflater.from(activity).inflate(R.layout.screen_main, null, false)

        view[R.id.activities].setOnClickListener {
            val bundle = ActivityOptions.makeCustomAnimation(activity, R.anim.idle, R.anim.idle).toBundle()
            ActivityNavigator.forActivity(activity).withData("a", "String from MainUI").go(ActivityA::class.java, bundle)
        }

        fun setupButton(button: Int, screen: Int) {
            view[button].setOnClickListener { parent?.go(AppViewScreen.create(screen)) }
        }

        setupButton(R.id.asset_font, R.layout.screen_font)
        setupButton(R.id.compound_images, R.layout.screen_compound_images)
        setupButton(R.id.flow_layout, R.layout.flow_layout)
        setupButton(R.id.slide_menu_1, R.layout.slide_menu_1)
        setupButton(R.id.slide_menu_2, R.layout.slide_menu_2)
        setupButton(R.id.slide_menu_3, R.layout.slide_menu_3)
        setupButton(R.id.slide_menu_4, R.layout.slide_menu_4)

        return view
    }
}
