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
    private var indicatorsAdapter: RecyclerView.Adapter<SimpleViewHolder>? = null
    private var lastIndex = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean = false

    @Deprecated("do not use it", ReplaceWith("bind"), DeprecationLevel.ERROR)
    override fun setAdapter(adapter: Adapter<*>?) {
        throw RuntimeException("Use bind/unbind instead")
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
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        indicatorsAdapter = IndicatorAdapter(indicatorLayout, indicatorBinder)
        super.setAdapter(indicatorsAdapter)
        pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                flush()
            }
        }
        bindTarget!!.addOnPageChangeListener(pageChangeListener!!)
    }

    fun flush() {
        indicatorsAdapter!!.notifyDataSetChanged()
        val index = bindTarget!!.currentItem
        stopScroll()
        layoutManager?.startSmoothScroll(SmoothLinearCenterScroller(context, index, Math.abs(index - lastIndex) > 1))
        lastIndex = index
    }

    interface IndicatorBinder {
        fun bind(view: View, position: Int, selected: Boolean)
    }

    private class SmoothLinearCenterScroller(context: Context, position: Int, private val instant: Boolean) : LinearSmoothScroller(context) {
        companion object {
            private const val ANIMATION_SPEED = 0.035f
        }

        init {
            targetPosition = position
            stop()
        }

        override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int =
                boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float =
                1f * super.calculateSpeedPerPixel(displayMetrics) / ANIMATION_SPEED

        override fun onTargetFound(targetView: View, state: RecyclerView.State, action: SmoothScroller.Action) {
            if (instant) {
                action.update(-calculateDxToMakeVisible(targetView, horizontalSnapPreference), 0, 1, null)
            } else {
                super.onTargetFound(targetView, state, action)
            }
        }

        override fun updateActionForInterimTarget(action: Action) {
            if (instant) {
                action.jumpTo(targetPosition)
            } else {
                super.updateActionForInterimTarget(action)
            }
        }
    }

    private inner class IndicatorAdapter(@param:LayoutRes private val indicatorLayout: Int, private val indicatorBinder: IndicatorBinder) : Adapter<SimpleViewHolder>() {

        override fun getItemCount(): Int = if (bindTarget!!.adapter == null) 0 else bindTarget!!.adapter!!.count

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder =
                SimpleViewHolder(context, indicatorLayout)

        override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
            indicatorBinder.bind(holder.itemView, position, position == bindTarget!!.currentItem)
        }
    }

    private inner class SimpleViewHolder(context: Context, @LayoutRes res: Int)
        : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(res, this@ViewPagerIndicator, false))
}