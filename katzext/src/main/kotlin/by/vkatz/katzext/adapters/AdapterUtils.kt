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
typealias ViewBinder<T> = (adapter: SimpleViewPagerAdapter<T>, itemView: View, pos: Int, data: T) -> Unit

typealias SimpleViewBinder<T> = (itemView: View, data: T) -> Unit

typealias SimpleViewHolderProvider<T> = (parent: ViewGroup) -> SimpleViewHolder<T>

open class SimpleViewHolder<T>(itemView: View?, val binder: ViewBinder<T>? = null) : RecyclerView.ViewHolder(itemView) {
    constructor(@LayoutRes layoutRid: Int, parent: ViewGroup, binder: ViewBinder<T>? = null) : this(parent.inflate(layoutRid, parent, false), binder)
}

open class ViewTypeHandler<T>(val typeValidator: (item: T) -> Boolean,
                              val viewHolderProvider: SimpleViewHolderProvider<T>) {

    constructor(typeValidator: (item: T) -> Boolean, @LayoutRes layoutRid: Int, binder: ViewBinder<T>)
            : this(typeValidator, { parent -> SimpleViewHolder(layoutRid, parent, binder) })
}

open class PaginationList<T>(private val pageSize: Int, private val loader: (from: Int, count: Int, callback: ValueCallback<List<T>>) -> Unit) : ArrayList<T>() {
    var isLoading = false
        private set
    var loadedPagesCount = 0
        private set
    var hasMorePages = true
        private set

    init {
        loadPage()
    }

    fun loadPage(pageLoaded: Callback? = null) {
        if (isLoading) {
            return
        }
        loader(size, pageSize, { data ->
            addAll(data)
            loadedPagesCount++
            hasMorePages = data.size == pageSize
            isLoading = false
            pageLoaded?.invoke()
        })
    }
}