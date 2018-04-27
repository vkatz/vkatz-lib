package by.vkatz.samples

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import by.vkatz.katzext.adapters.*
import by.vkatz.katzext.utils.asTextView
import by.vkatz.katzext.utils.asyncUI
import by.vkatz.katzext.utils.inflate
import by.vkatz.katzilla.FragmentScreen
import by.vkatz.katzilla.helpers.KotzillaFragment
import kotlinx.android.synthetic.main.adapters.*
import kotlinx.coroutines.experimental.delay

/**
 * Created by V on 26.04.2018.
 */
class AdaptersScreen : KotzillaFragment<FragmentScreen.SimpleModel>() {

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

    private fun setAdapter(index: Int) {

        //  PagedList<Int>()


        val adapter = when (index) {
            0 -> SimpleRecyclerViewAdapter(listOf(1, 2, 3),
                                           { this.toLong() },
                                           R.layout.spinner_item,
                                           { adapter, itemView, pos, data -> itemView.asTextView().text = data.toString() })

            1 -> SimpleRecyclerViewAdapter(listOf(3, 4, 5),
                                           { this.toLong() },
                                           { parent: ViewGroup ->
                                               SimpleViewHolder(R.layout.spinner_item, parent,
                                                                { adapter, itemView, pos, data -> itemView.asTextView().text = data.toString() })
                                           })

            2 -> MultiTypeRecyclerViewAdapter(listOf("a", 1, "c", 2), { hashCode().toLong() },
                                              ViewTypeHandler(
                                                      { it is String },
                                                      R.layout.spinner_item,
                                                      { adapter, itemView, pos, data ->
                                                          itemView.asTextView().text = data as String
                                                          itemView.setBackgroundColor(Color.RED)
                                                      }),
                                              ViewTypeHandler(
                                                      { it is Int },
                                                      { parent: ViewGroup ->
                                                          SimpleViewHolder(R.layout.spinner_item, parent,
                                                                           { adapter, itemView, pos, data: Any ->
                                                                               itemView.asTextView().text = data.toString()
                                                                               itemView.setBackgroundColor(Color.BLUE)
                                                                           })
                                                      })
                                             )

            3 -> HeaderFooterRecyclerViewAdapter(Array(50, { i -> i }).toList(), null,
                                                 R.layout.spinner_item,
                                                 { _, itemView, _, _ -> itemView.asTextView().text = "Header" },
                                                 R.layout.spinner_item,
                                                 { _, itemView, _, _ -> itemView.asTextView().text = "Footer" },
                                                 ViewTypeHandler({ true },
                                                                 R.layout.spinner_item,
                                                                 { _, itemView, _, data -> itemView.asTextView().text = data.toString() })
                                                ).apply { headerVisible = true; footerVisible = true }


            4 -> {
                val list = PaginationList<String>(20, { from, count, callback ->
                    asyncUI {
                        delay(1000)
                        callback(Array(count, { i -> "Item #${i + from}" }).toList())
                    }
                })
                HeaderFooterRecyclerViewAdapter(list, null, null,
                                                { parent -> SimpleViewHolder(ProgressBar(parent.context), { adapter, _, _, _ -> list.loadPage { adapter.notifyDataSetChanged() } }) },
                                                ViewTypeHandler({ true },
                                                                R.layout.spinner_item,
                                                                { _, itemView, _, data -> itemView.asTextView().text = data })
                                               ).apply { footerVisible = true }
            }
            else -> null
        }
        recycler.adapter = adapter
    }
}
