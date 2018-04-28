package by.vkatz.katzext.utils

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
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

    fun setComplexBackground(view: View, layer1: Drawable?, layer2: Drawable?) {
        var bg: Drawable? = null
        if (layer1 != null && layer2 != null)
            bg = LayerDrawable(arrayOf(layer1, layer2))
        else if (layer1 != null)
            bg = layer1
        else if (layer2 != null) bg = layer2
        if (bg != null) {
            if (Build.VERSION.SDK_INT <= 21) {
                //android 4.* has bug, on setBg it reset padding's, here is fix
                val pl = view.paddingLeft
                val pt = view.paddingTop
                val pr = view.paddingRight
                val pb = view.paddingBottom
                view.background = bg
                view.setPadding(pl, pt, pr, pb)
            } else view.background = bg
        }
        view.postInvalidate()
    }

    interface TextInterface {
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
