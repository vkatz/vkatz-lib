package by.vkatz.katzext.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created by V on 26.04.2018.
 */
open class MultiTypeRecyclerViewAdapter<T>(open var data: List<T>,
                                           private val idProvider: (T.() -> Long)? = null,
                                           private vararg var typeHandlers: ViewTypeHandler<T>
                                          ) : RecyclerView.Adapter<SimpleViewHolder<*>>() {


    init {
        @Suppress("LeakingThis")
        setHasStableIds(idProvider != null)
    }

    override fun getItemViewType(position: Int): Int {
        typeHandlers.forEachIndexed { index, viewTypeHandler ->
            if (viewTypeHandler.typeValidator(data[position])) {
                return index
            }
        }
        throw Exception("Unregistered type for item at index: $position")
    }

    override fun getItemId(position: Int): Long =
            idProvider?.invoke(data[position]) ?: super.getItemId(position)

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): SimpleViewHolder<*> =
            typeHandlers[type].viewHolderProvider(parent.context)

    override fun getItemCount(): Int = data.size

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: SimpleViewHolder<*>, position: Int) {
        (holder as SimpleViewHolder<T>).binder?.invoke(holder, data[position])
    }
}