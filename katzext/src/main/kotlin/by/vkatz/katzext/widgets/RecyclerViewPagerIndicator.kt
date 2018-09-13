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
import by.vkatz.katzext.R
import by.vkatz.katzext.adapters.HeaderFooterRecyclerViewAdapter
import by.vkatz.katzext.adapters.SimpleViewHolder
import by.vkatz.katzext.adapters.ViewTypeHandler
import by.vkatz.katzext.utils.clamp
import com.axs.android.utils.adapters.CircularRecyclerViewAdapter

typealias ViewPagerIndicatorBinder = (view: View, position: Int, selected: Boolean) -> Unit

class RecyclerViewPagerIndicator : RecyclerView {

    private var bindTarget: RecyclerViewPager? = null
    private var onPageChangedListener: RecyclerViewPager.OnPageChangedListener? = null
    private var indicatorsAdapter: IndicatorAdapter? = null
    private var indicatorDataProvider: IndicatorDataProvider? = null
    private var lastIndex = -1
    private var forceCenter = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewPagerIndicator, defStyle, 0)
        forceCenter = a.getBoolean(R.styleable.RecyclerViewPagerIndicator_extendForceCenterMode, false)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        a.recycle()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean = false

    @Deprecated("do not use it", ReplaceWith("bind"), DeprecationLevel.ERROR)
    override fun setAdapter(adapter: Adapter<*>?) {
        if (isInEditMode) super.setAdapter(adapter)
        else throw RuntimeException("Use bind/unbind instead")
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val mw = measuredWidth
        val mh = measuredHeight
        super.onMeasure(widthSpec, heightSpec)
        if (mw != measuredWidth || mh != measuredHeight) flush(true)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed) flush(true)
    }

    fun unbind() {
        if (bindTarget != null && onPageChangedListener != null) {
            bindTarget!!.removeOnPageChangedListener(onPageChangedListener!!)
            bindTarget = null
            onPageChangedListener = null
        }
    }

    fun getOffset() = if (forceCenter) 1 else 0

    fun bind(target: RecyclerViewPager, @LayoutRes indicatorLayout: Int, indicatorBinder: ViewPagerIndicatorBinder) {
        this.bindTarget = target
        val targetAdapter = target.adapter
        indicatorDataProvider = if (targetAdapter is CircularRecyclerViewAdapter<*>) {
            object : IndicatorDataProvider {
                override fun getCount(): Int = targetAdapter.data.size
                override fun getCurrent(): Int = target.currentPage % targetAdapter.data.size
            }
        } else {
            object : IndicatorDataProvider {
                override fun getCount(): Int = bindTarget?.adapter?.itemCount ?: 0
                override fun getCurrent(): Int = target.currentPage
            }
        }
        indicatorsAdapter = IndicatorAdapter(indicatorLayout, indicatorBinder)
        indicatorsAdapter!!.headerVisible = forceCenter
        indicatorsAdapter!!.footerVisible = forceCenter
        super.setAdapter(indicatorsAdapter)
        onPageChangedListener = object : RecyclerViewPager.OnPageChangedListener {
            override fun onPageChanged(sender: RecyclerViewPager, page: Int) = flush()
        }
        target.addOnPageChangedListener(onPageChangedListener!!)
    }

    fun setDataProvider(dataProvider: IndicatorDataProvider) {
        indicatorDataProvider = dataProvider
    }

    fun flush(reset: Boolean = false) {
        indicatorsAdapter ?: return
        val count = indicatorDataProvider?.getCount() ?: 0
        val index = (indicatorDataProvider?.getCurrent() ?: 0).clamp(0, count) + getOffset()
        if (reset || index != lastIndex) {
            indicatorsAdapter!!.data = arrayOfNulls<Unit?>(count).toList()
            indicatorsAdapter!!.notifyDataSetChanged()
            stopScroll()
            layoutManager?.startSmoothScroll(SmoothLinearCenterScroller(context, index, reset || Math.abs(index - lastIndex) > 1))
            lastIndex = index
        }
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
            if (instant) action.jumpTo(targetPosition)
            else super.updateActionForInterimTarget(action)
        }
    }

    private inner class IndicatorAdapter(@LayoutRes private val indicatorLayout: Int, private val indicatorBinder: ViewPagerIndicatorBinder)
        : HeaderFooterRecyclerViewAdapter<Unit?>(
            ArrayList(), null,
            { SimpleViewHolder(View(it.context).apply { layoutParams = LayoutParams(-1, 0) }, null) },
            { SimpleViewHolder(View(it.context).apply { layoutParams = RecyclerView.LayoutParams(-1, 0) }, null) },
            ViewTypeHandler({ true }, indicatorLayout, {
                indicatorBinder(itemView, layoutPosition - getOffset(), layoutPosition - getOffset() == indicatorDataProvider?.getCurrent() ?: 0)
            }))
}