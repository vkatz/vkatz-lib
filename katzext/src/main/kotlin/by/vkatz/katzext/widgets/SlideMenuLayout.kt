package by.vkatz.katzext.widgets

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Scroller
import by.vkatz.katzext.R

@Suppress("MemberVisibilityCanPrivate")
open class SlideMenuLayout : ExtendRelativeLayout {
    companion object {
        val LEFT = 1
        val RIGHT = 2
        val TOP = 3
        val BOTTOM = 4

        val FLAG_NEVER_FINISH = 0
        val FLAG_ALWAYS_FINISH = 1
        val FLAG_CUSTOM = 2
    }

    val slideFrom: Int
    var isExpanded: Boolean = false
        private set
    var isMenuEnabled: Boolean = false
    var scrollerDuration: Int = 0
    private var scroll: Boolean = false
    private var autoScroll: Boolean = false
    private var pos: Float = 0.toFloat()
    private var dPos: Int = 0
    private val flags: Int
    private var slideSize: Int = 0
    private var slideHidingSize: Int = 0
    private var slideVisibleSize: Int = 0
    private val startScrollDistance: Float
    private val scroller: Scroller
    private var onExpandStateChangeListener: ((view: SlideMenuLayout, expanded: Boolean) -> Unit)? = null
    private var onSlideChangeListener: ((view: SlideMenuLayout, value: Float) -> Unit)? = null
    private var customScrollBehavior: ((sender: SlideMenuLayout, velocity: Float) -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        scroller = Scroller(context)
        scroll = false
        autoScroll = false
        //init params
        val a = context.obtainStyledAttributes(attrs, R.styleable.SlideMenuLayout, 0, 0)
        slideFrom = a.getInt(R.styleable.SlideMenuLayout_slideFrom, LEFT)
        isExpanded = a.getBoolean(R.styleable.SlideMenuLayout_menuExpanded, false)
        isMenuEnabled = a.getBoolean(R.styleable.SlideMenuLayout_menuEnabled, true)
        slideHidingSize = a.getDimensionPixelSize(R.styleable.SlideMenuLayout_menuHidingSize, 0)
        slideVisibleSize = a.getDimensionPixelSize(R.styleable.SlideMenuLayout_menuVisibleSize, 0)
        startScrollDistance = a.getDimensionPixelSize(R.styleable.SlideMenuLayout_startScrollDistance, ViewConfiguration.get(context).scaledTouchSlop).toFloat()
        scrollerDuration = a.getInt(R.styleable.SlideMenuLayout_scrollerDuration, 250)
        flags = a.getInt(R.styleable.SlideMenuLayout_scrollBehavior, FLAG_ALWAYS_FINISH)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var dx = 0f
        var dy = 0f
        if (isHorizontal()) dx = clamp(scroller.currX.toFloat(), 0f, slideSize.toFloat())
        else dy = clamp(scroller.currY.toFloat(), 0f, slideSize.toFloat())
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue
            val lp = child.layoutParams as LayoutParams
            if (lp.isMovable) {
                child.translationX = dx
                child.translationY = dy
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val oldSlideSize = slideSize
        slideSize = 0
        val w = measuredWidth
        val h = measuredHeight
        when (slideFrom) {
            BOTTOM -> slideSize = if (slideHidingSize != 0) slideHidingSize else h - slideVisibleSize
            TOP -> slideSize = if (slideHidingSize != 0) -slideHidingSize else slideVisibleSize - h
            RIGHT -> slideSize = if (slideHidingSize != 0) slideHidingSize else w - slideVisibleSize
            LEFT -> slideSize = if (slideHidingSize != 0) -slideHidingSize else slideVisibleSize - w
        }
        if (oldSlideSize != slideSize) {
            if (isExpanded) expand(false)
            else collapse(false)
        }
    }

    override fun invalidate() {
        super.invalidate()
        val scroll = if (isHorizontal()) scroller.currX else scroller.currY
        val anim = scroller.computeScrollOffset()
        val updatedScroll = if (isHorizontal()) scroller.currX else scroller.currY
        if (onSlideChangeListener != null && scroll != updatedScroll) {
            val value = 1 - Math.abs(1f * updatedScroll / (getMaxSlide() - getMinSlide()))
            onSlideChangeListener!!.invoke(this@SlideMenuLayout, value)
        }
        if (anim) postInvalidate()
        else {
            val isExpanded: Boolean = if (isHorizontal()) scroller.currX == 0 else scroller.currY == 0
            if (isExpanded != this@SlideMenuLayout.isExpanded && onExpandStateChangeListener != null)
                onExpandStateChangeListener!!.invoke(this@SlideMenuLayout, isExpanded)
            this@SlideMenuLayout.isExpanded = isExpanded
            autoScroll = false
        }
        requestLayout()
    }

    fun isHorizontal() = slideFrom == RIGHT || slideFrom == LEFT

    fun isVertical() = !isHorizontal()

    private fun isEventInsideChild(x: Float, y: Float): Boolean {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams as LayoutParams
            if (!lp.isTouchable || child.visibility == View.GONE) continue
            val cx = (x - child.translationX).toInt()
            val cy = (y - child.translationY).toInt()
            if (cx >= child.left && cy >= child.top && cx <= child.right && cy <= child.bottom) return true
        }
        return false
    }

