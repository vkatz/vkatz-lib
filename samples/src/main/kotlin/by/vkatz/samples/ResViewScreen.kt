package by.vkatz.samples

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import by.vkatz.katzext.utils.inflate
import by.vkatz.katzilla.FragmentScreen
import by.vkatz.katzilla.helpers.KatzillaFragment

/**
 * Created by V on 24.04.2018.
 */

class ResViewScreen : KatzillaFragment<ResViewScreen.Model>() {

    class Model(@LayoutRes val layoutId: Int) : FragmentScreen.ScreenModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, model: Model, savedInstanceState: Bundle?): View? =
            inflater.inflate(model.layoutId)
}
