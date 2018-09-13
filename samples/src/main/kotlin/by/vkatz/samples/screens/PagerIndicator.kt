package by.vkatz.samples.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import by.vkatz.katzext.adapters.SimpleRecyclerViewAdapter
import by.vkatz.katzext.adapters.SimpleViewHolder
import by.vkatz.katzext.utils.asTextView
import by.vkatz.katzext.utils.inflate
import by.vkatz.samples.R
import kotlinx.android.synthetic.main.pager_indicator.*

/**
 * Created by V on 24.04.2018.
 */

class PagerIndicator : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.pager_indicator)

    @SuppressLint("SetTextI18n")
    @Suppress("MoveLambdaOutsideParentheses", "NAME_SHADOWING", "UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager.adapter = SimpleRecyclerViewAdapter(Array(10, { i -> "Page $i" }).toList(), { this.hashCode().toLong() },
                                                  { parent: ViewGroup ->
                                                      SimpleViewHolder(
                                                              TextView(parent.context).apply {
                                                                  gravity = Gravity.CENTER
                                                                  layoutParams = ViewGroup.LayoutParams(-1, -1)
                                                                  setPadding(30, 30, 30, 30)
                                                                  setBackgroundColor(0xffdfdfdf.toInt())
                                                              },
                                                              { data -> itemView.asTextView().text = data })
                                                  })
        pagerIndicator1.bind(pager, R.layout.pager_indicator_item, { view, _, selected ->
            view.asTextView().text = if (selected) "+" else "-"
        })
        pagerIndicator2.bind(pager, R.layout.pager_indicator_item, { view, _, selected ->
            view.asTextView().text = if (selected) "+" else "-"
        })
        pagerIndicator3.bind(pager, R.layout.pager_indicator_item, { view, position, selected ->
            view.asTextView().apply {
                text = (pager.adapter as? SimpleRecyclerViewAdapter<String>)?.data?.get(position) ?: ""
                alpha = if (selected) 1f else .5f
            }
        })
        pagerIndicator4.bind(pager, R.layout.pager_indicator_item, { view, position, selected ->
            view.asTextView().apply {
                text = (pager.adapter as? SimpleRecyclerViewAdapter<String>)?.data?.get(position) ?: ""
                alpha = if (selected) 1f else .5f
            }
        })
        pagerIndicator5.bind(pager, R.layout.pager_indicator_item, { view, position, selected ->
            view.asTextView().apply {
                text = "Preeeeeeeeety long tab name $position"
                alpha = if (selected) 1f else .5f
            }
        })
        pagerIndicator1.flush()
        pagerIndicator2.flush()
        pagerIndicator3.flush()
        pagerIndicator4.flush()
        pagerIndicator5.flush()

    }
}
