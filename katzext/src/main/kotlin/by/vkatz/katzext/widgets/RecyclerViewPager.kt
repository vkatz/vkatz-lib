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

    val currentPage: Int get() = snapHelper.findSnapView(layoutManager)?.let { getChildAdapterPosition(it) } ?: 0

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

    override fun scrollToPosition(position: Int) {
        //------------DO NOT REMOVE----------------------------
        //we need to reset adapter and clan existing ViewHolders
        //this line will call RecyclerView to cleanup view hierarchy
        //and setup same adapter after this on appropriate position
        //without this we may get stuck on mid-animation during
        //setVisibility + setPosition combo because of layout called twice
        adapter = adapter
        //------------DO NOT REMOVE----------------------------
        super.scrollToPosition(position)
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