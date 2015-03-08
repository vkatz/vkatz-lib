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
    private OnExpandStateChangedListener onExpandStateChangedListener;

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
        scroller = new Scroller(context);
        scroll = false;
        autoScroll = false;
        onScreen = false;
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View slideView = null;
        for (int i = 0; i < getChildCount(); i++)
            if (((LayoutParams) getChildAt(i).getLayoutParams()).isSlidable) {
                slideView = getChildAt(i);
                break;
            }
        if (slideView == null) slideSize = 0;
        else {
            if (slideFrom == BOTTOM) slideSize = getMeasuredHeight() - slideView.getTop();
            else if (slideFrom == TOP) slideSize = -slideView.getBottom();
            else if (slideFrom == RIGHT) slideSize = getMeasuredWidth() - slideView.getLeft();
            else if (slideFrom == LEFT) slideSize = -slideView.getRight();
            else slideSize = 0;
            if (!onScreen) {
                scroller.startScroll(0, 0, isHorizontal() ? slideSize : 0, isHorizontal() ? 0 : slideSize, 0);
                invalidate();
            }
        }
    }

    private boolean isHorizontal() {
        return slideFrom == RIGHT || slideFrom == LEFT;
    }

    private boolean isEventInsideChild(float x, float y) {
        int cx = (int) (x - scroller.getCurrX());
        int cy = (int) (y - scroller.getCurrY());
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (!lp.isStatic && cx >= child.getLeft() && cy >= child.getTop() && cx <= child.getRight() && cy <= child.getBottom()) return true;
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
                    if (isExpanded != expanded && onExpandStateChangedListener != null) onExpandStateChangedListener.onExpandStateChanged(SlideMenuLayout.this, isExpanded);
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

    @Override
    protected void dispatchDraw(Canvas canvas) {
        onScreen = true;
        scroller.computeScrollOffset();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (!lp.isStatic) {
                child.setTranslationX(scroller.getCurrX());
                child.setTranslationY(scroller.getCurrY());
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

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false, false);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void setOnExpandStateChangedListener(OnExpandStateChangedListener onExpandStateChangedListener) {
        this.onExpandStateChangedListener = onExpandStateChangedListener;
    }

    public static interface OnExpandStateChangedListener {
        public void onExpandStateChanged(SlideMenuLayout view, boolean expanded);
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {
        protected boolean isSlidable;
        protected boolean isStatic;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.SlideMenuLayout_Layout, 0, 0);
            isSlidable = a.getBoolean(R.styleable.SlideMenuLayout_Layout_layout_slidable, false);
            isStatic = a.getBoolean(R.styleable.SlideMenuLayout_Layout_layout_static, false);
            a.recycle();
        }

        public LayoutParams(int width, int height, boolean isSlidable, boolean isStatic) {
            super(width, height);
            this.isSlidable = isSlidable;
            this.isStatic = isStatic;
        }
    }
}