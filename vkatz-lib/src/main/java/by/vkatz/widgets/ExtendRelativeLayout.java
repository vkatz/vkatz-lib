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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = resolveSize(0, widthMeasureSpec);
        int h = resolveSize(0, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.w != null) lp.width = (int) ((lp.w.ofWidth ? w : h) * lp.w.value / 100);
            if (lp.h != null) lp.height = (int) ((lp.h.ofWidth ? w : h) * lp.h.value / 100);
            if (lp.l != null) lp.leftMargin = (int) ((lp.l.ofWidth ? w : h) * lp.l.value / 100);
            if (lp.r != null) lp.rightMargin = (int) ((lp.r.ofWidth ? w : h) * lp.r.value / 100);
            if (lp.t != null) lp.topMargin = (int) ((lp.t.ofWidth ? w : h) * lp.t.value / 100);
            if (lp.b != null) lp.bottomMargin = (int) ((lp.b.ofWidth ? w : h) * lp.b.value / 100);
            int wSpec = getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), child.getLayoutParams().width);
            int hSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), child.getLayoutParams().height);
            if (lp.width >= 0) wSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            if (lp.height >= 0) hSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            child.measure(wSpec, hSpec);
        }
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int[] rules = lp.getRules();
            int w = child.getMeasuredWidth();
            int h = child.getMeasuredHeight();
            int ml = lp.leftMargin;
            int mr = lp.rightMargin;
            int mt = lp.topMargin;
            int mb = lp.bottomMargin;
            int cl = getPaddingLeft();
            int ct = getPaddingTop();
            int cr = getMeasuredWidth() - getPaddingRight();
            int cb = getMeasuredHeight() - getPaddingBottom();
            boolean directionFromLeft = true;
            boolean directionFromTop = true;
            if (rules[RelativeLayout.ALIGN_PARENT_LEFT] == TRUE) directionFromLeft = true;
            if (rules[RelativeLayout.ALIGN_PARENT_RIGHT] == TRUE) directionFromLeft = false;
            if (rules[RelativeLayout.ALIGN_PARENT_TOP] == TRUE) directionFromTop = true;
            if (rules[RelativeLayout.ALIGN_PARENT_BOTTOM] == TRUE) directionFromTop = false;
            if (rules[RelativeLayout.CENTER_HORIZONTAL] == TRUE) {
                directionFromLeft = true;
                cl = getMeasuredWidth() / 2 - w / 2;
            }
            if (rules[RelativeLayout.CENTER_VERTICAL] == TRUE) {
                directionFromTop = true;
                ct = getMeasuredHeight() / 2 - h / 2;
            }
            if (rules[RelativeLayout.CENTER_IN_PARENT] == TRUE) {
                directionFromLeft = true;
                cl = getMeasuredWidth() / 2 - w / 2;
                directionFromTop = true;
                ct = getMeasuredHeight() / 2 - h / 2;
            }
            View anchor1, anchor2;
            //anchor1 dependencies
            anchor1 = getChildById(rules[RelativeLayout.LEFT_OF]);
            if (anchor1 != null) {
                directionFromLeft = false;
                cr = anchor1.getLeft();
            }
            anchor1 = getChildById(rules[RelativeLayout.RIGHT_OF]);
            if (anchor1 != null) {
                directionFromLeft = true;
                cl = anchor1.getRight();
            }
            anchor1 = getChildById(rules[RelativeLayout.BELOW]);
            if (anchor1 != null) {
                directionFromTop = true;
                ct = anchor1.getBottom();
            }
            anchor1 = getChildById(rules[RelativeLayout.ABOVE]);
            if (anchor1 != null) {
                directionFromTop = false;
                cb = anchor1.getTop();
            }
            anchor1 = getChildById(rules[RelativeLayout.ALIGN_LEFT]);
            if (anchor1 != null) {
                directionFromLeft = true;
                cl = anchor1.getLeft();
            }
            anchor1 = getChildById(rules[RelativeLayout.ALIGN_RIGHT]);
            if (anchor1 != null) {
                directionFromLeft = false;
                cr = anchor1.getRight();
            }
            anchor1 = getChildById(rules[RelativeLayout.ALIGN_TOP]);
            if (anchor1 != null) {
                directionFromTop = true;
                ct = anchor1.getTop();
            }
            anchor1 = getChildById(rules[RelativeLayout.ALIGN_BOTTOM]);
            if (anchor1 != null) {
                directionFromTop = false;
                cb = anchor1.getBottom();
            }
            anchor1 = getChildById(rules[RelativeLayout.LEFT_OF]);
            anchor2 = getChildById(rules[RelativeLayout.RIGHT_OF]);
            if (anchor1 != null && anchor2 != null) {
                directionFromLeft = true;
                cl = (anchor1.getLeft() + anchor2.getRight() - w) / 2;
            }
            anchor1 = getChildById(rules[RelativeLayout.ALIGN_LEFT]);
            anchor2 = getChildById(rules[RelativeLayout.ALIGN_RIGHT]);
            if (anchor1 != null && anchor2 != null) {
                directionFromLeft = true;
                cl = (anchor1.getLeft() + anchor2.getRight() - w) / 2;
            }
            anchor1 = getChildById(rules[RelativeLayout.BELOW]);
            anchor2 = getChildById(rules[RelativeLayout.ABOVE]);
            if (anchor1 != null && anchor2 != null) {
                directionFromTop = true;
                ct = (anchor1.getBottom() + anchor2.getTop() - h) / 2;
            }
            anchor1 = getChildById(rules[RelativeLayout.ALIGN_BOTTOM]);
            anchor2 = getChildById(rules[RelativeLayout.ALIGN_TOP]);
            if (anchor1 != null && anchor2 != null) {
                directionFromTop = true;
                ct = (anchor1.getBottom() + anchor2.getTop() - h) / 2;
            }
            int ll = directionFromLeft ? (cl + ml) : (cr - w - mr);
            int lt = directionFromTop ? (ct + mt) : (cb - h - mb);
            child.layout(ll, lt, ll + w, lt + h);
        }
    }

    public View getChildById(int id) {
        if (id == 0) return null;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getId() == id) return child;
        }
        return null;
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {
        private Data w, h, l, t, r, b;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ExtendRelativeLayout_Layout, 0, 0);
            w = extract(a.getString(R.styleable.ExtendRelativeLayout_Layout_extend_width), true);
            h = extract(a.getString(R.styleable.ExtendRelativeLayout_Layout_extend_height), false);
            l = extract(a.getString(R.styleable.ExtendRelativeLayout_Layout_extend_left), true);
            r = extract(a.getString(R.styleable.ExtendRelativeLayout_Layout_extend_right), true);
            t = extract(a.getString(R.styleable.ExtendRelativeLayout_Layout_extend_top), false);
            b = extract(a.getString(R.styleable.ExtendRelativeLayout_Layout_extend_bottom), false);
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
