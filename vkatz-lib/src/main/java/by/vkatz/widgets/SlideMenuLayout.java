package by.vkatz.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

import by.vkatz.R;

/**
 * Created by vKatz on 18.02.2015.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SlideMenuLayout extends ExtendRelativeLayout {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int TOP = 3;
    public static final int BOTTOM = 4;

    public static final int FLAG_NEVER_FINISH = 0;
    public static final int FLAG_ALWAYS_FINISH = 1;

    private int slideFrom;
    private boolean isExpanded;
    private boolean isMenuEnabled;
    private boolean scroll;
    private boolean autoScroll;
    private float pos;
    private int dPos;
    private int flags;
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
        isExpanded = a.getBoolean(R.styleable.SlideMenuLayout_isExpanded, false);
        isMenuEnabled = a.getBoolean(R.styleable.SlideMenuLayout_isMenuEnabled, true);
        startScrollDistance = a.getDimensionPixelSize(R.styleable.SlideMenuLayout_startScrollDistance, 25);
        flags = a.getInt(R.styleable.SlideMenuLayout_scrollBehavior, FLAG_ALWAYS_FINISH);
        a.recycle();
        scroller = new Scroller(context);
        scroll = false;
        autoScroll = false;
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View menu = null;
        boolean hasMenu = false;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.isMenu) {
                menu = getChildAt(i);
                if (hasMenu) throw new RuntimeException("Should not contain more than 1 menu");
                hasMenu = true;
            }
        }
        int oldSlideSize = slideSize;
        if (menu == null) slideSize = 0;
        else {
            ExtendRelativeLayout.LayoutParams lp = (ExtendRelativeLayout.LayoutParams) menu.getLayoutParams();
            if (slideFrom == BOTTOM) slideSize = getMeasuredHeight() - lp.mTop;
            else if (slideFrom == TOP) slideSize = -lp.mBottom;
            else if (slideFrom == RIGHT) slideSize = getMeasuredWidth() - lp.mLeft;
            else if (slideFrom == LEFT) slideSize = -lp.mRight;
            else slideSize = 0;
            if (oldSlideSize != slideSize) {
                if (isExpanded) expand(false);
                else collapse(false);
            }
        }
        int dx = 0;
        int dy = 0;
        if (isHorizontal()) dx = (int) clamp(scroller.getCurrX(), 0, slideSize);
        else dy = (int) clamp(scroller.getCurrY(), 0, slideSize);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.isMovable || lp.isMenu) {
                lp.dx = dx;
                lp.dy = dy;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean isHorizontal() {
        return slideFrom == RIGHT || slideFrom == LEFT;
    }

    private boolean isEventInsideChild(float x, float y) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (!lp.isTouchable || child.getVisibility() == GONE) continue;
            int cx = (int) (x - child.getTranslationX());
            int cy = (int) (y - child.getTranslationY());
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
                scrollMenuBy(dPos);
                pos = curPos;
                update();
                return true;
            } else {
                if (Math.abs(pos - curPos) > startScrollDistance) {
                    pos = curPos;
                    scroll = true;
                    return true;
                } else return false;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP)
            if (scroll) finisMenuScroll();
        return false;
    }

    public int scrollMenuBy(int amount) {
        if (amount == 0) return 0;
        int scroll = amount;
        if (isHorizontal()) {
            float fPos = scroller.getCurrX() + amount;
            if (Math.abs(fPos) > Math.abs(slideSize)) scroll = slideSize - scroller.getCurrX();
            if (fPos * (slideFrom == LEFT ? -1 : 1) < 0) scroll = -scroller.getCurrX();
            scroller.startScroll(scroller.getCurrX(), 0, scroll, 0, 0);
        } else {
            float fPos = scroller.getCurrY() + amount;
            if (Math.abs(fPos) > Math.abs(slideSize)) scroll = slideSize - scroller.getCurrY();
            if (fPos * (slideFrom == TOP ? -1 : 1) < 0) scroll = -scroller.getCurrY();
            scroller.startScroll(0, scroller.getCurrY(), 0, scroll, 0);
        }
        dPos = amount;
        update();
        return scroll;
    }

    public void flingMenuBy(int velocity) {
        if (isHorizontal()) scroller.fling(scroller.getCurrX(), scroller.getCurrY(), velocity, 0, Math.min(0, slideSize), Math.max(0, slideSize), 0, 0);
        else scroller.fling(scroller.getCurrX(), scroller.getCurrY(), 0, velocity, 0, 0, Math.min(0, slideSize), Math.max(0, slideSize));
        update();
    }

    public void finisMenuScroll() {
        if (hasFlag(flags, FLAG_ALWAYS_FINISH)) { //finish scroll
            if (getCurrentSlide() == getMinSlide() || getCurrentSlide() == getMaxSlide()) return;
            if (isHorizontal()) {
                if ((dPos > 0 && slideFrom == LEFT) || (dPos < 0 && slideFrom == RIGHT)) expand();
                else collapse();
            } else {
                if ((dPos > 0 && slideFrom == TOP) || (dPos < 0 && slideFrom == BOTTOM)) expand();
                else collapse();
            }
        } else { //do velocity scroll
            int velocity = dPos * 50;
            flingMenuBy(dPos * 50);
        }
    }

    public boolean hasFlag(int what, int flag) {
        return (what & flag) == flag;
    }

    public void expand() {
        expand(true);
    }

    public void expand(boolean anim) {
        scroller.startScroll(scroller.getCurrX(), scroller.getCurrY(), -scroller.getCurrX(), -scroller.getCurrY(), anim ? 250 : 0);
        autoScroll = true;
        update();
    }

    public void collapse() {
        collapse(true);
    }

    public void collapse(boolean anim) {
        scroller.startScroll(scroller.getCurrX(), scroller.getCurrY(), (isHorizontal() ? slideSize : 0) - scroller.getCurrX(), (isHorizontal() ? 0 : slideSize) - scroller.getCurrY(), anim ? 250 : 0);
        autoScroll = true;
        update();
    }

    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean anim) {
        if (isExpanded()) collapse(anim);
        else expand(anim);
    }

    private void update() {
        new Runnable() {
            @Override
            public void run() {
                int scroll = isHorizontal() ? scroller.getCurrX() : scroller.getCurrY();
                scroller.computeScrollOffset();
                int updatedScroll = isHorizontal() ? scroller.getCurrX() : scroller.getCurrY();
                if (onSlideChangeListener != null && scroll != updatedScroll) {
                    float value = 1 - Math.abs(1f * updatedScroll / (getMaxSlide() - getMinSlide()));
                    onSlideChangeListener.onScrollSizeChangeListener(SlideMenuLayout.this, value);
                }
                requestLayout();
                if (!scroller.isFinished()) post(this);
                else {
                    boolean isExpanded;
                    if (isHorizontal()) isExpanded = scroller.getCurrX() == 0;
                    else isExpanded = scroller.getCurrY() == 0;
                    if (isExpanded != SlideMenuLayout.this.isExpanded && onExpandStateChangeListener != null) onExpandStateChangeListener.onExpandStateChanged(SlideMenuLayout.this, isExpanded);
                    SlideMenuLayout.this.isExpanded = isExpanded;
                    autoScroll = false;
                }
            }
        }.run();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isMenuEnabled() && dispatchTouch(ev, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isMenuEnabled()) dispatchTouch(ev, false);
        return true;
    }

    public boolean isMenuEnabled() {
        return isMenuEnabled;
    }

    public void setMenuEnabled(boolean enabled) {
        this.isMenuEnabled = enabled;
    }

    public int getSlideFrom() {
        return slideFrom;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public int getMinSlide() {
        return Math.min(0, slideSize);
    }

    public int getMaxSlide() {
        return Math.max(0, slideSize);
    }

    public int getCurrentSlide() {
        return isHorizontal() ? scroller.getCurrX() : scroller.getCurrY();
    }

    private float clamp(float val, float a, float b) {
        float max = Math.max(a, b);
        float min = Math.min(a, b);
        if (val < min) return min;
        if (val > max) return max;
        return val;
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
        void onScrollSizeChangeListener(SlideMenuLayout view, float value);
    }

    public static class LayoutParams extends ExtendRelativeLayout.LayoutParams {
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