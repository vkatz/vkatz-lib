package by.vkatz.katzilla

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by V on 23.04.2018.
 */
open class FragmentScreen<Model : FragmentScreen.ScreenModel> : Fragment() {

    internal var internalModel: Model? = null

    internal var internalParent: FragmentBackStack? = null

    var parent: FragmentBackStack? = null
        get() = internalParent
        private set

    var screenOptions = ScreenOptions()

    open fun setNavigationState(state: Int) {}

    private fun validateModel() {
        if (internalModel == null) {
            throw Exception("Model should not be null here, smth goes wrong")
        }
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        validateModel()
        return onCreateView(inflater, container, internalModel!!, savedInstanceState)
    }

    open fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, model: Model, savedInstanceState: Bundle?): View? =
            super.onCreateView(inflater, container, savedInstanceState)

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        validateModel()
        onViewCreated(view, internalModel!!, savedInstanceState)
    }

    open fun onViewCreated(view: View, model: Model, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    abstract class ScreenModel
    class SimpleModel : ScreenModel()
    data class ScreenOptions(var storeInBackStack: Boolean = true)
}