package by.vkatz.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import by.vkatz.R;
import by.vkatz.utils.TextViewExtension;

/**
 * User: Katz
 * Date: 06.02.14
 * Time: 19:02
 */
public class ExtendTextView extends TextView implements TextViewExtension.Interface {
    private int cpdw, cpdh;

    public ExtendTextView(Context context) {
        super(context);
    }

    public ExtendTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ExtendTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExtendTextView, defStyle, 0);
        setFont(a.getString(R.styleable.ExtendTextView_font));
        setCompoundDrawableSize(
                a.getDimensionPixelSize(R.styleable.ExtendTextView_compoundDrawableWidth, 0),
                a.getDimensionPixelSize(R.styleable.ExtendTextView_compoundDrawableHeight, 0));
        a.recycle();
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        setCompoundDrawableSize(cpdw, cpdh);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        setCompoundDrawableSize(cpdw, cpdh);
    }

    @Override
    public void setFont(String assetFontFile) {
        TextViewExtension.setFont(this, assetFontFile);
    }

    @Override
    public void setCompoundDrawableSize(int width, int height) {
        TextViewExtension.setCompoundDrawableSize(this, cpdw = width, cpdh = height);
    }
}
