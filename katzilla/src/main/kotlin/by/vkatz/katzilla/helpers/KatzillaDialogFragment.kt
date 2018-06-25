package by.vkatz.katzilla.helpers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.vkatz.katzilla.FragmentScreen

/**
 * [FragmentScreen] based dialog, will provide model
 *
 * Use [show] to provide data for dialog + override [onCreateView] to use it
 */
open class KatzillaDialogFragment<Model : FragmentScreen.ScreenModel> : DialogFragment() {
    private var internalModel: Model? = null
    val model: Model
        get() {
            if (internalModel == null) {
                throw Exception("Screen model is null - do not try to do smth after screen is removed from screen")
            }
            return internalModel!!
        }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (internalModel != null) {
            ViewModelProvider(viewModelStore, ViewModelProvider.NewInstanceFactory()).get(FragmentDialogModelHolderModel::class.java).model = internalModel
            internalModel = null
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        internalModel = ViewModelProvider(viewModelStore, ViewModelProvider.NewInstanceFactory()).get(FragmentDialogModelHolderModel::class.java).model as Model
    }

    //override view creation

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

    //override show's to provide models

    @Deprecated("", ReplaceWith("show(model, manager, tag)"))
    final override fun show(manager: FragmentManager?, tag: String?) {
        throw RuntimeException("Use show(model, manager, tag) instead")
    }

    @Deprecated("", ReplaceWith("show(model, transaction, tag)"))
    final override fun show(transaction: FragmentTransaction?, tag: String?): Int {
        throw RuntimeException("Use show(model, transaction, tag) instead")
    }

    @Deprecated("", ReplaceWith("showNow(model, manager, tag)"))
    final override fun showNow(manager: FragmentManager?, tag: String?) {
        throw RuntimeException("Use showNow(model, manager, tag) instead")
    }

    open fun show(model: Model, manager: FragmentManager?, tag: String?) {
        this.internalModel = model
        super.show(manager, tag)
    }

    open fun show(model: Model, transaction: FragmentTransaction?, tag: String?): Int {
        this.internalModel = model
        return super.show(transaction, tag)
    }

    open fun showNow(model: Model, manager: FragmentManager?, tag: String?) {
        this.internalModel = model
        super.showNow(manager, tag)
    }

    class FragmentDialogModelHolderModel(var model: FragmentScreen.ScreenModel? = null) : ViewModel() {
        override fun onCleared() {
            super.onCleared()
            model?.release()
        }
    }
}