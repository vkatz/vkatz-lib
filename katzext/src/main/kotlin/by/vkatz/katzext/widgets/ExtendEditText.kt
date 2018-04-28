package by.vkatz.katzext.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import by.vkatz.katzext.R
import by.vkatz.katzext.utils.LibExtension

typealias OnSelectionChangedListener = (sender: ExtendEditText, start: Int, end: Int) -> Unit
typealias AfterTextChangedListener = (sender: ExtendEditText, text: Editable) -> Unit

/**
 * Mask format - "000 000-0000" - 0 will be replaced to input char, other chars will appear automatically
 */
open class ExtendEditText : AppCompatEditText, LibExtension.TextInterface, LibExtension.ImageInterface {
    private var cpdw: Int = 0
    private var cpdh: Int = 0

    private var onSelectionChangedListener: OnSelectionChangedListener? = null
    private var textWatcher: TextWatcher? = null
    private var maskTextWatcher: TextWatcher? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtendEditText, defStyle, 0)
        setCompoundDrawableSize(
                a.getDimensionPixelSize(R.styleable.ExtendEditText_compoundDrawableWidth, 0),
                a.getDimensionPixelSize(R.styleable.ExtendEditText_compoundDrawableHeight, 0))
        setComplexBackground(a.getDrawable(R.styleable.ExtendEditText_extendBackground1), a.getDrawable(R.styleable.ExtendEditText_extendBackground2))
        setMask(a.getString(R.styleable.ExtendEditText_extendInputMask))
        isEnabled = a.getBoolean(R.styleable.ExtendEditText_extendEnabled, isEnabled)
        isActivated = a.getBoolean(R.styleable.ExtendEditText_extendActivated, isActivated)
        a.recycle()
    }

    fun setMask(mask: String?) {
        if (maskTextWatcher != null) {
            removeTextChangedListener(maskTextWatcher)
            maskTextWatcher = null
        }
        if (mask == null) return
        val maskDigitsSize = mask.count { it == '0' }
        var inEdit = false
        maskTextWatcher = addAfterTextChangedListener { _, text ->
            if (!inEdit) {
                inEdit = true
                val cutEnd = text.length - mask.length > 1
                var pos = 0
                while (pos != text.length) {
                    if (!text[pos].isDigit()) text.delete(pos, pos + 1)
                    else pos++
                }
                if (text.length > maskDigitsSize) {
                    if (cutEnd) text.delete(0, text.length - maskDigitsSize)
                    else text.delete(maskDigitsSize, text.length)
                }
                pos = 0
                while (pos < mask.length && pos < text.length)
                    if (mask[pos] != '0') text.insert(pos, "${mask[pos++]}") else pos++
                inEdit = false
            }
        }
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

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        onSelectionChangedListener?.invoke(this, selStart, selEnd)
    }

    fun setOnSelectionChangedListener(onSelectionChangedListener: OnSelectionChangedListener?) {
        this.onSelectionChangedListener = onSelectionChangedListener
    }

    fun setAfterTextChangedListener(afterTextChangedListener: AfterTextChangedListener?) {
        if (textWatcher != null) removeTextChangedListener(textWatcher!!)
        if (afterTextChangedListener != null)
            textWatcher = addAfterTextChangedListener(afterTextChangedListener)
    }

    fun addAfterTextChangedListener(afterTextChangedListener: AfterTextChangedListener): TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(editable: Editable) {
                afterTextChangedListener(this@ExtendEditText, editable)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }
        addTextChangedListener(textWatcher)
        return textWatcher
    }
}
