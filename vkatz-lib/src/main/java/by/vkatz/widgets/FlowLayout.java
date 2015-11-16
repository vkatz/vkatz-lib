package by.vkatz.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import by.vkatz.R;

/**
 * Created by vKatz on 12.10.2015.
 */
public class FlowLayout extends ViewGroup {
    private static final int GRAVITY_CENTER = 0;
    private static final int GRAVITY_TOP = 1;
    private static final int GRAVITY_BOT = 2;

    private int offsetVertical, offsetHorizontal, lineGravity, fixedLineHeight;
    private ArrayList<Integer> lineSizes;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        lineSizes = new ArrayList<>();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout, defStyle, 0);
        offsetHorizontal = a.getDimensionPixelSize(R.styleable.FlowLayout_offsetHorizontal, 0);
        offsetVertical = a.getDimensionPixelSize(R.styleable.FlowLayout_offsetVertical, 0);
        fixedLineHeight = a.getDimensionPixelSize(R.styleable.FlowLayout_lineHeight, -2);
        lineGravity = a.getInteger(R.styleable.FlowLayout_lineGravity, GRAVITY_CENTER);
        a.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int lineHeight = fixedLineHeight > 0 ? fixedLineHeight : 0;
        int myWidth = r - l;
        int line = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) continue;
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (fixedLineHeight <= 0) lineHeight = Math.max(childHeight, lineHeight);
            if (childWidth + childLeft + getPaddingRight() > myWidth) {
                childLeft = getPaddingLeft();
                childTop += offsetVertical + lineHeight;
                if (fixedLineHeight <= 0) lineHeight = childHeight;
                line++;
            }
            int lineSize = lineSizes.get(line);
            int offset = 0;
            if (lineGravity == GRAVITY_TOP) offset = 0;
            else if (lineGravity == GRAVITY_CENTER) offset = (lineSize - childHeight) / 2;
            else if (lineGravity == GRAVITY_BOT) offset = lineSize - childHeight;
            child.layout(childLeft, offset + childTop, childLeft + childWidth, offset + childTop + childHeight);
            childLeft += childWidth + offsetHorizontal;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int lineHeight = fixedLineHeight > 0 ? fixedLineHeight : 0;
        int myWidth = resolveSize(100, widthMeasureSpec);
        int wantedHeight = 0;
        lineSizes.clear();
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) continue;
            child.measure(
                    getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), child.getLayoutParams().width),
                    getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), child.getLayoutParams().height));
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (fixedLineHeight <= 0) lineHeight = Math.max(childHeight, lineHeight);
            if (childWidth + childLeft + getPaddingRight() > myWidth) {
                childLeft = getPaddingLeft();
                childTop += offsetVertical + lineHeight;
                if (fixedLineHeight <= 0) lineHeight = childHeight;
                lineSizes.add(lineHeight);
            }
            childLeft += childWidth + offsetHorizontal;
        }
        lineSizes.add(lineHeight);
        if (getChildCount() == 0) lineHeight = 0;
        wantedHeight += childTop + lineHeight + getPaddingBottom();
        setMeasuredDimension(myWidth, resolveSize(wantedHeight, heightMeasureSpec));
    }
}
