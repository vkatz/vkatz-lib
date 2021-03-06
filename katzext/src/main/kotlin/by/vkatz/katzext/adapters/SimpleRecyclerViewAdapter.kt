package by.vkatz.katzext.adapters

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

open class SimpleRecyclerViewAdapter<T>(open var data: List<T>,
                                        private val idProvider: (T.() -> Long)? = null,
                                        private val viewHolderProvider: SimpleViewHolderProvider<T>
                                       ) : RecyclerView.Adapter<SimpleViewHolder<T>>() {

    constructor(data: List<T>,
                idProvider: (T.() -> Long)? = null,
                @LayoutRes layoutRID: Int,
                viewBinder: ViewBinder<T>
               ) : this(data, idProvider, { parent -> SimpleViewHolder(layoutRID, parent, viewBinder) })

    init {
        @Suppress("LeakingThis")
        setHasStableIds(idProvider != null)
    }

    open fun getItemAt(pos: Int): T = data[pos]

    override fun getItemId(position: Int): Long = idProvider?.invoke(getItemAt(position)) ?: super.getItemId(position)

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): SimpleViewHolder<T> = viewHolderProvider(parent)

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SimpleViewHolder<T>, position: Int) {
        holder.bindHolder(getItemAt(position))
    }
}