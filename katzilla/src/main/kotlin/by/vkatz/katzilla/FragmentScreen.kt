package by.vkatz.katzilla

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

/**
 * Created by V on 23.04.2018.
 */
open class FragmentScreen<Model : FragmentScreen.ScreenModel> : Fragment() {

    internal var internalModel: Model? = null

    internal var internalParent: FragmentBackStack? = null

    val parent: FragmentBackStack?
        get() = internalParent

    val model: Model
        get() {
            if (internalModel == null) {
                throw Exception("Screen model is null - do not try to do smth after screen is removed from backstack")
            }
            return internalModel!!
        }

    var screenOptions = ScreenOptions()

    open fun setNavigationState(state: Int) {}

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            onCreateView(inflater, container, model, savedInstanceState)

    open fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, model: Model, savedInstanceState: Bundle?): View? =
            super.onCreateView(inflater, container, savedInstanceState)

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onViewCreated(view, model, savedInstanceState)
    }

    open fun onViewCreated(view: View, model: Model, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    abstract class ScreenModel : ViewModel() {
        internal fun release() {
            onCleared()
        }
    }

    class SimpleModel : ScreenModel()
    data class ScreenOptions(var storeInBackStack: Boolean = true)
}