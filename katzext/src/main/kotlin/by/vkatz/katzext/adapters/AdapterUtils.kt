package by.vkatz.katzext.adapters

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import by.vkatz.katzext.utils.inflate

/**
 * Created by V on 27.04.2018.
 */
typealias ViewBinder<T> = SimpleViewHolder<T>.(data: T) -> Unit

typealias SimpleViewHolderProvider<T> = (parent: ViewGroup) -> SimpleViewHolder<T>

open class SimpleViewHolder<T>(itemView: View, private val binder: ViewBinder<T>? = null) : RecyclerView.ViewHolder(itemView) {
    constructor(@LayoutRes layoutRid: Int, parent: ViewGroup, binder: ViewBinder<T>? = null) : this(parent.inflate(layoutRid, parent, false), binder)

    open fun bind(data: T) {
        binder?.invoke(this, data)
    }
}

open class ViewTypeHandler<T>(val typeValidator: (item: T) -> Boolean, val viewHolderProvider: SimpleViewHolderProvider<out T>) {

    constructor(typeValidator: (item: T) -> Boolean, @LayoutRes layoutRid: Int, binder: ViewBinder<T>)
            : this(typeValidator, { parent -> SimpleViewHolder(layoutRid, parent, binder) })
}