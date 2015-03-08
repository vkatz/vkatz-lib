package by.vkatz.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.widget.TextView;
import by.vkatz.R;
import by.vkatz.utils.FontsManager;

/**
 * User: Katz
 * Date: 06.02.14
 * Time: 19:02
 */
@SuppressWarnings("all")
public class AssetFontTextView extends TextView {

    //    private int bgColorFilter = 0;
    private ColorStateList bgColorFilter;
    private PorterDuffColorFilter filter;

    public AssetFontTextView(Context context) {
        super(context);
    }

    public AssetFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AssetFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AssetFontTextView, defStyle, 0);
        setTypeface(FontsManager.instance().getFont(getContext(), a.getString(R.styleable.AssetFontTextView_font)));
        bgColorFilter = a.getColorStateList(R.styleable.AssetFontTextView_backgroundColorFilter);
        a.recycle();
        refreshDrawableState();
    }

    public void setFont(String assetFontFile) {
        setTypeface(FontsManager.instance().getFont(getContext(), assetFontFile));
    }

    public void setBackgroundColorFilterResource(int resourceId) {
        bgColorFilter = getContext().getResources().getColorStateList(resourceId);
        drawableStateChanged();
    }

    @Override
    protected void drawableStateChanged() {
        if (getBackground() != null && bgColorFilter != null) {
            int color = bgColorFilter.getColorForState(getDrawableState(), bgColorFilter.getDefaultColor());
            filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            invalidate();
        }
        super.drawableStateChanged();
    }

    @Override
    public void invalidate() {
        if (getBackground() != null) getBackground().setColorFilter(filter);
        super.invalidate();
    }
}
