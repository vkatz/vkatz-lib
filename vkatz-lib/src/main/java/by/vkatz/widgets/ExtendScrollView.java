package by.vkatz.widgets;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by vKatz on 14.05.2015.
 */
public class ExtendScrollView extends ScrollView {
    OnExtendScrollListener onExtendScrollListener;
    private boolean isScrolling;
    private Handler handler;
    private int lastScroll;
    private boolean inTouch;

    public ExtendScrollView(Context context) {
        super(context);
        init();
    }

    public ExtendScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExtendScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        isScrolling = false;
        inTouch = false;
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                boolean scrolling = (lastScroll == getScrollY() && !inTouch);
                if (isScrolling && scrolling) {
                    if (onExtendScrollListener != null) onExtendScrollListener.onScrollStateChanged(ExtendScrollView.this, false);
                    isScrolling = false;
                }
                lastScroll = getScrollY();
                handler.postDelayed(this, 200);
            }
        });
    }

    public boolean isScrolling() {
        return isScrolling;
    }

    public boolean isInTouch() {
        return inTouch;
    }

    public boolean isInFling() {
        return isScrolling && !inTouch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                inTouch = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                inTouch = false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (!isScrolling && onExtendScrollListener != null) onExtendScrollListener.onScrollStateChanged(ExtendScrollView.this, true);
        isScrolling = true;
        super.onScrollChanged(l, t, oldl, oldt);
        if (onExtendScrollListener != null) onExtendScrollListener.onScrollChanged(this, getScrollY());
    }

    public void setOnExtendScrollListener(OnExtendScrollListener onExtendScrollListener) {
        this.onExtendScrollListener = onExtendScrollListener;
    }

    public interface OnExtendScrollListener {
        void onScrollChanged(ExtendScrollView view, int scroll);

        void onScrollStateChanged(ExtendScrollView view, boolean isScrolling);
    }
}
