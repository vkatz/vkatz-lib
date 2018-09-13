package by.vkatz.samples.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import by.vkatz.katzext.utils.*
import by.vkatz.samples.R
import by.vkatz.samples.Services
import kotlinx.android.synthetic.main.screen_main.*

/**
 * Created by vKatz on 08.03.2015.
 */

class MainScreenViewModel : ViewModel() {
    private val random = Services.random

    var counter = AppLiveData(random.getRandom())
}

class MainScreen : Fragment() {
    private val model by lazyViewModel(MainScreenViewModel::class)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.screen_main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.counter.observe(this) { t -> counter.text = "$t" }
        counterPlus.setOnClickListener { model.counter.value += 1 }
        counterMinus.setOnClickListener { model.counter.value -= 1 }

        dataPass.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_dataPassScreen, DataPassViewModel(model.counter))
        }
        spinners.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_spinnerScreen)
        }
        adapters.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_adaptersScreen)
        }
        slideMenu1.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_resViewScreen, ResScreenViewModel(R.layout.slide_menu_1))
        }
        slideMenu2.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_resViewScreen, ResScreenViewModel(R.layout.slide_menu_2))
        }
        slideMenu3.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_resViewScreen, ResScreenViewModel(R.layout.slide_menu_3))
        }
        slideMenu4.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_resViewScreen, ResScreenViewModel(R.layout.slide_menu_4))
        }
        flowLayout.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_resViewScreen, ResScreenViewModel(R.layout.flow_layout))
        }
        pagerIndicator.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_pagerIndicator)
        }
        utils.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_utilsScreen)
        }
    }
}