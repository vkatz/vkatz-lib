package by.vkatz.katzext.adapters

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import by.vkatz.katzext.utils.Callback
import by.vkatz.katzext.utils.ValueCallback
import by.vkatz.katzext.utils.inflate

/**
 * Created by V on 27.04.2018.
 */
typealias ViewBinder<T> = SimpleViewHolder<T>.(data: T) -> Unit

typealias SimpleViewHolderProvider<T> = (parent: ViewGroup) -> SimpleViewHolder<T>

open class SimpleViewHolder<T>(itemView: View?, private val binder: ViewBinder<T>? = null) : RecyclerView.ViewHolder(itemView) {
    constructor(@LayoutRes layoutRid: Int, parent: ViewGroup, binder: ViewBinder<T>? = null) : this(parent.inflate(layoutRid, parent, false), binder)

    open fun bind(data: T) {
        binder?.invoke(this, data)
    }
}

open class ViewTypeHandler<T>(val typeValidator: (item: T) -> Boolean,
                              val viewHolderProvider: SimpleViewHolderProvider<out T>) {

    constructor(typeValidator: (item: T) -> Boolean, @LayoutRes layoutRid: Int, binder: ViewBinder<T>)
            : this(typeValidator, { parent -> SimpleViewHolder(layoutRid, parent, binder) })
}

open class PaginationList<T>(private val pageSize: Int, private val loader: (from: Int, count: Int, callback: ValueCallback<List<T>>) -> Unit) : ArrayList<T>() {
    var isLoading = false
        private set
    var hasMorePages = true
        private set

    private var onPageLoaded: Callback? = null

    init {
        loadPage()
    }

    fun loadPage(pageLoaded: Callback? = null) {
        if (isLoading || !hasMorePages) {
            return
        }
        isLoading = true
        loader(size, pageSize, { data ->
            addAll(data)
            hasMorePages = data.size == pageSize
            isLoading = false
            pageLoaded?.invoke()
            onPageLoaded?.invoke()
        })
    }

    fun setOnPageLoadedListener(listener: Callback?) {
        onPageLoaded = listener
    }
}