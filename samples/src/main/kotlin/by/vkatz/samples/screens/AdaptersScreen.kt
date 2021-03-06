package by.vkatz.samples.screens

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.vkatz.katzext.adapters.*
import by.vkatz.katzext.utils.asTextView
import by.vkatz.katzext.utils.inflate
import by.vkatz.samples.R
import kotlinx.android.synthetic.main.adapters.*

/**
 * Created by V on 26.04.2018.
 */
class AdaptersScreen : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.adapters)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        simple1.setOnClickListener { setAdapter(0) }
        simple2.setOnClickListener { setAdapter(1) }
        mulType.setOnClickListener { setAdapter(2) }
        headerFooter.setOnClickListener { setAdapter(3) }
        recycler.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
    }

    @SuppressLint("SetTextI18n")
    @Suppress("MoveLambdaOutsideParentheses")
    private fun setAdapter(index: Int) {
        val adapter = when (index) {
            // usual recycler without direct creation of VH
            0 -> SimpleRecyclerViewAdapter(listOf(1, 2, 3),
                                           { this.toLong() },
                                           R.layout.spinner_item,
                                           { itemView.asTextView().text = it.toString() })
            // usual recycler with VH
            1 -> SimpleRecyclerViewAdapter(listOf(3, 4, 5),
                                           { this.toLong() },
                                           { parent ->
                                               SimpleViewHolder(R.layout.spinner_item, parent, { itemView.asTextView().text = it.toString() })
                                           })
            // adapter for multiple types - just register handlers and it's done
            2 -> MultiTypeRecyclerViewAdapter(listOf("a", 1, "c", 2), { hashCode().toLong() },
                                              ViewTypeHandler<Any>({ it is String }, AdaptersScreen::SpinnerItemViewHolder),
                                              ViewTypeHandler(
                                                      { it is Int },
                                                      {
                                                          SimpleViewHolder<Any>(R.layout.spinner_item, it,
                                                                                {
                                                                                    itemView.asTextView().text = it.toString()
                                                                                    itemView.setBackgroundColor(Color.BLUE)
                                                                                })
                                                      })
                                             )
            // multi type adapter where u can add 1 header and 1 footer (u can hide it via visibility properties) (if u need more - use default MultiTypeRecyclerViewAdapter)
            3 -> HeaderFooterRecyclerViewAdapter(Array(50, { i -> i }).toList(), null,
                                                 R.layout.spinner_item,
                                                 { itemView.asTextView().text = "Header" },
                                                 R.layout.spinner_item,
                                                 { itemView.asTextView().text = "Footer" },
                                                 ViewTypeHandler({ true },
                                                                 R.layout.spinner_item,
                                                                 { itemView.asTextView().text = it.toString() })
                                                ).apply { headerVisible = true; footerVisible = true }
            else -> null

        }
        recycler.adapter = adapter
    }

    class SpinnerItemViewHolder(parent: ViewGroup) : SimpleViewHolder<String>(R.layout.spinner_item, parent, null) {

        private val textView: TextView = itemView.asTextView()

        override fun bind(item: String) {
            textView.text = item
            textView.setBackgroundColor(Color.RED)
        }
    }
}
