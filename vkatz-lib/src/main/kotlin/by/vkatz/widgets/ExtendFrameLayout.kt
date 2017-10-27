package by.vkatz.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import by.vkatz.R
import by.vkatz.utils.LibExtension

open class ExtendFrameLayout : FrameLayout, LibExtension.ImageInterface {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtendFrameLayout, defStyle, 0)
        setComplexBackground(a.getDrawable(R.styleable.ExtendFrameLayout_extendBackground1), a.getDrawable(R.styleable.ExtendFrameLayout_extendBackground2))
        isEnabled = a.getBoolean(R.styleable.ExtendFrameLayout_extendEnabled, isEnabled)
        isActivated = a.getBoolean(R.styleable.ExtendFrameLayout_extendActivated, isActivated)
        a.recycle()
    }

    override fun setComplexBackground(layer1: Drawable?, layer2: Drawable?) {
        LibExtension.setComplexBackground(this, layer1, layer2)
    }
}