    private fun dispatchTouch(ev: MotionEvent, isIntercept: Boolean): Boolean {
        invalidate()
        if (autoScroll) return false
        val curPos = if (isHorizontal()) ev.x else ev.y
        if (!scroll && !isEventInsideChild(ev.x, ev.y)) return false
        if (ev.action == MotionEvent.ACTION_DOWN) {
            scroll = false
            pos = curPos
            return !isIntercept
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            return if (scroll) {
                dPos = (curPos - pos).toInt()
                scrollMenuBy(dPos)
                pos = curPos
                true
            } else {
                if (Math.abs(pos - curPos) > startScrollDistance) {
                    parent?.requestDisallowInterceptTouchEvent(true)
                    pos = curPos
                    scroll = true
                    true
                } else false
            }
        } else if (ev.action == MotionEvent.ACTION_CANCEL || ev.action == MotionEvent.ACTION_UP)
            if (scroll) finishMenuScroll()
        return false
    }

    fun scrollMenuBy(amount: Int): Int {
        if (amount == 0) return 0
        var scroll = amount
        if (isHorizontal()) {
            val fPos = (scroller.currX + amount).toFloat()
            if (Math.abs(fPos) > Math.abs(slideSize)) scroll = slideSize - scroller.currX
            if (fPos * (if (slideFrom == LEFT) -1 else 1) < 0) scroll = -scroller.currX
            scroller.startScroll(scroller.currX, 0, scroll, 0, 0)
        } else {
            val fPos = (scroller.currY + amount).toFloat()
            if (Math.abs(fPos) > Math.abs(slideSize)) scroll = slideSize - scroller.currY
            if (fPos * (if (slideFrom == TOP) -1 else 1) < 0) scroll = -scroller.currY
            scroller.startScroll(0, scroller.currY, 0, scroll, 0)
        }
        dPos = amount
        invalidate()
        return scroll
    }

    fun scrollMenuTo(value: Int, anim: Boolean) {
        scroller.startScroll(scroller.currX, scroller.currY,
                (if (isHorizontal()) value else 0) - scroller.currX,
                (if (isHorizontal()) 0 else value) - scroller.currY,
                if (anim) scrollerDuration else 0)
        autoScroll = true
        invalidate()
    }

    fun flingMenuBy(velocity: Int) {
        if (isHorizontal()) scroller.fling(scroller.currX, scroller.currY, velocity, 0, Math.min(0, slideSize), Math.max(0, slideSize), 0, 0)
        else scroller.fling(scroller.currX, scroller.currY, 0, velocity, 0, 0, Math.min(0, slideSize), Math.max(0, slideSize))
        invalidate()
    }

