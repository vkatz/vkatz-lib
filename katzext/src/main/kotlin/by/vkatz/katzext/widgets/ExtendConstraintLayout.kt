package by.vkatz.katzext.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import by.vkatz.katzext.R

/**
 * Allow child views to be measured outside of parent bounds
 */
open class ExtendConstraintLayout : ConstraintLayout {

    private var ignoreVerticalBounds = false
    private var ignoreHorizontalBounds = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtendConstraintLayout, defStyleAttr, 0)
        ignoreHorizontalBounds = a.getBoolean(R.styleable.ExtendConstraintLayout_extendIgnoreHorizontalBounds, false)
        ignoreVerticalBounds = a.getBoolean(R.styleable.ExtendConstraintLayout_extendIgnoreVerticalBounds, false)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wms = if (ignoreHorizontalBounds) MeasureSpec.makeMeasureSpec(Int.MAX_VALUE / 2, MeasureSpec.UNSPECIFIED) else widthMeasureSpec
        val hms = if (ignoreVerticalBounds) MeasureSpec.makeMeasureSpec(Int.MAX_VALUE / 2, MeasureSpec.UNSPECIFIED) else heightMeasureSpec
        super.onMeasure(wms, hms)
    }
}