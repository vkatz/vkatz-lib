package by.vkatz.katzext.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import by.vkatz.katzext.R
import by.vkatz.katzext.utils.LibExtension

open class ExtendCheckBox : AppCompatCheckBox, LibExtension.TextInterface, LibExtension.ImageInterface {
    private var cpdw: Int = 0
    private var cpdh: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtendCheckBox, defStyle, 0)
        setCompoundDrawableSize(
                a.getDimensionPixelSize(R.styleable.ExtendCheckBox_compoundDrawableWidth, 0),
                a.getDimensionPixelSize(R.styleable.ExtendCheckBox_compoundDrawableHeight, 0))
        setComplexBackground(a.getDrawable(R.styleable.ExtendCheckBox_extendBackground1), a.getDrawable(R.styleable.ExtendCheckBox_extendBackground2))
        isEnabled = a.getBoolean(R.styleable.ExtendCheckBox_extendEnabled, isEnabled)
        isActivated = a.getBoolean(R.styleable.ExtendCheckBox_extendActivated, isActivated)
        a.recycle()
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
        setCompoundDrawableSize(cpdw, cpdh)
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
        setCompoundDrawableSize(cpdw, cpdh)
    }

    override fun setCompoundDrawableSize(width: Int, height: Int) {
        cpdw = width
        cpdh = height
        LibExtension.setCompoundDrawableSize(this, width, height)
    }

    override fun setComplexBackground(layer1: Drawable?, layer2: Drawable?) {
        LibExtension.setComplexBackground(this, layer1, layer2)
    }
}
