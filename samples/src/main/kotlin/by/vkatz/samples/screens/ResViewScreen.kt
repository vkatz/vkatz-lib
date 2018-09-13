package by.vkatz.samples.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import by.vkatz.katzext.utils.inflate
import by.vkatz.katzext.utils.lazyViewModel

class ResScreenViewModel(@LayoutRes val resId: Int) : ViewModel()

class ResViewScreen : Fragment() {

    private val model by lazyViewModel(ResScreenViewModel::class)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(model.resId)

}
