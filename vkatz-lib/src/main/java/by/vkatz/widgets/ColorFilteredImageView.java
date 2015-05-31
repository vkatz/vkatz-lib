package by.vkatz.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.widget.ImageView;
import by.vkatz.R;

/**
 * Created by vKatz
 */
@SuppressWarnings("all")
public class ColorFilteredImageView extends ImageView {
    private ColorStateList colorFilter;
    private ColorStateList bgColorFilter;

    public ColorFilteredImageView(Context context) {
        super(context);
    }

    public ColorFilteredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ColorFilteredImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorFilteredImageView, defStyle, 0);
        colorFilter = a.getColorStateList(R.styleable.ColorFilteredImageView_colorFilter);
        bgColorFilter = a.getColorStateList(R.styleable.ColorFilteredImageView_backgroundColorFilter);
        a.recycle();
        refreshDrawableState();
        post(new Runnable() {
            @Override
            public void run() {
                refreshDrawableState();
            }
        });
    }

    @Override
    protected void drawableStateChanged() {
        if (getBackground() != null && bgColorFilter != null) {
            int color = bgColorFilter.getColorForState(getDrawableState(), bgColorFilter.getDefaultColor());
            getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
            invalidate();
        }
        if (getDrawable() != null && colorFilter != null) {
            int color = colorFilter.getColorForState(getDrawableState(), colorFilter.getDefaultColor());
            getDrawable().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
            invalidate();
        }
        super.drawableStateChanged();
    }
}
