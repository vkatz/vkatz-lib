package by.vkatz.samples

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.vkatz.katzext.utils.inflate
import by.vkatz.katzilla.FragmentScreen
import by.vkatz.katzilla.helpers.KotzillaFragment
import kotlinx.android.synthetic.main.data_pass.*

/**
 * Created by V on 24.04.2018.
 */

class DataPassScreen : KotzillaFragment<DataPassScreen.Model>() {

    class Model(var counter: Int, val passCallback: (value: Int) -> Unit) : FragmentScreen.ScreenModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, model: Model, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.data_pass)

    override fun onViewCreated(view: View, model: Model, savedInstanceState: Bundle?) {
        super.onViewCreated(view, model, savedInstanceState)

        counter.text = "${model.counter}"
        counterPlus.setOnClickListener {
            model.counter++
            counter.text = "${model.counter}"
        }
        counterMinus.setOnClickListener {
            model.counter--
            counter.text = "${model.counter}"
        }
        passBack.setOnClickListener {
            model.passCallback(model.counter)
            parent?.back()
        }
        goBack.setOnClickListener { parent?.back() }

    }
}
