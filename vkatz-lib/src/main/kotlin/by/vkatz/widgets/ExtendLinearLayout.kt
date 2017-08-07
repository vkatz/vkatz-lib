package by.vkatz.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.LinearLayout
import by.vkatz.R
import by.vkatz.utils.LibExtension

open class ExtendLinearLayout : LinearLayout, LibExtension.ImageInterface {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtendLinearLayout, defStyle, 0)
        setComplexBackground(a.getDrawable(R.styleable.ExtendLinearLayout_extendBackground1), a.getDrawable(R.styleable.ExtendLinearLayout_extendBackground2))
        isEnabled = a.getBoolean(R.styleable.ExtendLinearLayout_extendEnabled, isEnabled)
        isActivated = a.getBoolean(R.styleable.ExtendLinearLayout_extendActivated, isActivated)
        a.recycle()
    }

    override final fun setComplexBackground(layer1: Drawable?, layer2: Drawable?) {
        LibExtension.setComplexBackground(this, layer1, layer2)
    }
}
