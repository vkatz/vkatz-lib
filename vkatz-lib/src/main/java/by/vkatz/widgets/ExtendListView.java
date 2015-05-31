package by.vkatz.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by vKatz on 13.05.2015.
 */
public class ExtendListView extends ListView {
    private OnScrollListener onScrollListener;
    private OnExtendScrollListener onExtendScrollListener;
    private int activeItem = 0;
    private int activeItemOffset = 0;

    public ExtendListView(Context context) {
        super(context);
        init();
    }

    public ExtendListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExtendListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (onScrollListener != null) onScrollListener.onScrollStateChanged(view, scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (onScrollListener != null) onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                if (onExtendScrollListener != null) {
                    if (firstVisibleItem != activeItem) {
                        if (activeItem - firstVisibleItem < 0) onExtendScrollListener.onScrollDown(ExtendListView.this);
                        else onExtendScrollListener.onScrollUp(ExtendListView.this);
                        activeItem = firstVisibleItem;
                        activeItemOffset = getChildAt(0).getTop();
                    } else {
                        int dx = activeItemOffset - getChildAt(0).getTop();
                        if (Math.abs(dx) > 20) {
                            if (dx > 0) onExtendScrollListener.onScrollDown(ExtendListView.this);
                            else onExtendScrollListener.onScrollUp(ExtendListView.this);
                            activeItemOffset = getChildAt(0).getTop();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        this.onScrollListener = l;
    }

    public void setOnExtedScrollListener(OnExtendScrollListener l) {
        this.onExtendScrollListener = l;
    }

    public long getState() {
        if (getChildCount() > 0) {
            long pos = getFirstVisiblePosition();
            long offset = getChildAt(0).getTop();
            return (pos << 32) | (offset & 0xFFFFFFFFL);
        } else return 0;
    }

    public void setState(long state) {
        if (state != 0) {
            int pos = (int) (state >> 32);
            int offset = (int) (state & 0xFFFFFFFFL);
            smoothScrollToPositionFromTop(pos, offset, 0);
        }
    }

    public interface OnExtendScrollListener {
        void onScrollUp(ExtendListView view);

        void onScrollDown(ExtendListView view);
    }

    public static abstract class SimpleOnExtendScrollListener implements OnExtendScrollListener {
        private int direction = 0;

        @Override
        public final void onScrollDown(ExtendListView view) {
            if (direction != -1) onScrollDownStart(view);
            direction = -1;
        }

        @Override
        public final void onScrollUp(ExtendListView view) {
            if (direction != 1) onScrollUpStart(view);
            direction = 1;
        }

        public abstract void onScrollDownStart(ExtendListView view);

        public abstract void onScrollUpStart(ExtendListView view);
    }
}
