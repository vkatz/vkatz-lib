package by.vkatz.katzext.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import by.vkatz.katzext.R
import java.util.*

open class FlowLayout : ExtendRelativeLayout {

    private var offsetVertical: Int = 0
    private var offsetHorizontal: Int = 0
    private var lineGravity: Int = 0
    private var fixedLineHeight: Int = 0
    private lateinit var lineSizes: ArrayList<Int>

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        lineSizes = ArrayList()
        val a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout, defStyle, 0)
        offsetHorizontal = a.getDimensionPixelSize(R.styleable.FlowLayout_offsetHorizontal, 0)
        offsetVertical = a.getDimensionPixelSize(R.styleable.FlowLayout_offsetVertical, 0)
        fixedLineHeight = a.getDimensionPixelSize(R.styleable.FlowLayout_lineHeight, -2)
        lineGravity = a.getInteger(R.styleable.FlowLayout_lineGravity, GRAVITY_CENTER)
        a.recycle()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childLeft = paddingLeft
        var childTop = paddingTop
        var lineHeight = if (fixedLineHeight > 0) fixedLineHeight else 0
        val myWidth = r - l
        var line = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            if (fixedLineHeight <= 0) lineHeight = Math.max(childHeight, lineHeight)
            if (childWidth + childLeft + paddingRight > myWidth) {
                childLeft = paddingLeft
                childTop += offsetVertical + lineHeight
                if (fixedLineHeight <= 0) lineHeight = childHeight
                line++
            }
            val lineSize = lineSizes[line]
            var offset = 0
            when (lineGravity) {
                GRAVITY_TOP -> offset = 0
                GRAVITY_CENTER -> offset = (lineSize - childHeight) / 2
                GRAVITY_BOT -> offset = lineSize - childHeight
            }
            child.layout(childLeft, offset + childTop, childLeft + childWidth, offset + childTop + childHeight)
            childLeft += childWidth + offsetHorizontal
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var childLeft = paddingLeft
        var childTop = paddingTop
        var lineHeight = if (fixedLineHeight > 0) fixedLineHeight else 0
        val myWidth = View.resolveSize(100, widthMeasureSpec)
        var wantedHeight = 0
        lineSizes.clear()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue
            child.measure(
                    ViewGroup.getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, child.layoutParams.width),
                    ViewGroup.getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, child.layoutParams.height))
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            if (fixedLineHeight <= 0) lineHeight = Math.max(childHeight, lineHeight)
            if (childWidth + childLeft + paddingRight > myWidth) {
                childLeft = paddingLeft
                childTop += offsetVertical + lineHeight
                if (fixedLineHeight <= 0) lineHeight = childHeight
                lineSizes.add(lineHeight)
            }
            childLeft += childWidth + offsetHorizontal
        }
        lineSizes.add(lineHeight)
        if (childCount == 0) lineHeight = 0
        wantedHeight += childTop + lineHeight + paddingBottom
        setMeasuredDimension(myWidth, View.resolveSize(wantedHeight, heightMeasureSpec))
    }

    companion object {
        private val GRAVITY_CENTER = 0
        private val GRAVITY_TOP = 1
        private val GRAVITY_BOT = 2
    }
}