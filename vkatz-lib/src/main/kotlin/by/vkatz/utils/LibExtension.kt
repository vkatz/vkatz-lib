package by.vkatz.utils

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
            processed[it] = drawables[it]
            val intrinsicWidth: Int
            val intrinsicHeight: Int
            if (width > 0 && height > 0) {
                intrinsicWidth = width
                intrinsicHeight = height
            } else if (width > 0) {
                intrinsicWidth = width
                intrinsicHeight = (drawables[it].intrinsicHeight * (1f * width / drawables[it].intrinsicWidth)).toInt()
            } else {
                intrinsicWidth = (drawables[it].intrinsicWidth * (1f * height / drawables[it].intrinsicHeight)).toInt()
                intrinsicHeight = height
            }
            processed[it]!!.setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
        textView.setCompoundDrawables(processed[0], processed[1], processed[2], processed[3])
    }

    fun setFont(textView: TextView, assetFontFile: String?) {
        textView.typeface = FontsManager.instance.getFont(textView.context, assetFontFile) ?: textView.typeface
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
        fun setFont(assetFontFile: String?)

        fun setCompoundDrawableSize(width: Int, height: Int)
    }

    interface ImageInterface {
        fun setComplexBackground(layer1: Drawable?, layer2: Drawable?)
    }
}
