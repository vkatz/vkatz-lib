package by.vkatz.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import by.vkatz.R;

/**
 * Created by vKatz on 18.02.2015.
 */
@SuppressWarnings("unused")
public class SlideMenuLayout extends RelativeLayout {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int TOP = 3;
    public static final int BOTTOM = 4;

    private int slideFrom;
    private boolean expanded;
    private boolean enabled;
    private boolean scroll;
    private boolean autoScroll;
    private boolean onScreen;
    private float pos;
    private int dPos;
    private int slideSize;
    private float startScrollDistance;
    private Scroller scroller;
    private OnExpandStateChangeListener onExpandStateChangeListener;
    private OnSlideChangeListener onSlideChangeListener;

    public SlideMenuLayout(Context context) {
        this(context, null);
    }

    public SlideMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideMenuLayout, 0, 0);
        slideFrom = a.getInt(R.styleable.SlideMenuLayout_slideFrom, 2);
        expanded = a.getBoolean(R.styleable.SlideMenuLayout_expanded, false);
        enabled = a.getBoolean(R.styleable.SlideMenuLayout_enabled, true);
        startScrollDistance = a.getDimensionPixelSize(R.styleable.SlideMenuLayout_startScrollDistance, 25);
        a.recycle();
        scroller = new Scroller(context);
        scroll = false;
        autoScroll = false;
        onScreen = false;
        setWillNotDraw(false);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        View menu = null;
        boolean hasMenu = false;
        for (int i = 0; i < getChildCount(); i++)
            if (((LayoutParams) getChildAt(i).getLayoutParams()).isMenu) {
                menu = getChildAt(i);
                if (hasMenu) throw new RuntimeException("Should not contain more than 1 menu");
                hasMenu = true;
            }
        if (menu == null) slideSize = 0;
        else {
            if (slideFrom == BOTTOM) slideSize = getMeasuredHeight() - menu.getTop();
            else if (slideFrom == TOP) slideSize = -menu.getBottom();
            else if (slideFrom == RIGHT) slideSize = getMeasuredWidth() - menu.getLeft();
            else if (slideFrom == LEFT) slideSize = -menu.getRight();
            else slideSize = 0;
            if (!onScreen && !expanded) {
                scroller.startScroll(0, 0, isHorizontal() ? slideSize : 0, isHorizontal() ? 0 : slideSize, 0);
                invalidate();
            }
        }
    }

    private boolean isHorizontal() {
        return slideFrom == RIGHT || slideFrom == LEFT;
    }

    private boolean isEventInsideChild(float x, float y) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (!lp.isTouchable || child.getVisibility() == GONE) continue;
            int cx = (int) x;
            int cy = (int) y;
            if (lp.isMovable || lp.isMenu) {
                cx -= scroller.getCurrX();
                cy -= scroller.getCurrY();
            }
            if (cx >= child.getLeft() && cy >= child.getTop() && cx <= child.getRight() && cy <= child.getBottom()) return true;
        }
        return false;
    }

    private boolean dispatchTouch(MotionEvent ev, boolean isIntercept) {
        if (autoScroll) return false;
        float curPos = isHorizontal() ? ev.getX() : ev.getY();
        if (!scroll && !isEventInsideChild(ev.getX(), ev.getY())) return false;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            scroll = false;
            pos = curPos;
            return !isIntercept;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (scroll) {
                dPos = (int) (curPos - pos);
                int scroll = dPos;
                if (isHorizontal()) {
                    float fPos = scroller.getCurrX() + dPos;
                    if (Math.abs(fPos) > Math.abs(slideSize)) scroll = slideSize - scroller.getCurrX();
                    if (fPos * (slideFrom == LEFT ? -1 : 1) < 0) scroll = -scroller.getCurrX();
                    scroller.startScroll(scroller.getCurrX(), 0, scroll, 0, 0);
                } else {
                    float fPos = scroller.getCurrY() + dPos;
                    if (Math.abs(fPos) > Math.abs(slideSize)) scroll = slideSize - scroller.getCurrY();
                    if (fPos * (slideFrom == TOP ? -1 : 1) < 0) scroll = -scroller.getCurrY();
                    scroller.startScroll(0, scroller.getCurrY(), 0, scroll, 0);
                }
                pos = curPos;
                invalidate();
                return true;
            } else {
                if (Math.abs(pos - curPos) > startScrollDistance) {
                    pos = curPos;
                    scroll = true;
                    return true;
                } else return false;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP) {
            if (scroll) {
                if (isHorizontal()) {
                    if ((dPos > 0 && slideFrom == LEFT) || (dPos < 0 && slideFrom == RIGHT)) expand();
                    else collapse();
                } else {
                    if ((dPos > 0 && slideFrom == TOP) || (dPos < 0 && slideFrom == BOTTOM)) expand();
                    else collapse();
                }
            }
        }
        return false;
    }

    public void expand() {
        expand(true);
    }

    public void expand(boolean anim) {
        scroller.startScroll(scroller.getCurrX(), scroller.getCurrY(), -scroller.getCurrX(), -scroller.getCurrY(), anim ? 250 : 0);
        autoScroll = true;
        runInvalidates();
    }

    public void collapse() {
        collapse(true);
    }

    public void collapse(boolean anim) {
        scroller.startScroll(scroller.getCurrX(), scroller.getCurrY(), (isHorizontal() ? slideSize : 0) - scroller.getCurrX(), (isHorizontal() ? 0 : slideSize) - scroller.getCurrY(), anim ? 250 : 0);
        autoScroll = true;
        runInvalidates();
    }

    private void runInvalidates() {
        new Runnable() {
            @Override
            public void run() {
                invalidate();
                if (!scroller.isFinished()) post(this);
                else {
                    boolean isExpanded;
                    if (isHorizontal()) isExpanded = scroller.getCurrX() == 0;
                    else isExpanded = scroller.getCurrY() == 0;
                    if (isExpanded != expanded && onExpandStateChangeListener != null) onExpandStateChangeListener.onExpandStateChanged(SlideMenuLayout.this, isExpanded);
                    expanded = isExpanded;
                    autoScroll = false;
                }
            }
        }.run();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isEnabled() && dispatchTouch(ev, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scroll && !isEventInsideChild(ev.getX(), ev.getY())) return false;
        if (isEnabled()) dispatchTouch(ev, false);
        return true;
    }

    private float clamp(float val, float a, float b) {
        float max = Math.max(a, b);
        float min = Math.min(a, b);
        if (val < min) return min;
        if (val > max) return max;
        return val;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        onScreen = true;
        int scroll = isHorizontal() ? scroller.getCurrX() : scroller.getCurrY();
        scroller.computeScrollOffset();
        int updatedScroll = isHorizontal() ? scroller.getCurrX() : scroller.getCurrY();
        if (onSlideChangeListener != null && scroll != updatedScroll) onSlideChangeListener.onScrollSizeChangeListener(SlideMenuLayout.this, updatedScroll);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.isMovable || lp.isMenu) {
                float dx = scroller.getCurrX();
                float dy = scroller.getCurrY();
                if (isHorizontal()) dx = clamp(dx, 0, slideSize);
                else dy = clamp(dy, 0, slideSize);
                child.setTranslationX(dx);
                child.setTranslationY(dy);
            }
        }
        super.dispatchDraw(canvas);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getSlideFrom() {
        return slideFrom;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public int getMinSlide() {
        return Math.min(0, slideSize);
    }

    public int getMaxSlide() {
        return Math.max(0, slideSize);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false, true, false);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void setOnExpandStateChangeListener(OnExpandStateChangeListener onExpandStateChangeListener) {
        this.onExpandStateChangeListener = onExpandStateChangeListener;
    }

    public void setOnSlideChangeListener(OnSlideChangeListener onSlideChangeListener) {
        this.onSlideChangeListener = onSlideChangeListener;
    }

    public interface OnExpandStateChangeListener {
        void onExpandStateChanged(SlideMenuLayout view, boolean expanded);
    }

    public interface OnSlideChangeListener {
        void onScrollSizeChangeListener(SlideMenuLayout view, int slide);
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {
        protected boolean isMenu;
        protected boolean isMovable;
        protected boolean isTouchable;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.SlideMenuLayout_Layout, 0, 0);
            isMenu = a.getBoolean(R.styleable.SlideMenuLayout_Layout_isMenu, false);
            isMovable = a.getBoolean(R.styleable.SlideMenuLayout_Layout_isMovable, true);
            isTouchable = a.getBoolean(R.styleable.SlideMenuLayout_Layout_isTouchable, true);
            a.recycle();
        }

        public LayoutParams(int w, int h, boolean isMenu, boolean isMovable, boolean isTouchable) {
            super(w, h);
            this.isMenu = isMenu;
            this.isMovable = isMovable;
            this.isTouchable = isTouchable;
        }
    }
}