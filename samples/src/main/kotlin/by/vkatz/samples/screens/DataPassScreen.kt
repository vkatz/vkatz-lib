package by.vkatz.samples.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import by.vkatz.katzext.utils.AppLiveData
import by.vkatz.katzext.utils.inflate
import by.vkatz.katzext.utils.lazyViewModel
import by.vkatz.katzext.utils.navController
import by.vkatz.samples.R
import kotlinx.android.synthetic.main.data_pass.*

/**
 * Created by V on 24.04.2018.
 */
class DataPassViewModel(val counter: AppLiveData<Int>) : ViewModel()

class DataPassScreen : Fragment() {
    private val model by lazyViewModel(DataPassViewModel::class)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.data_pass)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var innerCounter = model.counter.value
        counter.text = "$innerCounter"
        counterPlus.setOnClickListener {
            innerCounter++
            counter.text = "$innerCounter"
        }
        counterMinus.setOnClickListener {
            innerCounter--
            counter.text = "$innerCounter"
        }
        passBack.setOnClickListener {
            model.counter.value = innerCounter
            navController.navigateUp()
        }
        goBack.setOnClickListener { navController.navigateUp() }

    }
}
