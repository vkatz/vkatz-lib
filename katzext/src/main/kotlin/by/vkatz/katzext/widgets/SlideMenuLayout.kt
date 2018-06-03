package by.vkatz.katzext.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import by.vkatz.katzext.R
import by.vkatz.katzext.utils.clamp
import by.vkatz.katzext.utils.closeTo
import by.vkatz.katzext.utils.forEachChildren
import by.vkatz.katzext.utils.makeVisibleOrGone
import kotlinx.android.parcel.Parcelize


@Suppress("MemberVisibilityCanPrivate", "MemberVisibilityCanBePrivate", "unused")
open class SlideMenuLayout : ConstraintLayout, NestedScrollingParent2, NestedScrollingChild2 {
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

        const val VELOCITY_FRICTION = 0.1
    }

    private val minVelocity: Int
    private val maxVelocity: Int
    private var mParentHelper: NestedScrollingParentHelper? = null
    private var mChildHelper: NestedScrollingChildHelper? = null
    private var velocityTracker: VelocityTracker? = null
    private var onExpandStateChangeListener: ((view: SlideMenuLayout, target: View, expanded: Boolean) -> Unit)? = null
    private var onSlideChangeListener: ((view: SlideMenuLayout, target: View, value: Float) -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val vc = ViewConfiguration.get(context)
        minVelocity = vc.scaledMinimumFlingVelocity
        maxVelocity = vc.scaledMaximumFlingVelocity
    }

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

    //---------------------------Nested scroll part start -----------------------------------------

    private fun findNestedScrollTarget() = (0 until childCount).map { getChildAt(it) }.firstOrNull { it.lp.nestedScrollBehavior != 0 && it.lp.slideEnabled }

    private fun getNestedParentHelper(): NestedScrollingParentHelper {
        if (mParentHelper == null) {
            mParentHelper = NestedScrollingParentHelper(this)
        }
        return mParentHelper!!
    }

    private fun getNestedChildHelper(): NestedScrollingChildHelper {
        if (mChildHelper == null) {
            mChildHelper = NestedScrollingChildHelper(this)
        }
        return mChildHelper!!
    }

    override fun getNestedScrollAxes(): Int {
        return getNestedParentHelper().nestedScrollAxes
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        getNestedParentHelper().onStopNestedScroll(target, type)
        val nst = findNestedScrollTarget() ?: return
        if (nst.lp.scrollBehavior == BEHAVIOUR_AUTO) {
            finishMenuScroll(nst)
        }
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        val nst = findNestedScrollTarget() ?: return false
        isNestedScrollingEnabled = true
        return axes == ViewCompat.SCROLL_AXIS_HORIZONTAL && nst.isHorizontal() || axes == ViewCompat.SCROLL_AXIS_VERTICAL && !nst.isHorizontal()
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        getNestedParentHelper().onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val nst = findNestedScrollTarget()
        var usedX = 0
        var usedY = 0
        if (nst != null) {
            val expandFirst = nst.hasNestedScrollFlag(OPEN_BEFORE_NESTED_SCROLL)
            val collapseFirst = nst.hasNestedScrollFlag(CLOSE_BEFORE_NESTED_SCROLL)
            if (nst.lp.slideDirection == SLIDE_FROM_TOP && (expandFirst && dy < 0 || collapseFirst && dy > 0)
                    || nst.lp.slideDirection == SLIDE_FROM_BOTTOM && (expandFirst && dy > 0 || collapseFirst && dy < 0)) {
                usedY = scrollMenuBy(nst, -dy)
                target.offsetTopAndBottom(usedY)
                if (nst.lp.scrollBehavior == BEHAVIOUR_AUTO) {
                    nst.lp.slideVelocity = -dy
                }
            }
            if (nst.lp.slideDirection == SLIDE_FROM_LEFT && (expandFirst && dx < 0 || collapseFirst && dx > 0)
                    || nst.lp.slideDirection == SLIDE_FROM_RIGHT && (expandFirst && dx > 0 || collapseFirst && dx < 0)) {
                usedX = scrollMenuBy(nst, -dx)
                target.offsetLeftAndRight(usedY)
                if (nst.lp.scrollBehavior == BEHAVIOUR_AUTO) {
                    nst.lp.slideVelocity = -dx
                }
            }
        }
        consumed[0] -= usedX
        consumed[1] -= usedY
        getNestedChildHelper().dispatchNestedPreScroll(dx, dy, consumed, null, type)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        val nst = findNestedScrollTarget()
        var usedX = 0
        var usedY = 0
        if (nst != null) {
            val expandLast = nst.hasNestedScrollFlag(OPEN_AFTER_NESTED_SCROLL)
            val collapseLast = nst.hasNestedScrollFlag(CLOSE_AFTER_NESTED_SCROLL)
            if (nst.lp.slideDirection == SLIDE_FROM_TOP && (expandLast && dyUnconsumed < 0 || collapseLast && dyUnconsumed > 0)
                    || nst.lp.slideDirection == SLIDE_FROM_BOTTOM && (expandLast && dyUnconsumed > 0 || collapseLast && dyUnconsumed < 0)) {
                usedY = scrollMenuBy(nst, -dyUnconsumed)
                target.offsetTopAndBottom(usedY)
                if (nst.lp.scrollBehavior == BEHAVIOUR_AUTO) {
                    nst.lp.slideVelocity = -dyUnconsumed
                }
            }
            if (nst.lp.slideDirection == SLIDE_FROM_LEFT && (expandLast && dxUnconsumed < 0 || collapseLast && dxUnconsumed > 0)
                    || nst.lp.slideDirection == SLIDE_FROM_RIGHT && (expandLast && dxUnconsumed > 0 || collapseLast && dxUnconsumed < 0)) {
                usedX = scrollMenuBy(nst, -dxUnconsumed)
                target.offsetLeftAndRight(usedX)
                if (nst.lp.scrollBehavior == BEHAVIOUR_AUTO) {
                    nst.lp.slideVelocity = -dxUnconsumed
                }
            }
        }
        getNestedChildHelper().dispatchNestedScroll(dxConsumed + usedX, dyConsumed + usedY, dxUnconsumed - usedX, dyUnconsumed - usedY, null, type)
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return getNestedChildHelper().startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        getNestedChildHelper().stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return getNestedChildHelper().hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int): Boolean {
        return getNestedChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {
        return getNestedChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return getNestedChildHelper().dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return getNestedChildHelper().dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        getNestedChildHelper().isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return getNestedChildHelper().isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return getNestedChildHelper().startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        getNestedChildHelper().stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return getNestedChildHelper().hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return getNestedChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return getNestedChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        if (!consumed) {
            attemptNestedFling(velocityX, velocityY)
            return true
        }
        return false
    }

    //---------------------------Nested scroll part end ------------------------------------------

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
            velocityTracker?.recycle()
            velocityTracker = VelocityTracker.obtain()
            target.clearAnimation()
            lp.isInScroll = false
            lp.slideVelocity = 0
            lp.slideLastPoint = curPos
            return !isIntercept
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            velocityTracker?.addMovement(ev)
            return if (lp.isInScroll) {
                scrollMenuBy(target, (curPos - lp.slideLastPoint).toInt())
                lp.slideLastPoint = curPos
                true
            } else {
                if (Math.abs(lp.slideLastPoint - curPos) > lp.slideMinDistance) {
                    parent?.requestDisallowInterceptTouchEvent(true)
                    lp.slideLastPoint = curPos
                    lp.isInScroll = true
                    true
                } else false
            }
        } else if (ev.action == MotionEvent.ACTION_CANCEL || ev.action == MotionEvent.ACTION_UP)
            if (lp.isInScroll) {
                velocityTracker?.computeCurrentVelocity(1000, maxVelocity.toFloat())
                lp.slideVelocity = target.getSlideValue(velocityTracker?.xVelocity, velocityTracker?.yVelocity)?.toInt() ?: 0
                velocityTracker?.recycle()
                velocityTracker = null
                finishMenuScroll(target)
            }
        return false
    }

    private fun finishMenuScroll(target: View) {
        val slideValue = target.currentSlide()
        if (target.hasScrollFlag(BEHAVIOUR_AUTO)) { //finish scroll
            if (slideValue.closeTo(target.maxSlide()) || slideValue.closeTo(target.minSlide())) return
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
            val velocityX = if (target.isHorizontal()) target.lp.slideVelocity.toFloat() else 0f
            val velocityY = if (target.isHorizontal()) 0f else target.lp.slideVelocity.toFloat()
            attemptNestedFling(velocityX, velocityY)
        }
    }

    private fun attemptNestedFling(velocityX: Float, velocityY: Float) {
        forEachChildren {
            when (it) {
                is RecyclerView -> {
                    it.fling(-velocityX.toInt(), -velocityY.toInt())
                    return@forEachChildren
                }
                is NestedScrollView -> {
                    it.fling(-velocityY.toInt())
                    return@forEachChildren
                }
            }
        }
    }

    fun scrollMenuTo(target: View, pos: Int) {
        scrollMenuBy(target, pos - target.currentSlide().toInt())
    }

    fun scrollMenuBy(target: View, amount: Int): Int {
        if (amount == 0) return 0
        val was = target.currentSlide()
        val wasInScroll = target.lp.isInScroll
        setScroll(target, false, was + amount, 0)
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
        setScroll(target, animate, 0f, target.lp.slideAutoFinishDuration)
    }

    fun collapse(target: View, animate: Boolean = true) {
        setScroll(target, animate, target.lp.slideSize, target.lp.slideAutoFinishDuration)
    }

    private fun setScroll(target: View, animate: Boolean, value: Float, duration: Long, interpolator: Interpolator? = DecelerateInterpolator()) {
        if (target.lp.slideSize.closeTo(0f)) return

        target.animate().cancel()
        val targetValue = value.clamp(target.minSlide(), target.maxSlide())

        val wasSlided = target.lp.slided
        target.lp.slided = targetValue.closeTo(0f)
        if (wasSlided != target.lp.slided) {
            onSlideChanged(target)
        }

        if (animate) {
            target.animate().let { if (target.isHorizontal()) it.translationX(targetValue) else it.translationY(targetValue) }
                    .setInterpolator(interpolator).setDuration(duration).setUpdateListener {
                        onSlideSizeChanged(target)
                    }
        } else {
            target.setCurrentSlide(targetValue)
            onSlideSizeChanged(target)
        }
        invalidate()
        target.lp.isInScroll = false
    }

    private fun onSlideSizeChanged(target: View) {
        onSlideChangeListener?.invoke(this, target, target.currentSlide() / target.maxSlide())
        updateAnchors(target)
    }

    private fun updateAnchors(target: View) {
        var needLayout = false
        forEachChildren {
            if (it.lp.slideAnchorForId == target.id) {
                val dx = target.translationX.toInt()
                val dy = target.translationY.toInt()
                updateAnchor(it, target.left + dx, target.top + dy, target.measuredWidth, target.measuredHeight)
                needLayout = true
            }
        }
        if (needLayout) {
            requestLayout()
        }
    }

    private fun updateAnchor(target: View, left: Int, top: Int, w: Int, h: Int, clamp: Boolean = true) {
        if (target.layoutParams !is AnchorLayoutParams) {
            target.layoutParams = AnchorLayoutParams(target.lp.slideAnchorForId)
        }
        target.lp.apply {
            leftMargin = if (clamp) left.clamp(0, measuredWidth) else left
            topMargin = if (clamp) top.clamp(0, measuredHeight) else top
            width = if (clamp) (w + (left - leftMargin)).clamp(0, measuredWidth) else w
            height = if (clamp) (h + (top - topMargin)).clamp(0, measuredHeight) else h
            target.makeVisibleOrGone(height != 0 && width != 0)
        }
    }

    private fun onSlideChanged(target: View) {
        onExpandStateChangeListener?.invoke(this, target, target.lp.slided)
    }

    fun setOnExpandStateChangeListener(onExpandStateChangeListener: ((view: SlideMenuLayout, target: View, expanded: Boolean) -> Unit)?) {
        this.onExpandStateChangeListener = onExpandStateChangeListener
    }

    fun setOnSlideChangeListener(onSlideChangeListener: ((view: SlideMenuLayout, target: View, value: Float) -> Unit)?) {
        this.onSlideChangeListener = onSlideChangeListener
    }

    fun getMenuParams(target: View) = target.lp

    fun isExpanded(target: View) = target.lp.slided

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            forEachChildren { it.dispatchApplyWindowInsets(WindowInsets(insets)) }
            insets
        } else super.onApplyWindowInsets(insets)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams = LayoutParams(context, attrs)

    override fun generateDefaultLayoutParams(): LayoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean = p is LayoutParams

    override fun onSaveInstanceState(): Parcelable? {
        val data = SaveStateData(ArrayList(), ArrayList())
        forEachChildren {
            if (it.id != 0) {
                if (it.lp.slideEnabled) {
                    data.states.add(ViewStateData(it.id, it.currentSlide(), it.lp.slideSize))
                }
                if (it.lp.slideAnchorForId != 0) {
                    data.anchors.add(AnchorStateData(it.id, it.lp.leftMargin, it.lp.topMargin, it.lp.width, it.lp.height))
                }
            }

        }
        return SavedState(data, super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            val data = state.state
            data.states.forEach {
                getChildById(it.id)?.apply {
                    lp.slideSize = it.slideSize
                    setCurrentSlide(it.slide)
                    if (lp.scrollBehavior == BEHAVIOUR_AUTO) {
                        post { finishMenuScroll(this) }
                    }
                }
            }
            data.anchors.forEach {
                getChildById(it.id)?.apply {
                    updateAnchor(this, it.left, it.top, it.w, it.h, false)
                }
            }
            super.onRestoreInstanceState(state.superState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    open class LayoutParams : ConstraintLayout.LayoutParams {

        internal var isInScroll = false
        internal var slideLastPoint: Float = 0f
        internal var slideVelocity: Int = 0
        internal var slideSize: Float = 0f

        var slided: Boolean = true
        var slideEnabled: Boolean = false
        var slideSelfEnabled: Boolean = true
        var slideSizeAmount: Int = 0
        var slideMinDistance: Float = 0f
        var slideAutoFinishDuration: Long = 250
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
            slideAutoFinishDuration = a.getInt(R.styleable.SlideMenuLayout_Layout_slideAutoFinishDuration, 250).toLong()
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

    @Parcelize
    data class ViewStateData(var id: Int, var slide: Float, var slideSize: Float) : Parcelable

    @Parcelize
    data class AnchorStateData(var id: Int, var left: Int, var top: Int, var w: Int, var h: Int) : Parcelable

    @Parcelize
    data class SaveStateData(var states: ArrayList<ViewStateData>, var anchors: ArrayList<AnchorStateData>) : Parcelable

    class SavedState : View.BaseSavedState {
        val state: SaveStateData

        constructor(state: SaveStateData, superState: Parcelable) : super(superState) {
            this.state = state
        }

        constructor(parcel: Parcel) : super(parcel) {
            state = parcel.readParcelable(javaClass.classLoader)
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeParcelable(state, 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}