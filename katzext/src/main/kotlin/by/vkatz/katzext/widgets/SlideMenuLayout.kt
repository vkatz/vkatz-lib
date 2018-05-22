package by.vkatz.katzext.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.v4.view.NestedScrollingParent
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import by.vkatz.katzext.R
import by.vkatz.katzext.utils.clamp
import by.vkatz.katzext.utils.closeTo
import by.vkatz.katzext.utils.forEachChildren

@Suppress("MemberVisibilityCanPrivate", "MemberVisibilityCanBePrivate", "unused")
open class SlideMenuLayout : RelativeLayout, NestedScrollingParent {
    companion object {
        const val SLIDE_FROM_LEFT = 1
        const val SLIDE_FROM_RIGHT = 2
        const val SLIDE_FROM_TOP = 3
        const val SLIDE_FROM_BOTTOM = 4

        const val BEHAVIOUR_NONE = 0
        const val BEHAVIOUR_AUTO = 1

        const val OPEN_BEFORE_NESTED_SCROLL = 1
        const val OPEN_AFTER_NESTED_SCROLL = 2
        const val CLOSE_BEFORE_NESTED_SCROLL = 4
        const val CLOSE_AFTER_NESTED_SCROLL = 8
    }

    private var onExpandStateChangeListener: ((view: SlideMenuLayout, target: View, expanded: Boolean) -> Unit)? = null
    private var onSlideChangeListener: ((view: SlideMenuLayout, target: View, value: Float) -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private val View.lp: LayoutParams
        get() = layoutParams as LayoutParams

    private fun <T> View.getSlideValue(x: T, y: T) = if (isHorizontal()) x else y

    private fun View.isHorizontal() = lp.slideDirection == SLIDE_FROM_LEFT || lp.slideDirection == SLIDE_FROM_RIGHT

    private fun View.minSlide() = minOf(0f, lp.slideSize)

    private fun View.maxSlide() = maxOf(0f, lp.slideSize)

    private fun View.currentSlide() = getSlideValue(translationX, translationY)

    private fun View.setCurrentSlide(value: Float) {
        if (isHorizontal()) {
            translationX = value
        } else {
            translationY = value
        }
    }

    private fun View.hasScrollFlag(flag: Int): Boolean = lp.scrollBehavior and flag == flag

    private fun View.hasNestedScrollFlag(flag: Int): Boolean = lp.nestedScrollBehavior and flag == flag

    private fun View.isInside(x: Float, y: Float): Boolean {
        val cx = x - translationX
        val cy = y - translationY
        return cx >= left && cy >= top && cx <= right && cy <= bottom
    }

    private fun getChildById(id: Int) = (0 until childCount).firstOrNull { getChildAt(it).id == id }?.let { getChildAt(it) }

    private fun getActiveChild() = (0 until childCount).map { getChildAt(it) }.firstOrNull { it.lp.isInScroll }


    private fun findNestedScrollTarget() = (0 until childCount).map { getChildAt(it) }.firstOrNull { it.lp.nestedScrollBehavior != 0 && it.lp.slideEnabled }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        val nst = findNestedScrollTarget() ?: return false
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_HORIZONTAL && nst.isHorizontal() || nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && !nst.isHorizontal()
    }

    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
    }

    override fun onStopNestedScroll(target: View) {
        finishMenuScroll(findNestedScrollTarget() ?: return)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        val nst = findNestedScrollTarget() ?: return
        val expandLast = nst.hasNestedScrollFlag(OPEN_AFTER_NESTED_SCROLL)
        val collapseLast = nst.hasNestedScrollFlag(CLOSE_AFTER_NESTED_SCROLL)
        if (nst.lp.slideDirection == SLIDE_FROM_TOP && (expandLast && dyUnconsumed < 0 || collapseLast && dyUnconsumed > 0)
                || nst.lp.slideDirection == SLIDE_FROM_BOTTOM && (expandLast && dyUnconsumed > 0 || collapseLast && dyUnconsumed < 0)) {
            val used = scrollMenuBy(nst, -dyUnconsumed)
            nst.lp.slideVelocity = used.toFloat()
            target.offsetTopAndBottom(used)
        }
        //todo horizontal
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        val nst = findNestedScrollTarget() ?: return
        val expandFirst = nst.hasNestedScrollFlag(OPEN_BEFORE_NESTED_SCROLL)
        val collapseFirst = nst.hasNestedScrollFlag(CLOSE_BEFORE_NESTED_SCROLL)
        if (nst.lp.slideDirection == SLIDE_FROM_TOP && (expandFirst && dy < 0 || collapseFirst && dy > 0)
                || nst.lp.slideDirection == SLIDE_FROM_BOTTOM && (expandFirst && dy > 0 || collapseFirst && dy < 0)) {
            val used = scrollMenuBy(nst, -dy)
            nst.lp.slideVelocity = used.toFloat()
            consumed[1] = -used
            Log.i("AAA", "A: $dy->$used")
            target.offsetTopAndBottom(used)
        }
        //todo horizontal
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        val nst = findNestedScrollTarget() ?: return false
        return !(nst.currentSlide().closeTo(nst.minSlide(), 0.001f) || nst.currentSlide().closeTo(nst.maxSlide(), 0.001f))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val w = measuredWidth
        val h = measuredHeight
        forEachChildren {
            if (it.lp.slideEnabled) {
                val oldSize = it.lp.slideSize
                it.lp.slideSize = when {
                    it.lp.slideSizeAmount != 0 -> it.lp.slideSizeAmount * (if (it.lp.slideDirection == SLIDE_FROM_RIGHT || it.lp.slideDirection == SLIDE_FROM_BOTTOM) 1f else -1f)
                    it.lp.slideDirection == SLIDE_FROM_LEFT -> -it.right.toFloat()
                    it.lp.slideDirection == SLIDE_FROM_TOP -> -it.bottom.toFloat()
                    it.lp.slideDirection == SLIDE_FROM_RIGHT -> w - it.left.toFloat()
                    it.lp.slideDirection == SLIDE_FROM_BOTTOM -> h - it.top.toFloat()
                    else -> 0f
                }
                if (oldSize != it.lp.slideSize) {
                    if (it.lp.slided) {
                        expand(it, false)
                    } else {
                        collapse(it, false)
                    }
                }
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean = dispatchTouch(ev, true)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        dispatchTouch(ev, false)
        return true
    }

    private fun dispatchTouch(ev: MotionEvent, isIntercept: Boolean): Boolean {
        invalidate()

        val target = getActiveChild() ?: (0 until childCount).mapNotNull { getChildAt(it) }.mapNotNull { child ->
            when {
                child.visibility == View.GONE -> null
                !child.isInside(ev.x, ev.y) -> null
                child.lp.slideEnabled && child.lp.slideSelfEnabled -> child
                child.lp.slideZoneForId != 0 -> {
                    val potentialTarget = getChildById(child.lp.slideZoneForId)
                    if (potentialTarget != null && potentialTarget.lp.slideEnabled && potentialTarget.visibility != View.GONE) {
                        potentialTarget
                    } else {
                        null
                    }
                }
                else -> null
            }
        }.firstOrNull() ?: return false

        val lp = target.lp

        val curPos = target.getSlideValue(ev.x, ev.y)

        if (ev.action == MotionEvent.ACTION_DOWN) {
            target.clearAnimation()
            lp.isInScroll = false
            lp.slideVelocity = 0f
            lp.slideLastPoint = curPos
            lp.slideLastPointTime = System.currentTimeMillis()
            return !isIntercept
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            return if (lp.isInScroll) {
                scrollMenuBy(target, (curPos - lp.slideLastPoint).toInt())
                lp.slideVelocity = 1f * (curPos - lp.slideLastPoint) / (System.currentTimeMillis() - lp.slideLastPointTime)
                lp.slideLastPoint = curPos
                lp.slideLastPointTime = System.currentTimeMillis()
                true
            } else {
                if (Math.abs(lp.slideLastPoint - curPos) > lp.slideMinDistance) {
                    parent?.requestDisallowInterceptTouchEvent(true)
                    lp.slideLastPoint = curPos
                    lp.slideLastPointTime = System.currentTimeMillis()
                    lp.isInScroll = true
                    true
                } else false
            }
        } else if (ev.action == MotionEvent.ACTION_CANCEL || ev.action == MotionEvent.ACTION_UP)
            if (lp.isInScroll) {
                finishMenuScroll(target)
            }
        return false
    }

    private fun finishMenuScroll(target: View) {
        val slideValue = target.currentSlide()
        if (target.hasScrollFlag(BEHAVIOUR_AUTO)) { //finish scroll
            if (slideValue.closeTo(target.maxSlide(), 0.001f) || slideValue.closeTo(target.minSlide(), 0.001f)) return
            if (target.isHorizontal()) {
                if (target.lp.slideVelocity > 0 && target.lp.slideDirection == SLIDE_FROM_LEFT || target.lp.slideVelocity < 0 && target.lp.slideDirection == SLIDE_FROM_RIGHT) {
                    expand(target)
                } else {
                    collapse(target)
                }
            } else {
                if (target.lp.slideVelocity > 0 && target.lp.slideDirection == SLIDE_FROM_TOP || target.lp.slideVelocity < 0 && target.lp.slideDirection == SLIDE_FROM_BOTTOM) {
                    expand(target)
                } else {
                    collapse(target)
                }
            }
        } else {
            setScroll(target, true, (slideValue + target.lp.slideVelocity * 50).clamp(target.minSlide(), target.maxSlide()))
        }
    }

    fun scrollMenuTo(target: View, pos: Int) {
        scrollMenuBy(target, pos - target.currentSlide().toInt())
    }

    fun scrollMenuBy(target: View, amount: Int): Int {
        if (amount == 0) return 0
        val was = target.currentSlide()
        val wasInScroll = target.lp.isInScroll
        setScroll(target, false, was + amount)
        target.lp.isInScroll = wasInScroll
        return (target.currentSlide() - was).toInt()
    }

    fun toggle(target: View, animate: Boolean = true) {
        if (target.lp.slided) {
            collapse(target, animate)
        } else {
            expand(target, animate)
        }
    }

    fun expand(target: View, animate: Boolean = true) {
        setScroll(target, animate, 0f)
    }

    fun collapse(target: View, animate: Boolean = true) {
        setScroll(target, animate, target.lp.slideSize)
    }

    private fun setScroll(target: View, animate: Boolean, value: Float, interpolator: Interpolator? = DecelerateInterpolator()) {
        target.animate().cancel()
        val targetValue = value.clamp(target.minSlide(), target.maxSlide())

        val wasSlided = target.lp.slided
        target.lp.slided = targetValue.closeTo(0f, 0.001f)
        if (wasSlided != target.lp.slided) {
            notifySlideChanged(target)
        }

        if (animate) {
            target.animate().let { if (target.isHorizontal()) it.translationX(targetValue) else it.translationY(targetValue) }
                    .setInterpolator(interpolator).setDuration(target.lp.slideAutoFinishDuration.toLong()).setUpdateListener {
                        notifySlideSizeChanged(target)
                    }
        } else {
            target.setCurrentSlide(targetValue)
            notifySlideSizeChanged(target)
        }
        invalidate()
        target.lp.isInScroll = false
    }

    private fun notifySlideSizeChanged(target: View) {
        onSlideChangeListener?.invoke(this, target, target.currentSlide() / target.maxSlide())
        var needLayout = false
        forEachChildren {
            if (it.lp.slideAnchorForId == target.id) {
                val dx = target.translationX.toInt()
                val dy = target.translationY.toInt()
                if (it.layoutParams !is AnchorLayoutParams) {
                    it.layoutParams = AnchorLayoutParams(it.lp.slideAnchorForId)
                }
                it.lp.apply {
                    leftMargin = target.left + dx
                    topMargin = target.top + dy
                    width = target.measuredWidth
                    height = target.measuredHeight
                }
                needLayout = true
            }
        }
        if (needLayout) {
            requestLayout()
        }
    }

    private fun notifySlideChanged(target: View) {
        onExpandStateChangeListener?.invoke(this, target, target.lp.slided)
    }

    fun setOnExpandStateChangeListener(onExpandStateChangeListener: ((view: SlideMenuLayout, target: View, expanded: Boolean) -> Unit)?) {
        this.onExpandStateChangeListener = onExpandStateChangeListener
    }

    fun setOnSlideChangeListener(onSlideChangeListener: ((view: SlideMenuLayout, target: View, value: Float) -> Unit)?) {
        this.onSlideChangeListener = onSlideChangeListener
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            forEachChildren { it.dispatchApplyWindowInsets(WindowInsets(insets)) }
            insets
        } else super.onApplyWindowInsets(insets)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams = LayoutParams(context, attrs)

    override fun generateDefaultLayoutParams(): LayoutParams = LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean = p is LayoutParams

    @Suppress("MemberVisibilityCanBePrivate")
    open class LayoutParams : RelativeLayout.LayoutParams {

        internal var isInScroll = false
        internal var slideLastPoint: Float = 0f
        internal var slideLastPointTime: Long = 0
        internal var slideVelocity: Float = 0f
        internal var slideSize: Float = 0f

        var slided: Boolean = true
        var slideEnabled: Boolean = false
        var slideSelfEnabled: Boolean = true
        var slideSizeAmount: Int = 0
        var slideMinDistance: Float = 0f
        var slideAutoFinishDuration: Int = 250
        var slideDirection: Int = 0
        var scrollBehavior: Int = BEHAVIOUR_AUTO
        var nestedScrollBehavior: Int = 0

        var slideZoneForId: Int = 0
        var slideAnchorForId: Int = 0

        constructor(w: Int, h: Int) : super(w, h)

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.SlideMenuLayout_Layout, 0, 0)
            slided = a.getBoolean(R.styleable.SlideMenuLayout_Layout_slided, true)
            slideEnabled = a.getBoolean(R.styleable.SlideMenuLayout_Layout_slideEnabled, false)
            slideSelfEnabled = a.getBoolean(R.styleable.SlideMenuLayout_Layout_slideSelfEnabled, true)
            slideSizeAmount = a.getDimensionPixelSize(R.styleable.SlideMenuLayout_Layout_slideSizeAmount, 0)
            slideMinDistance = a.getDimensionPixelSize(R.styleable.SlideMenuLayout_Layout_slideMinDistance, ViewConfiguration.get(c).scaledTouchSlop).toFloat()
            slideAutoFinishDuration = a.getInt(R.styleable.SlideMenuLayout_Layout_slideAutoFinishDuration, 250)
            slideDirection = a.getInt(R.styleable.SlideMenuLayout_Layout_slideDirection, 0)
            scrollBehavior = a.getInt(R.styleable.SlideMenuLayout_Layout_scrollBehavior, BEHAVIOUR_AUTO)
            nestedScrollBehavior = a.getInt(R.styleable.SlideMenuLayout_Layout_nestedScrollBehavior, 0)
            slideZoneForId = a.getResourceId(R.styleable.SlideMenuLayout_Layout_slideZoneForId, 0)
            slideAnchorForId = a.getResourceId(R.styleable.SlideMenuLayout_Layout_slideAnchorForId, 0)
            a.recycle()
        }
    }

    class AnchorLayoutParams(anchor: Int) : LayoutParams(-2, -2) {
        init {
            slideAnchorForId = anchor
        }
    }
}