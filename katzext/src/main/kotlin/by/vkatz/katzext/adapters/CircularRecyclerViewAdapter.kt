package com.axs.android.utils.adapters

import by.vkatz.katzext.adapters.SimpleRecyclerViewAdapter
import by.vkatz.katzext.adapters.SimpleViewHolderProvider
import by.vkatz.katzext.adapters.ViewBinder

open class CircularRecyclerViewAdapter<T> : SimpleRecyclerViewAdapter<T> {
    constructor(data: List<T>, idProvider: (T.() -> Long)?, viewHolderProvider: SimpleViewHolderProvider<T>) : super(data, idProvider, viewHolderProvider)
    constructor(data: List<T>, idProvider: (T.() -> Long)?, layoutRID: Int, viewBinder: ViewBinder<T>) : super(data, idProvider, layoutRID, viewBinder)

    override fun getItemCount(): Int {
        return if (data.isEmpty()) 0 else Int.MAX_VALUE
    }

    override fun getItemAt(pos: Int): T {
        return super.getItemAt(pos % data.size)
    }
}