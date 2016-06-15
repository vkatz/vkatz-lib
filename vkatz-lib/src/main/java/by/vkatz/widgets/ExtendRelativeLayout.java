package by.vkatz.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import by.vkatz.R;

/**
 * Created by vKatz on 02.12.2015.
 */
public class ExtendRelativeLayout extends RelativeLayout {
    public ExtendRelativeLayout(Context context) {
        super(context);
    }

    public ExtendRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int myWidth = -1;
        int myHeight = -1;
        int width = 0;
        int height = 0;
        if (widthMode != MeasureSpec.UNSPECIFIED) myWidth = widthSize;
        if (heightMode != MeasureSpec.UNSPECIFIED) myHeight = heightSize;
        if (widthMode == MeasureSpec.EXACTLY) width = myWidth;
        if (heightMode == MeasureSpec.EXACTLY) height = myHeight;
        //start measure
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            //apply extends
            lp.width = resolveExtendSize(lp.width, lp.extendWidth, width, height);
            lp.height = resolveExtendSize(lp.height, lp.extendHeight, width, height);
            lp.leftMargin = resolveExtendSize(lp.leftMargin, lp.extendLeft, width, height);
            lp.rightMargin = resolveExtendSize(lp.rightMargin, lp.extendRight, width, height);
            lp.topMargin = resolveExtendSize(lp.topMargin, lp.extendTop, width, myHeight);
            lp.bottomMargin = resolveExtendSize(lp.bottomMargin, lp.extendBottom, width, height);
            //measure child and fill lp measured params
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int mWidthSpec = measureHorizontal(child, width);
            int mHeightSpec = measureVertical(child, height);
            child.measure(mWidthSpec, mHeightSpec);
        }
        if (widthMode != MeasureSpec.EXACTLY) {
            int measured = 0;
            for (int i = 0; i < childCount; i++)
                measured = Math.max(measured, ((LayoutParams) getChildAt(i).getLayoutParams()).mRight);
            width = measured + getPaddingRight();
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            int measured = 0;
            for (int i = 0; i < childCount; i++)
                measured = Math.max(measured, ((LayoutParams) getChildAt(i).getLayoutParams()).mBottom);
            height = measured + getPaddingBottom();
        }
        setMeasuredDimension(width, height);
    }

    private int measureHorizontal(View child, int parentWidth) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int width = lp.width;
        int[] rules = lp.getRules();
        int measuredWidth = child.getMeasuredWidth();
        int left = getPaddingLeft();
        int right = parentWidth - getPaddingRight();
        boolean directionFromLeft = true;
        //no anchor rules
        if (rules[ALIGN_PARENT_LEFT] == TRUE) directionFromLeft = true;
        if (rules[ALIGN_PARENT_RIGHT] == TRUE) directionFromLeft = false;
        if (rules[CENTER_IN_PARENT] == TRUE) {
            directionFromLeft = true;
            left = parentWidth / 2 - measuredWidth / 2;
        }
        //anchor rules
        View anchorRightOf = getChildById(rules[RIGHT_OF]);
        View anchorLeftOf = getChildById(rules[LEFT_OF]);
        View anchorAlignRight = getChildById(rules[ALIGN_RIGHT]);
        View anchorAlignLeft = getChildById(rules[ALIGN_LEFT]);
        if (anchorAlignLeft != null) {
            directionFromLeft = true;
            LayoutParams clp = (LayoutParams) anchorAlignLeft.getLayoutParams();
            left = clp.mLeft + clp.dx;
        }
        if (anchorRightOf != null) {
            directionFromLeft = true;
            LayoutParams clp = (LayoutParams) anchorRightOf.getLayoutParams();
            left = clp.mRight + clp.dx;
        }
        if (anchorAlignRight != null) {
            directionFromLeft = false;
            LayoutParams clp = (LayoutParams) anchorAlignRight.getLayoutParams();
            right = clp.mRight + clp.dx;
        }
        if (anchorLeftOf != null) {
            directionFromLeft = false;
            LayoutParams clp = (LayoutParams) anchorLeftOf.getLayoutParams();
            right = clp.mLeft + clp.dx;
        }
        left += lp.leftMargin;
        right -= lp.rightMargin;
        int mLeft, mRight;
        if (width > 0) { //ignore any clipping, view will be exact this size;
            if (directionFromLeft) {
                mLeft = left;
                mRight = left + width;
            } else {
                mRight = right;
                mLeft = right - width;
            }
        } else if (width == ViewGroup.LayoutParams.MATCH_PARENT) { //fill between left and right
            mLeft = left;
            mRight = right;
        } else { //fill between left and right and not bigger than measured size
            if (directionFromLeft) {
                mLeft = left;
                mRight = left + Math.min(right - left, measuredWidth);
            } else {
                mRight = right;
                mLeft = right - Math.min(right - left, measuredWidth);
            }
        }
        lp.mLeft = mLeft;
        lp.mRight = mRight;
        return MeasureSpec.makeMeasureSpec(mRight - mLeft, MeasureSpec.EXACTLY);
    }

    private int measureVertical(View child, int parentHeight) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int height = lp.height;
        int[] rules = lp.getRules();
        int measuredHeight = child.getMeasuredHeight();
        int top = getPaddingTop();
        int bottom = parentHeight - getPaddingBottom();
        boolean directionFromTop = true;
        //no anchor rules
        if (rules[ALIGN_PARENT_TOP] == TRUE) directionFromTop = true;
        if (rules[ALIGN_PARENT_BOTTOM] == TRUE) directionFromTop = false;
        if (rules[CENTER_IN_PARENT] == TRUE) {
            directionFromTop = true;
            top = parentHeight / 2 - measuredHeight / 2;
        }
        //anchor rules
        View anchorBelow = getChildById(rules[BELOW]);
        View anchorAbove = getChildById(rules[ABOVE]);
        View anchorAlignTop = getChildById(rules[ALIGN_TOP]);
        View anchorAlignBottom = getChildById(rules[ALIGN_BOTTOM]);
        if (anchorAlignTop != null) {
            directionFromTop = true;
            LayoutParams clp = (LayoutParams) anchorAlignTop.getLayoutParams();
            top = clp.mTop + clp.dy;
        }
        if (anchorBelow != null) {
            directionFromTop = true;
            LayoutParams clp = (LayoutParams) anchorBelow.getLayoutParams();
            top = clp.mBottom + clp.dy;
        }
        if (anchorAlignBottom != null) {
            directionFromTop = false;
            LayoutParams clp = (LayoutParams) anchorAlignBottom.getLayoutParams();
            bottom = clp.mBottom + clp.dy;
        }
        if (anchorAbove != null) {
            directionFromTop = false;
            LayoutParams clp = (LayoutParams) anchorAbove.getLayoutParams();
            bottom = clp.mTop + clp.dy;
        }

        top += lp.topMargin;
        bottom -= lp.bottomMargin;
        int mTop, mBottom;
        if (height > 0) { //ignore any clipping, view will be exact this size;
            if (directionFromTop) {
                mTop = top;
                mBottom = top + height;
            } else {
                mBottom = bottom;
                mTop = bottom - height;
            }
        } else if (height == ViewGroup.LayoutParams.MATCH_PARENT) { //fill between top and bot
            mTop = top;
            mBottom = bottom;
        } else { //fill between top and bot and not bigger than measured size
            if (directionFromTop) {
                mTop = top;
                mBottom = top + Math.min(bottom - top, measuredHeight);
            } else {
                mBottom = bottom;
                mTop = bottom - Math.min(bottom - top, measuredHeight);
            }
        }
        lp.mTop = mTop;
        lp.mBottom = mBottom;
        return MeasureSpec.makeMeasureSpec(mBottom - mTop, MeasureSpec.EXACTLY);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                child.layout(lp.mLeft + lp.dx, lp.mTop + lp.dy, lp.mRight + lp.dx, lp.mBottom + lp.dy);
            }
        }
    }

    private int resolveExtendSize(int original, LayoutParams.Data size, int w, int h) {
        if (size != null) {
            if (size.ofWidth && w >= 0) return (int) (w * size.value / 100);
            if (!size.ofWidth && h >= 0) return (int) (h * size.value / 100);
        }
        return original;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    private View getChildById(int id) {
        if (id == 0) return null;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getId() == id) return child;
        }
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    public static class LayoutParams extends RelativeLayout.LayoutParams {
        protected int dx = 0, dy = 0;
        protected int mLeft = 0, mTop = 0, mRight = 0, mBottom = 0;
        private Data extendWidth, extendHeight, extendLeft, extendTop, extendRight, extendBottom;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ExtendRelativeLayout_Layout, 0, 0);
            extendWidth = extract(a.getString(R.styleable.ExtendRelativeLayout_Layout_extendWidth), true);
            extendHeight = extract(a.getString(R.styleable.ExtendRelativeLayout_Layout_extendHeight), false);
            extendLeft = extract(a.getString(R.styleable.ExtendRelativeLayout_Layout_extendLeft), true);
            extendRight = extract(a.getString(R.styleable.ExtendRelativeLayout_Layout_extendRight), true);
            extendTop = extract(a.getString(R.styleable.ExtendRelativeLayout_Layout_extendTop), false);
            extendBottom = extract(a.getString(R.styleable.ExtendRelativeLayout_Layout_extendBottom), false);
            dx = a.getDimensionPixelOffset(R.styleable.ExtendRelativeLayout_Layout_extendDx, 0);
            dy = a.getDimensionPixelOffset(R.styleable.ExtendRelativeLayout_Layout_extendDy, 0);
            a.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        private Data extract(String data, boolean defaultOfWidth) {
            if (data == null) return null;
            else {
                String parts[] = data.split("%");
                return new Data(Float.valueOf(parts[0]), parts.length > 1 ? parts[1].equals("w") : defaultOfWidth);
            }
        }

        private static class Data {
            public final float value;
            public final boolean ofWidth;

            public Data(float value, boolean ofWidth) {
                this.value = value;
                this.ofWidth = ofWidth;
            }
        }
    }
}
