package by.vkatz.katzext.adapters

import android.view.View
import android.view.ViewGroup
import by.vkatz.katzext.widgets.ExtendSpinner

open class SimpleExtendSpinnerAdapter<T>(data: List<T>,
                                         idProvider: (T.() -> Long)?,
                                         private val noSelectionItem: T,
                                         private val viewCreator: (parent: ViewGroup) -> View,
                                         private val viewBinder: (view: View, item: T) -> Unit
                                        ) : ExtendSpinner.ExtendSpinnerAdapter<T>(data, idProvider) {

    override fun getSpinnerNoSelectionView(parent: ViewGroup): View? {
        val view = viewCreator(parent)
        viewBinder(view, noSelectionItem)
        return view
    }

    override fun getSpinnerView(pos: Int, parent: ViewGroup): View {
        val view = viewCreator(parent)
        viewBinder(view, getItem(pos))
        return view
    }

    override fun getDropDownView(parent: ViewGroup): View = viewCreator(parent)

    override fun bindDropDownView(pos: Int, item: T, view: View) {
        viewBinder(view, item)
    }
}