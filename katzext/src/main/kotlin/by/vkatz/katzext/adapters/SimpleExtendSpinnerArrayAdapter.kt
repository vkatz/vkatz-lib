package by.vkatz.katzext.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import by.vkatz.katzext.utils.asTextView
import by.vkatz.katzext.utils.inflate

class SimpleExtendSpinnerArrayAdapter(data: List<String?>, layoutRid: Int) :SimpleExtendSpinnerAdapter<String?>(
                data, null, null,
                { it.inflate(layoutRid, it, false) },
                { view, item -> view.asTextView().text = item }) {

    constructor(context: Context, stringArrayRid: Int, layoutRid: Int) : this(context.resources.getStringArray(stringArrayRid).toList(), layoutRid)

    override fun getSpinnerNoSelectionView(parent: ViewGroup): View? = null
}