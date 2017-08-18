package by.vkatz.utils

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.widget.TextView

object LibExtension {

    fun setCompoundDrawableSize(textView: TextView, width: Int, height: Int) {
        if (width <= 0 && height <= 0) return
        val drawables = textView.compoundDrawables
        val processed = arrayOfNulls<Drawable>(4)
        (0..3).filter { drawables[it] != null }.forEach {
            if (drawables[it] is FixedSizeDrawable) {
                (drawables[it] as FixedSizeDrawable).setSize(width, height)
                processed[it] = drawables[it]
            } else processed[it] = FixedSizeDrawable(drawables[it], width, height)
        }
        textView.setCompoundDrawables(processed[0], processed[1], processed[2], processed[3])
    }

    fun setFont(textView: TextView, assetFontFile: String?) {
        textView.typeface = FontsManager.instance.getFont(textView.context, assetFontFile)
    }

    fun setComplexBackground(view: View, layer1: Drawable?, layer2: Drawable?) {
        var bg: Drawable? = null
        if (layer1 != null && layer2 != null)
            bg = LayerDrawable(arrayOf(layer1, layer2))
        else if (layer1 != null)
            bg = layer1
        else if (layer2 != null) bg = layer2
        if (bg != null) view.background = bg
    }

    interface TextInterface {
        fun setFont(assetFontFile: String?)

        fun setCompoundDrawableSize(width: Int, height: Int)
    }

    interface ImageInterface {
        fun setComplexBackground(layer1: Drawable?, layer2: Drawable?)
    }

    class FixedSizeDrawable(private val child: Drawable, width: Int, height: Int) : LayerDrawable(arrayOf(child)) {
        private var intrinsicWidth: Int = 0
        private var intrinsicHeight: Int = 0

        init {
            setSize(width, height)
        }

        fun setSize(width: Int, height: Int) {
            if (width > 0 && height > 0) {
                intrinsicWidth = width
                intrinsicHeight = height
            } else if (width > 0) {
                intrinsicWidth = width
                intrinsicHeight = (child.intrinsicHeight * (1f * width / child.intrinsicWidth)).toInt()
            } else {
                intrinsicWidth = (child.intrinsicWidth * (1f * height / child.intrinsicHeight)).toInt()
                intrinsicHeight = height
            }
            isFilterBitmap = true
            child.isFilterBitmap = true
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }

        override fun getIntrinsicWidth(): Int = intrinsicWidth

        override fun getIntrinsicHeight(): Int = intrinsicHeight
    }
}
