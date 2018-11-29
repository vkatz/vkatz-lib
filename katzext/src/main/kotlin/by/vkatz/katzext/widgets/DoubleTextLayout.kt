package by.vkatz.katzext.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import by.vkatz.katzext.R
import by.vkatz.katzext.utils.ext.makeGone
import by.vkatz.katzext.utils.ext.makeVisibleOrGone

class DoubleTextLayout : LinearLayout {
    var primaryTextView: AppCompatTextView
        private set
    var secondaryTextView: AppCompatTextView
        private set

    var primaryText: CharSequence?
        get() = primaryTextView.text
        set(value) {
            primaryTextView.text = value
            primaryTextView.makeVisibleOrGone(!value.isNullOrBlank())
        }
    var secondaryText: CharSequence?
        get() = secondaryTextView.text
        set(value) {
            secondaryTextView.text = value
            secondaryTextView.makeVisibleOrGone(!value.isNullOrBlank())
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DoubleTextLayout, defStyleAttr, 0)

        primaryTextView = AppCompatTextView(ContextThemeWrapper(context, a.getResourceId(R.styleable.DoubleTextLayout_extendPrimaryTextStyle, 0)))
        primaryTextView.text = a.getString(R.styleable.DoubleTextLayout_extendPrimaryText)
        a.getColorStateList(R.styleable.DoubleTextLayout_extendPrimaryTextColor)?.let { primaryTextView.setTextColor(it) }
        a.getDimensionPixelSize(R.styleable.DoubleTextLayout_extendPrimaryTextSize, -1).takeIf { it > 0 }
                ?.let { primaryTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.toFloat()) }

        secondaryTextView = AppCompatTextView(ContextThemeWrapper(context, a.getResourceId(R.styleable.DoubleTextLayout_extendSecondaryTextStyle, 0)))
        secondaryTextView.text = a.getString(R.styleable.DoubleTextLayout_extendSecondaryText)
        a.getColorStateList(R.styleable.DoubleTextLayout_extendSecondaryTextColor)?.let { secondaryTextView.setTextColor(it) }
        a.getDimensionPixelSize(R.styleable.DoubleTextLayout_extendSecondaryTextSize, -1).takeIf { it > 0 }
                ?.let { secondaryTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.toFloat()) }

        addView(primaryTextView)
        addView(secondaryTextView)

        if (primaryTextView.text.isBlank()) primaryTextView.makeGone()
        if (secondaryTextView.text.isBlank()) secondaryTextView.makeGone()

        val margin = a.getDimensionPixelSize(R.styleable.DoubleTextLayout_extendTextMargin, 0)
        (secondaryTextView.layoutParams as MarginLayoutParams).apply {
            if (orientation == VERTICAL) topMargin = margin
            else leftMargin = margin
        }

        a.recycle()
    }
}