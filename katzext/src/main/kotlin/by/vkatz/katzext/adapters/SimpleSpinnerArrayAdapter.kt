package by.vkatz.katzext.adapters

import androidx.annotation.LayoutRes
import by.vkatz.katzext.utils.ext.asTextView

class SimpleSpinnerArrayAdapter(data: List<String?>, @LayoutRes layoutRid: Int)
    : SimpleSpinnerAdapter<String?>(data, null, null, null, null,
                                    layoutRid, { view, item -> view.asTextView().text = item })