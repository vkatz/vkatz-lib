package by.vkatz.katzext.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.LayoutRes
import by.vkatz.katzext.utils.ext.inflate

open class SimpleSpinnerAdapter<T>(open var data: List<T>,
                                   private val idProvider: (T.() -> Long)?,
                                   private val noSelectionViewProvider: (((parent: ViewGroup) -> View))?,
                                   private val viewProvider: ((parent: ViewGroup) -> View)?,
                                   private val viewBinder: ((view: View, item: T) -> Unit)?,
                                   private val dropDownViewProvider: (parent: ViewGroup) -> View,
                                   private val dropDownViewBinder: (view: View, item: T) -> Unit
                                  ) : BaseAdapter() {

    var displayNoSelectionItem = noSelectionViewProvider != null

    constructor(data: List<T>,
                idProvider: (T.() -> Long)?,
                @LayoutRes noSelectionViewResId: Int?,
                @LayoutRes viewResId: Int?,
                viewBinder: ((view: View, item: T) -> Unit)?,
                @LayoutRes droDownViewResId: Int,
                dropDownBinder: (view: View, item: T) -> Unit
               ) : this(data, idProvider,
                        noSelectionViewResId?.let { { parent: ViewGroup -> parent.inflate(it) } },
                        viewResId?.let { { parent: ViewGroup -> parent.inflate(it) } }, viewBinder,
                        { parent: ViewGroup -> parent.inflate(droDownViewResId) }, dropDownBinder)

    override fun getItem(position: Int): T = data[position]
    override fun getItemId(position: Int): Long = if (noSelectionViewProvider == null && position == 0) -1 else idProvider?.invoke(getItem(position)) ?: -1
    override fun getCount(): Int = data.size + if (displayNoSelectionItem) 1 else 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (displayNoSelectionItem && position == 0) return noSelectionViewProvider!!(parent)
        val v = (viewProvider ?: dropDownViewProvider).invoke(parent)
        (viewBinder ?: dropDownViewBinder).invoke(v, getItem(position - if (displayNoSelectionItem) 1 else 0)!!)
        return v
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (displayNoSelectionItem && position == 0) return View(parent.context)
        val v = dropDownViewProvider(parent)
        dropDownViewBinder(v, getItem(position - if (displayNoSelectionItem) 1 else 0)!!)
        return v
    }
}