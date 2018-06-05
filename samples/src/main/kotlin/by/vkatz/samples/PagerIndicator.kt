package by.vkatz.samples

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import by.vkatz.katzext.adapters.SimpleViewPagerAdapter
import by.vkatz.katzext.utils.asTextView
import by.vkatz.katzext.utils.inflate
import by.vkatz.katzext.widgets.ViewPagerIndicator
import by.vkatz.katzilla.FragmentScreen
import by.vkatz.katzilla.helpers.KatzillaFragment
import kotlinx.android.synthetic.main.pager_indicator.*

/**
 * Created by V on 24.04.2018.
 */

class PagerIndicator : KatzillaFragment<FragmentScreen.SimpleModel>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, model: FragmentScreen.SimpleModel, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.pager_indicator)

    override fun onViewCreated(view: View, model: FragmentScreen.SimpleModel, savedInstanceState: Bundle?) {
        super.onViewCreated(view, model, savedInstanceState)
        pager.adapter = SimpleViewPagerAdapter(Array(10, { i -> "Page $i" }).toList(), { this },
                { parent, data, _ ->
                    TextView(parent.context).apply {
                        text = data
                        gravity = Gravity.CENTER
                        setBackgroundColor(0xffdfdfdf.toInt())
                    }
                })
        pagerIndicator1.bind(pager, R.layout.pager_indicator_item, object : ViewPagerIndicator.IndicatorBinder {
            override fun bind(view: View, position: Int, selected: Boolean) {
                view.asTextView().text = if (selected) "+" else "-"
            }
        })
        pagerIndicator2.bind(pager, R.layout.pager_indicator_item, object : ViewPagerIndicator.IndicatorBinder {
            override fun bind(view: View, position: Int, selected: Boolean) {
                view.asTextView().text = if (selected) "+" else "-"
            }
        })
        pagerIndicator3.bind(pager, R.layout.pager_indicator_item, object : ViewPagerIndicator.IndicatorBinder {
            override fun bind(view: View, position: Int, selected: Boolean) {
                view.asTextView().apply {
                    text = pager.adapter?.getPageTitle(position)
                    alpha = if (selected) 1f else .5f
                }
            }
        })
        pagerIndicator4.bind(pager, R.layout.pager_indicator_item, object : ViewPagerIndicator.IndicatorBinder {
            override fun bind(view: View, position: Int, selected: Boolean) {
                view.asTextView().apply {
                    text = pager.adapter?.getPageTitle(position)
                    alpha = if (selected) 1f else .5f
                }
            }
        })
        pagerIndicator5.bind(pager, R.layout.pager_indicator_item, object : ViewPagerIndicator.IndicatorBinder {
            override fun bind(view: View, position: Int, selected: Boolean) {
                view.asTextView().apply {
                    text = "Preeeeeeeeety long tab name $position"
                    alpha = if (selected) 1f else .5f
                }
            }
        })
        pagerIndicator1.flush()
        pagerIndicator2.flush()
        pagerIndicator3.flush()
        pagerIndicator4.flush()
        pagerIndicator5.flush()

    }
}
