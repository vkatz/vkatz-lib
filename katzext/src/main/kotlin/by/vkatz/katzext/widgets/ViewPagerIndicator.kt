package by.vkatz.katzext.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import by.vkatz.katzext.R
import by.vkatz.katzext.adapters.HeaderFooterRecyclerViewAdapter
import by.vkatz.katzext.adapters.SimpleViewHolder
import by.vkatz.katzext.adapters.ViewTypeHandler

class ViewPagerIndicator : RecyclerView {

    private var bindTarget: ViewPager? = null
    private var pageChangeListener: ViewPager.SimpleOnPageChangeListener? = null
    private var indicatorsAdapter: IndicatorAdapter? = null
    private var indicatorDataProvider: IndicatorDataProvider? = null
    private var lastIndex = -1
    private var forceCenter = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator, defStyle, 0)
        forceCenter = a.getBoolean(R.styleable.ViewPagerIndicator_extendForceCenterMode, false)
        a.recycle()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean = false

    @Deprecated("do not use it", ReplaceWith("bind"), DeprecationLevel.ERROR)
    override fun setAdapter(adapter: Adapter<*>?) {
        throw RuntimeException("Use bind/unbind instead")
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val mw = measuredWidth
        val mh = measuredHeight
        super.onMeasure(widthSpec, heightSpec)
        if (mw != measuredWidth || mh != measuredHeight) {
            lastIndex = -1
            flush()
        }
    }

    fun unbind() {
        if (bindTarget != null && pageChangeListener != null) {
            bindTarget!!.removeOnPageChangeListener(pageChangeListener!!)
            bindTarget = null
            pageChangeListener = null
        }
    }

    fun getOffset() = (if (forceCenter) 1 else 0)

    fun bind(viewPager: ViewPager, @LayoutRes indicatorLayout: Int, indicatorBinder: IndicatorBinder) {
        this.bindTarget = viewPager
        indicatorDataProvider = object : IndicatorDataProvider {
            override fun getCount(): Int = bindTarget?.adapter?.count ?: 0
            override fun getCurrent(): Int = bindTarget?.currentItem ?: 0
        }
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        indicatorsAdapter = IndicatorAdapter(indicatorLayout, indicatorBinder)
        indicatorsAdapter!!.headerVisible = forceCenter
        indicatorsAdapter!!.footerVisible = forceCenter
        super.setAdapter(indicatorsAdapter)
        pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                flush()
            }
        }
        bindTarget!!.addOnPageChangeListener(pageChangeListener!!)
    }

    fun setDataProvider(dataProvider: IndicatorDataProvider) {
        indicatorDataProvider = dataProvider
    }

    fun flush() {
        indicatorsAdapter ?: return
        indicatorsAdapter!!.data = arrayOfNulls<Unit?>(indicatorDataProvider?.getCount() ?: 0).toList()
        indicatorsAdapter!!.notifyDataSetChanged()
        val index = (indicatorDataProvider?.getCurrent() ?: 0) + getOffset()
        stopScroll()
        layoutManager?.startSmoothScroll(SmoothLinearCenterScroller(context, index, Math.abs(index - lastIndex) > 1))
        lastIndex = index
    }

    interface IndicatorBinder {
        fun bind(view: View, position: Int, selected: Boolean)
    }

    interface IndicatorDataProvider {
        fun getCount(): Int
        fun getCurrent(): Int
    }

    private class SmoothLinearCenterScroller(context: Context, position: Int, private val instant: Boolean) : LinearSmoothScroller(context) {

        init {
            targetPosition = position
            stop()
        }

        override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int =
                boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)

        override fun onTargetFound(targetView: View, state: RecyclerView.State, action: SmoothScroller.Action) {
            action.update(-calculateDxToMakeVisible(targetView, horizontalSnapPreference), 0, if (instant) 1 else 350, null)
        }

        override fun updateActionForInterimTarget(action: Action) {
            if (instant) {
                action.jumpTo(targetPosition)
            } else {
                super.updateActionForInterimTarget(action)
            }
        }
    }

    private inner class IndicatorAdapter(@LayoutRes private val indicatorLayout: Int, private val indicatorBinder: IndicatorBinder)
        : HeaderFooterRecyclerViewAdapter<Unit?>(
            ArrayList(), null,
            { SimpleViewHolder(View(it.context).apply { layoutParams = RecyclerView.LayoutParams(-1, 0) }, null) },
            { SimpleViewHolder(View(it.context).apply { layoutParams = RecyclerView.LayoutParams(-1, 0) }, null) },
            ViewTypeHandler({ true }, indicatorLayout, {
                indicatorBinder.bind(itemView, layoutPosition - getOffset(), layoutPosition - getOffset() == indicatorDataProvider?.getCurrent() ?: 0)
            }))
}