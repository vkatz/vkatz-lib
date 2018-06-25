package by.vkatz.samples

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import by.vkatz.katzext.adapters.*
import by.vkatz.katzext.utils.PaginationList
import by.vkatz.katzext.utils.asTextView
import by.vkatz.katzext.utils.asyncUI
import by.vkatz.katzext.utils.inflate
import by.vkatz.katzilla.FragmentScreen
import by.vkatz.katzilla.helpers.KatzillaFragment
import kotlinx.android.synthetic.main.adapters.*
import kotlinx.coroutines.experimental.delay

/**
 * Created by V on 26.04.2018.
 */
class AdaptersScreen : KatzillaFragment<FragmentScreen.SimpleModel>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, model: SimpleModel, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.adapters)

    override fun onViewCreated(view: View, model: SimpleModel, savedInstanceState: Bundle?) {
        super.onViewCreated(view, model, savedInstanceState)
        simple1.setOnClickListener { setAdapter(0) }
        simple2.setOnClickListener { setAdapter(1) }
        mulType.setOnClickListener { setAdapter(2) }
        headerFooter.setOnClickListener { setAdapter(3) }
        pagination.setOnClickListener { setAdapter(4) }
        recycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
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
                                              ViewTypeHandler<Any>({ it is String }, ::SpinnerItemViewHolder),
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

        // pagination - just use PaginationList as data source and call list.loadPAge on footer show (or impl your own logic)
            4 -> {
                val list = PaginationList<String>(7, { from, count, callback ->
                    asyncUI(this) {
                        delay(1000)
                        val cnt = minOf(count, 100 - from)
                        callback(Array(cnt, { i -> "Item #${i + from}" }).toList())
                    }
                })
                val adapter = HeaderFooterRecyclerViewAdapter(list, null, null,
                                                              { SimpleViewHolder(ProgressBar(it.context), { list.loadPage() }) },
                                                              ViewTypeHandler({ true },
                                                                              R.layout.spinner_item,
                                                                              { itemView.asTextView().text = it })
                                                             )
                list.setOnPageLoadedListener {
                    if (adapter.data == list) {
                        if (!list.hasMorePages) {
                            adapter.footerVisible = false
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        list.setOnPageLoadedListener(null)
                    }
                }
                adapter
            }
            else -> null
        }
        recycler.adapter = adapter
    }

    class SpinnerItemViewHolder(parent: ViewGroup) : SimpleViewHolder<String>(R.layout.spinner_item, parent, null) {

        private val textView: TextView = itemView.asTextView()

        override fun bind(data: String) {
            textView.text = data
            textView.setBackgroundColor(Color.RED)
        }
    }
}