    fun finishMenuScroll() {
        if (hasFlag(flags, FLAG_ALWAYS_FINISH)) { //finish scroll
            if (getCurrentSlide() == getMinSlide() || getCurrentSlide() == getMaxSlide()) return
            if (isHorizontal()) {
                if (dPos > 0 && slideFrom == LEFT || dPos < 0 && slideFrom == RIGHT) expand()
                else collapse()
            } else {
                if (dPos > 0 && slideFrom == TOP || dPos < 0 && slideFrom == BOTTOM) expand()
                else collapse()
            }
        } else if (hasFlag(flags, FLAG_CUSTOM)) {
            if (customScrollBehavior == null) throw RuntimeException("SlideMenuLayout: U need to set customScrollBehavior to use flag 'custom'")
            customScrollBehavior!!.invoke(this, (dPos * 50).toFloat())
        } else { //do velocity scroll
            val velocity = dPos * 50
            flingMenuBy(velocity)
        }
    }

    fun hasFlag(what: Int, flag: Int): Boolean = what and flag == flag

    fun expand(anim: Boolean = true) {
        scroller.startScroll(scroller.currX, scroller.currY, -scroller.currX, -scroller.currY, if (anim) scrollerDuration else 0)
        autoScroll = true
        invalidate()
    }

    fun collapse(anim: Boolean = true) {
        scroller.startScroll(scroller.currX, scroller.currY,
                (if (isHorizontal()) slideSize else 0) - scroller.currX,
                (if (isHorizontal()) 0 else slideSize) - scroller.currY,
                if (anim) scrollerDuration else 0)
        autoScroll = true
        invalidate()
    }

    fun toggle(anim: Boolean = true) {
        if (isExpanded) collapse(anim)
        else expand(anim)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean = isMenuEnabled && dispatchTouch(ev, true)

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (isMenuEnabled) dispatchTouch(ev, false)
        return true
    }

    fun getMinSlide() = Math.min(0, slideSize)

    fun getMaxSlide() = Math.max(0, slideSize)

    fun getCurrentSlide() = if (isHorizontal()) scroller.currX else scroller.currY

    private fun clearSlideSizes() {
        slideHidingSize = 0
        slideVisibleSize = 0
    }

    fun setSlideHidingSize(slideHidingSize: Int) {
        clearSlideSizes()
        this.slideHidingSize = slideHidingSize
        requestLayout()
    }

    fun setSlideVisibleSize(slideVisibleSize: Int) {
        clearSlideSizes()
        this.slideVisibleSize = slideVisibleSize
        requestLayout()
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (i in 0 until childCount) getChildAt(i).dispatchApplyWindowInsets(WindowInsets(insets))
            insets
        } else super.onApplyWindowInsets(insets)
    }

    private fun clamp(value: Float, a: Float, b: Float): Float {
        val max = Math.max(a, b)
        val min = Math.min(a, b)
        if (value < min) return min
        if (value > max) return max
        return value
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams = LayoutParams(context, attrs)

    override fun generateDefaultLayoutParams(): LayoutParams = LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true, false)

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean = p is LayoutParams

    fun setOnExpandStateChangeListener(onExpandStateChangeListener: ((view: SlideMenuLayout, expanded: Boolean) -> Unit)?) {
        this.onExpandStateChangeListener = onExpandStateChangeListener
    }

    fun setOnSlideChangeListener(onSlideChangeListener: ((view: SlideMenuLayout, value: Float) -> Unit)?) {
        this.onSlideChangeListener = onSlideChangeListener
    }

    fun setScrollBehavior(scrollBehavior: ((sender: SlideMenuLayout, velocity: Float) -> Unit)?) {
        this.customScrollBehavior = scrollBehavior
    }

    class LayoutParams : RelativeLayout.LayoutParams {
        var isMovable: Boolean = false
        var isTouchable: Boolean = false

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.SlideMenuLayout_Layout, 0, 0)
            isMovable = a.getBoolean(R.styleable.SlideMenuLayout_Layout_applyScroll, true)
            isTouchable = a.getBoolean(R.styleable.SlideMenuLayout_Layout_interceptTouches, true)
            a.recycle()
        }

        constructor(w: Int, h: Int, isMovable: Boolean, isTouchable: Boolean) : super(w, h) {
            this.isMovable = isMovable
            this.isTouchable = isTouchable
        }
    }
}