package by.vkatz.support.widgets

import android.content.Context
import android.support.v4.view.NestedScrollingParent
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import by.vkatz.support.R
import by.vkatz.widgets.SlideMenuLayout

/**
 * Created by Katz on 06.06.2016.   //TEST MODE, NOT YET FINISHED
 */

class NestedSlideMenuLayout : SlideMenuLayout, NestedScrollingParent {

    private val nestedScrollFlags: Int
    private var nestedScrollAxes: Int = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NestedSlideMenuLayout, 0, 0)
        nestedScrollFlags = a.getInt(R.styleable.NestedSlideMenuLayout_nestedScrollBehavior, FLAG_EXPAND_FIRST or FLAG_COLLAPSE_FIRST)
        a.recycle()
        ViewCompat.setNestedScrollingEnabled(this, true)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_HORIZONTAL && isHorizontal() || nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && isVertical()
    }

    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
        this.nestedScrollAxes = nestedScrollAxes
        scrollMenuBy(0)
    }

    override fun onStopNestedScroll(target: View) {
        nestedScrollAxes = ViewCompat.SCROLL_AXIS_NONE
        finishMenuScroll()
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        val expandLast = hasFlag(nestedScrollFlags, FLAG_EXPAND_LAST)
        val collapseLast = hasFlag(nestedScrollFlags, FLAG_COLLAPSE_LAST)
        if (slideFrom == SlideMenuLayout.Companion.TOP && (expandLast && dyUnconsumed < 0 || collapseLast && dyUnconsumed > 0) || slideFrom == SlideMenuLayout.Companion.BOTTOM && (expandLast && dyUnconsumed > 0 || collapseLast && dyUnconsumed < 0)) {
            val used = scrollMenuBy(-dyUnconsumed)
            target.offsetTopAndBottom(used)
        }
        //todo horizontal
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        val expandFirst = hasFlag(nestedScrollFlags, FLAG_EXPAND_FIRST)
        val collapseFirst = hasFlag(nestedScrollFlags, FLAG_COLLAPSE_FIRST)
        if (slideFrom == SlideMenuLayout.Companion.TOP && (expandFirst && dy < 0 || collapseFirst && dy > 0) || slideFrom == SlideMenuLayout.Companion.BOTTOM && (expandFirst && dy > 0 || collapseFirst && dy < 0)) {
            val used = scrollMenuBy(-dy)
            consumed[1] -= used
            target.offsetTopAndBottom(used)
        }
        //todo horizontal
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return false
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun getNestedScrollAxes(): Int {
        return nestedScrollAxes
    }

    companion object {
        val FLAG_EXPAND_FIRST = 1
        val FLAG_EXPAND_LAST = 2
        val FLAG_COLLAPSE_FIRST = 4
        val FLAG_COLLAPSE_LAST = 8
    }
}
