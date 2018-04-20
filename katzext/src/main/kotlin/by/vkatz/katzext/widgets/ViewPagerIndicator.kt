package by.vkatz.katzext.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

class ViewPagerIndicator : RecyclerView {

    private var bindTarget: ViewPager? = null
    private var pageChangeListener: ViewPager.SimpleOnPageChangeListener? = null
    private var adapter: RecyclerView.Adapter? = null
    private var lastIndex = 0

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    /**
     * Do not use it
     */
    @Deprecated("")
    fun setAdapter(adapter: RecyclerView.Adapter?) {
        throw RuntimeException("do not use it")
    }

    fun unbind() {
        if (bindTarget != null && pageChangeListener != null) {
            bindTarget!!.removeOnPageChangeListener(pageChangeListener!!)
            bindTarget = null
            pageChangeListener = null
        }
    }

    fun bind(viewPager: ViewPager, @LayoutRes indicatorLayout: Int, indicatorBinder: IndicatorBinder) {
        this.bindTarget = viewPager
        setLayoutManager(LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false))

        adapter = IndicatorAdapter(indicatorLayout, indicatorBinder)
        super.setAdapter(adapter)

        pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                flush()
            }
        }
        bindTarget!!.addOnPageChangeListener(pageChangeListener!!)
    }

    fun flush() {
        adapter!!.notifyDataSetChanged()
        val index = bindTarget!!.currentItem
        stopScroll()
        if (Math.abs(index - lastIndex) > 1) {
            getLayoutManager().startSmoothScroll(SmoothLinearCenterScroller(getContext(), index, true))
        } else {
            getLayoutManager().startSmoothScroll(SmoothLinearCenterScroller(getContext(), index, false))
        }
        lastIndex = index
    }

    interface IndicatorBinder {
        fun bind(view: View, position: Int, selected: Boolean)
    }

    private class SmoothLinearCenterScroller internal constructor(context: Context, position: Int, private val instant: Boolean) : LinearSmoothScroller(context) {

        init {
            setTargetPosition(position)
            stop()
        }

        fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
            return boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
        }

        protected fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return 1f * super.calculateSpeedPerPixel(displayMetrics) / ANIMATION_SPEED
        }

        protected fun onTargetFound(targetView: View, state: RecyclerView.State, action: SmoothScroller.Action) {
            if (instant) {
                action.update(-calculateDxToMakeVisible(targetView, getHorizontalSnapPreference()), 0, 1, null)
            } else {
                super.onTargetFound(targetView, state, action)
            }
        }

        protected fun updateActionForInterimTarget(action: Action) {
            if (instant) {
                action.jumpTo(getTargetPosition())
            } else {
                super.updateActionForInterimTarget(action)
            }
        }

        companion object {

            private val ANIMATION_SPEED = 0.035f
        }
    }

    private inner class IndicatorAdapter internal constructor(@param:LayoutRes private val indicatorLayout: Int, private val indicatorBinder: IndicatorBinder) : Adapter<SimpleViewHolder>() {

        val itemCount: Int
            get() = if (bindTarget!!.adapter == null) 0 else bindTarget!!.adapter!!.count

        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
            return SimpleViewHolder(getContext(), indicatorLayout)
        }

        fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
            indicatorBinder.bind(holder.itemView, position, position == bindTarget!!.currentItem)
        }
    }

    private inner class SimpleViewHolder internal constructor(context: Context, @LayoutRes res: Int) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(res, this@ViewPagerIndicator, false))
}