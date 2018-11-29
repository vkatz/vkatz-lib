package by.vkatz.katzext.adapters

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import by.vkatz.katzext.utils.ext.inflate
import kotlinx.android.extensions.LayoutContainer

typealias ViewBinder<T> = SimpleViewHolder<T>.(data: T) -> Unit

typealias SimpleViewHolderProvider<T> = (parent: ViewGroup) -> SimpleViewHolder<T>

/**
 * Simple ViewHolder implementation to support
 *
 * 1) kotlin android extension
 *
 * 2) item data store, simple binding, simple creation
 *
 * 3) lifecycle
 */
open class SimpleViewHolder<T>(itemView: View, private val binder: ViewBinder<T>? = null) : RecyclerView.ViewHolder(itemView), LifecycleOwner, LayoutContainer {
    @Suppress("LeakingThis")
    private val lifecycleRegistry = LifecycleRegistry(this)
    override val containerView: View = itemView
    var itemData: T? = null
        private set

    constructor(@LayoutRes layoutRid: Int, parent: ViewGroup, binder: ViewBinder<T>? = null) : this(parent.inflate(layoutRid, parent, false), binder)

    init {
        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    open fun bindHolder(data: T) {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
        itemData = data
        bind(data)
    }

    open fun bind(item: T) {
        binder?.invoke(this, item)
    }
}

/**
 * Helper class to determinate type. Used within [MultiTypeRecyclerViewAdapter],[HeaderFooterRecyclerViewAdapter]
 */
open class ViewTypeHandler<T>(val typeValidator: (item: T) -> Boolean, val viewHolderProvider: SimpleViewHolderProvider<out T>, val holdersPoolSize: Int? = null) {
    constructor(typeValidator: (item: T) -> Boolean, @LayoutRes layoutRid: Int, binder: ViewBinder<T>)
            : this(typeValidator, { parent -> SimpleViewHolder(layoutRid, parent, binder) })
}