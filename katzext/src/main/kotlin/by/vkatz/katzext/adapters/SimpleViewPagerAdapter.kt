package by.vkatz.katzext.adapters

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

open class SimpleViewPagerAdapter<T>(var data: List<T>,
                                     private val titleProvider: (T.() -> String?)? = null,
                                     private val viewProvider: (parent: ViewGroup, data: T, position: Int) -> View
                                    ) : PagerAdapter() {

    override fun getCount(): Int = data.size

    override fun getPageTitle(position: Int): CharSequence? =
            titleProvider?.invoke(data[position])

    override fun instantiateItem(container: ViewGroup, position: Int) =
            viewProvider(container, data[position], position).also { container.addView(it) }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        if (obj is View) container.removeView(obj)
    }

    override fun isViewFromObject(view: View, obj: Any) = view == obj
}