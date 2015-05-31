package by.vkatz.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;
import by.vkatz.R;
import by.vkatz.utils.FontsManager;

/**
 * User: Katz
 * Date: 06.02.14
 * Time: 19:02
 */
@SuppressWarnings("all")
public class AssetFontEditText extends EditText {
    public AssetFontEditText(Context context) {
        super(context);
    }

    public AssetFontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AssetFontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AssetFontEditText, defStyle, 0);
        setTypeface(FontsManager.instance().getFont(getContext(), a.getString(R.styleable.AssetFontEditText_font)));
        a.recycle();
        refreshDrawableState();
    }
}
