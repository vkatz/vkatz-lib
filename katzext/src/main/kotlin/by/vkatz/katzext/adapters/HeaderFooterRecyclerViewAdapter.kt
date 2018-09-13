package by.vkatz.katzext.adapters

import android.view.ViewGroup
import androidx.annotation.LayoutRes

open class HeaderFooterRecyclerViewAdapter<T>(data: List<T>,
                                              idProvider: (T.() -> Long)?,
                                              private val headerProvider: SimpleViewHolderProvider<Unit>? = null,
                                              private val footerProvider: SimpleViewHolderProvider<Unit>? = null,
                                              vararg typeHandlers: ViewTypeHandler<T>
                                             ) : MultiTypeRecyclerViewAdapter<T>(data, idProvider, *typeHandlers) {
    companion object {
        const val TYPE_HEADER = -1
        const val TYPE_FOOTER = -2
        const val ID_HEADER = -1L
        const val ID_FOOTER = -2L

    }

    var headerVisible: Boolean = true
    var footerVisible: Boolean = true

    constructor(data: List<T>,
                idProvider: (T.() -> Long)?,
                headerProvider: SimpleViewHolderProvider<Unit>? = null,
                footerProvider: SimpleViewHolderProvider<Unit>? = null,
                itemProvider: SimpleViewHolderProvider<T>
               ) : this(data, idProvider, headerProvider, footerProvider, ViewTypeHandler({ true }, itemProvider))

    constructor(data: List<T>,
                idProvider: (T.() -> Long)?,
                @LayoutRes headerRId: Int,
                headerBinder: ViewBinder<Unit>?,
                @LayoutRes footerRId: Int,
                footerBinder: ViewBinder<Unit>?,
                vararg typeHandlers: ViewTypeHandler<T>
               ) : this(data, idProvider,
                        headerBinder?.let { binder -> { parent: ViewGroup -> SimpleViewHolder<Unit>(headerRId, parent, binder) } },
                        footerBinder?.let { binder -> { parent: ViewGroup -> SimpleViewHolder<Unit>(footerRId, parent, binder) } },
                        *typeHandlers)


    private fun Boolean.toInt() = if (this) 1 else 0

    private fun isHeaderVisible() =
            headerVisible && headerProvider != null

    private fun isHeaderIndex(position: Int) =
            headerVisible && headerProvider != null && position == 0

    private fun isFooterIndex(position: Int) =
            footerVisible && footerProvider != null && position == itemCount - 1

    override fun getItemCount(): Int =
            super.getItemCount() + (headerProvider != null && headerVisible).toInt() + (footerProvider != null && footerVisible).toInt()

    override fun getItemId(position: Int): Long {
        return when {
            isHeaderIndex(position) -> ID_HEADER
            isFooterIndex(position) -> ID_FOOTER
            else -> super.getItemId(position - isHeaderVisible().toInt())
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isHeaderIndex(position) -> TYPE_HEADER
            isFooterIndex(position) -> TYPE_FOOTER
            else -> super.getItemViewType(position - isHeaderVisible().toInt())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): SimpleViewHolder<*> {
        return when (type) {
            TYPE_HEADER -> headerProvider!!(parent)
            TYPE_FOOTER -> footerProvider!!(parent)
            else -> super.onCreateViewHolder(parent, type)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: SimpleViewHolder<*>, position: Int) {
        when {
            isHeaderIndex(position) || isFooterIndex(position) -> (holder as SimpleViewHolder<Unit>).bindHolder(Unit)
            else -> super.onBindViewHolder(holder, position - isHeaderVisible().toInt())
        }
    }
}