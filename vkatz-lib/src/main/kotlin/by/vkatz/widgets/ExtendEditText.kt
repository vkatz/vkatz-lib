package by.vkatz.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import by.vkatz.R
import by.vkatz.utils.LibExtension

typealias OnSelectionChangedListener = (sender: ExtendEditText, start: Int, end: Int) -> Unit
typealias AfterTextChangedListener = (sender: ExtendEditText, text: String) -> Unit

open class ExtendEditText : EditText, LibExtension.TextInterface, LibExtension.ImageInterface {

    private var cpdw: Int = 0
    private var cpdh: Int = 0

    private var onSelectionChangedListener: OnSelectionChangedListener? = null
    private var textWatcher: TextWatcher? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtendEditText, defStyle, 0)
        setFont(a.getString(R.styleable.ExtendEditText_extendFont))
        setCompoundDrawableSize(
                a.getDimensionPixelSize(R.styleable.ExtendEditText_compoundDrawableWidth, 0),
                a.getDimensionPixelSize(R.styleable.ExtendEditText_compoundDrawableHeight, 0))
        setComplexBackground(a.getDrawable(R.styleable.ExtendEditText_extendBackground1), a.getDrawable(R.styleable.ExtendEditText_extendBackground2))
        isEnabled = a.getBoolean(R.styleable.ExtendEditText_extendEnabled, isEnabled)
        isActivated = a.getBoolean(R.styleable.ExtendEditText_extendActivated, isActivated)
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

    override fun setFont(assetFontFile: String?) {
        LibExtension.setFont(this, assetFontFile)
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
        if (afterTextChangedListener != null) {
            textWatcher = object : TextWatcher {
                override fun afterTextChanged(editable: Editable) {
                    afterTextChangedListener(this@ExtendEditText, editable.toString())
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            }
            addTextChangedListener(textWatcher)
        }
    }
}
