package by.vkatz.katzext.widgets

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.ContentLoadingProgressBar
import by.vkatz.katzext.R

/**
 * Allow to apply compat tint via xml
 */
class ExtendProgressBar : ContentLoadingProgressBar {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtendProgressBar, 0, 0)
        val extTint = a.getColor(R.styleable.ExtendProgressBar_extendTint, 0)
        if (isIndeterminate && extTint != 0) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                val wrapDrawable = DrawableCompat.wrap(indeterminateDrawable)
                DrawableCompat.setTint(wrapDrawable, extTint)
                indeterminateDrawable = DrawableCompat.unwrap<Drawable>(wrapDrawable)
            } else {
                indeterminateDrawable.setColorFilter(extTint, PorterDuff.Mode.SRC_IN)
            }
        }
        a.recycle()
    }
}