package by.vkatz.support.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import by.vkatz.support.R;
import by.vkatz.widgets.SlideMenuLayout;

/**
 * Created by Katz on 06.06.2016.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class NestedSlideMenuLayout extends SlideMenuLayout implements NestedScrollingParent {
    public static final int FLAG_EXPAND_FIRST = 1;
    public static final int FLAG_EXPAND_LAST = 2;
    public static final int FLAG_COLLAPSE_FIRST = 4;
    public static final int FLAG_COLLAPSE_LAST = 8;

    private int nestedScrollFlags;
    private int nestedScrollAxes;

    public NestedSlideMenuLayout(Context context) {
        this(context, null);
    }

    public NestedSlideMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedSlideMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NestedSlideMenuLayout, 0, 0);
        nestedScrollFlags = a.getInt(R.styleable.NestedSlideMenuLayout_nestedScrollBehavior, FLAG_EXPAND_FIRST | FLAG_COLLAPSE_FIRST);
        a.recycle();
        ViewCompat.setNestedScrollingEnabled(this, true);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes == ViewCompat.SCROLL_AXIS_HORIZONTAL && isHorizontal()) || (nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && isVertical());
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        this.nestedScrollAxes = nestedScrollAxes;
        scrollMenuBy(0);
    }

    @Override
    public void onStopNestedScroll(View target) {
        nestedScrollAxes = ViewCompat.SCROLL_AXIS_NONE;
        finisMenuScroll();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        boolean expandLast = hasFlag(nestedScrollFlags, FLAG_EXPAND_LAST);
        boolean collapseLast = hasFlag(nestedScrollFlags, FLAG_COLLAPSE_LAST);
        if ((getSlideFrom() == TOP && ((expandLast && dyUnconsumed < 0) || (collapseLast && dyUnconsumed > 0))) ||
                (getSlideFrom() == BOTTOM && ((expandLast && dyUnconsumed > 0) || (collapseLast && dyUnconsumed < 0)))) {
            int used = scrollMenuBy(-dyUnconsumed);
            target.offsetTopAndBottom(used);
        }
        //todo horizontal
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        boolean expandFirst = hasFlag(nestedScrollFlags, FLAG_EXPAND_FIRST);
        boolean collapseFirst = hasFlag(nestedScrollFlags, FLAG_COLLAPSE_FIRST);
        if ((getSlideFrom() == TOP && ((expandFirst && dy < 0) || (collapseFirst && dy > 0))) ||
                (getSlideFrom() == BOTTOM && ((expandFirst && dy > 0) || (collapseFirst && dy < 0)))) {
            int used = scrollMenuBy(-dy);
            consumed[1] -= used;
            target.offsetTopAndBottom(used);
        }
        //todo horizontal
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return nestedScrollAxes;
    }
}
