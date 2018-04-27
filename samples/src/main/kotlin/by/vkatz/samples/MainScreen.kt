package by.vkatz.samples

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.vkatz.katzext.utils.AppLiveData
import by.vkatz.katzext.utils.asyncUI
import by.vkatz.katzext.utils.inflate
import by.vkatz.katzext.utils.toast
import by.vkatz.katzilla.FragmentScreen
import by.vkatz.katzilla.helpers.KotzillaFragment
import kotlinx.android.synthetic.main.screen_main.*
import kotlinx.coroutines.experimental.delay

/**
 * Created by vKatz on 08.03.2015.
 */
class MainScreen : KotzillaFragment<MainScreen.Model>() {
    private var backTimeouted = false

    class Model : FragmentScreen.ScreenModel() {
        var counter = AppLiveData(0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, model: Model, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.screen_main)

    override fun onViewCreated(view: View, model: Model, savedInstanceState: Bundle?) {
        super.onViewCreated(view, model, savedInstanceState)

        model.counter.observe(this) { t -> counter.text = "$t" }
        counterPlus.setOnClickListener { model.counter.value += 1 }
        counterMinus.setOnClickListener { model.counter.value -= 1 }

        dataPass.setOnClickListener {
            parent?.go(DataPassScreen::class, DataPassScreen.Model(model.counter.value, { model.counter.value = it }))
        }
        spinners.setOnClickListener {
            parent?.go(SpinnerScreen::class)
        }
        adapters.setOnClickListener {
            parent?.go(AdaptersScreen::class)
        }
        compoundImages.setOnClickListener {
            parent?.go(ResViewScreen::class, ResViewScreen.Model(R.layout.screen_compound_images))
        }
        slideMenu1.setOnClickListener {
            parent?.go(ResViewScreen::class, ResViewScreen.Model(R.layout.slide_menu_1))
        }
        slideMenu2.setOnClickListener {
            parent?.go(ResViewScreen::class, ResViewScreen.Model(R.layout.slide_menu_2))
        }
        slideMenu3.setOnClickListener {
            parent?.go(ResViewScreen::class, ResViewScreen.Model(R.layout.slide_menu_3))
        }
        slideMenu4.setOnClickListener {
            parent?.go(ResViewScreen::class, ResViewScreen.Model(R.layout.slide_menu_4))
        }
        flowLayout.setOnClickListener {
            parent?.go(ResViewScreen::class, ResViewScreen.Model(R.layout.flow_layout))
        }
    }

    override fun onBackPressed(): Boolean {
        return if (!backTimeouted) {
            backTimeouted = true
            context?.toast("Press back again to exit")
            asyncUI {
                delay(3000)
                backTimeouted = false
            }
            true
        } else {
            super.onBackPressed()
        }
    }
}
