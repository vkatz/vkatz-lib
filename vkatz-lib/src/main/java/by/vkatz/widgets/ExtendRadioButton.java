package by.vkatz.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import by.vkatz.R;
import by.vkatz.utils.TextViewExtension;

/**
 * User: Katz
 * Date: 06.02.14
 * Time: 19:02
 */
@SuppressWarnings("all")
public class ExtendRadioButton extends RadioButton implements TextViewExtension.Interface {
    private int cpdw, cpdh;

    public ExtendRadioButton(Context context) {
        super(context);
    }

    public ExtendRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ExtendRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExtendRadioButton, defStyle, 0);
        setFont(a.getString(R.styleable.ExtendRadioButton_font));
        setCompoundDrawableSize(
                a.getDimensionPixelSize(R.styleable.ExtendRadioButton_compoundDrawableWidth, 0),
                a.getDimensionPixelSize(R.styleable.ExtendRadioButton_compoundDrawableHeight, 0));
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
