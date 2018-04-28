package by.vkatz.katzext.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.RelativeLayout
import by.vkatz.katzext.R
import by.vkatz.katzext.utils.LibExtension

open class ExtendRelativeLayout : RelativeLayout, LibExtension.ImageInterface {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtendRelativeLayout, defStyle, 0)
        setComplexBackground(a.getDrawable(R.styleable.ExtendRelativeLayout_extendBackground1), a.getDrawable(R.styleable.ExtendRelativeLayout_extendBackground2))
        isEnabled = a.getBoolean(R.styleable.ExtendRelativeLayout_extendEnabled, isEnabled)
        isActivated = a.getBoolean(R.styleable.ExtendRelativeLayout_extendActivated, isActivated)
        a.recycle()
    }

    final override fun setComplexBackground(layer1: Drawable?, layer2: Drawable?) {
        LibExtension.setComplexBackground(this, layer1, layer2)
    }
}
