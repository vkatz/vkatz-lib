package by.vkatz.katzext.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

@Suppress("DeprecatedCallableAddReplaceWith")
class RecyclerViewPager : RecyclerView {

    private val layoutManager: LinearLayoutManager
    private val snapHelper = PagerSnapHelper()
    private var onPageChangedListeners = HashSet<OnPageChangedListener>()

    var currentPage: Int
        get() = snapHelper.findSnapView(layoutManager)?.let { getChildAdapterPosition(it) } ?: 0
        set(value) = scrollToPosition(value)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        super.setLayoutManager(layoutManager)
        snapHelper.attachToRecyclerView(this)
        addOnScrollListener(object : OnScrollListener() {
            private var lastPage = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val page = currentPage
                if (lastPage != currentPage) onPageChangedListeners.forEach {
                    it.onPageChanged(this@RecyclerViewPager, page)
                }
                lastPage = page

            }
        })
    }

    @Deprecated("Don't use it", level = DeprecationLevel.ERROR)
    override fun setLayoutManager(layout: LayoutManager?) {
        throw RuntimeException("Don't use it")
    }

    fun addOnPageChangedListener(onPageChangedListener: OnPageChangedListener) {
        onPageChangedListeners.add(onPageChangedListener)
    }

    fun removeOnPageChangedListener(onPageChangedListener: OnPageChangedListener) {
        onPageChangedListeners.remove(onPageChangedListener)
    }

    interface OnPageChangedListener {
        fun onPageChanged(sender: RecyclerViewPager, page: Int)
    }
}