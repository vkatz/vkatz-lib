package by.vkatz.adapters

import android.content.Context
import android.view.ViewGroup
import by.vkatz.utils.asTextView
import by.vkatz.utils.inflate

class SimpleExtendSpinnerArrayAdapter(data: List<String?>, layoutRid: Int)
    : SimpleExtendSpinnerAdapter<String?>(
        data, null, null,
        { it.inflate(layoutRid, it, false) },
        { view, item -> view.asTextView().text = item }) {

    constructor(context: Context, stringArrayRid: Int, layoutRid: Int) : this(context.resources.getStringArray(stringArrayRid).toList(), layoutRid)

    override fun getSpinnerNoSelectionView(parent: ViewGroup) = null
}