package by.vkatz.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import by.vkatz.R
import by.vkatz.utils.dp

typealias OnPopupShownListener = (popup: PopupWindow) -> Unit
typealias OnItemSelectedListener = (item: Any, sender: ExtendSpinner) -> Unit

open class ExtendSpinner : ExtendRelativeLayout {

    var adapter: ExtendSpinnerAdapter<*>? = null
    var popupBackground: Drawable? = null
    var popupElevation = 0
    var popupMinHeight = 0
    var popupHorizontalOffset = 0
    var popupVerticalOffset = 0
    var popupAdditionWidth = 0

    private var popupShownListener: OnPopupShownListener? = null
    private var itemSelectedListener: OnItemSelectedListener? = null
    private var spinnerView: View? = null

    var selection = -1
        set(value) {
            field = value
            if (adapter != null) {
                val item = if (value >= 0 && value < adapter!!.count) adapter?.getItem(value) else null
                if (spinnerView != null) removeView(spinnerView)
                spinnerView =
                        if (item != null) {
                            itemSelectedListener?.invoke(item, this)
                            adapter!!.getSpinnerView(value, this)
                        } else adapter!!.getSpinnerNoSelectionView(this) ?: adapter!!.getSpinnerView(0, this)
                if (spinnerView != null) addView(spinnerView)
            }
        }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtendSpinner, defStyle, 0)
        popupBackground = a.getDrawable(R.styleable.ExtendSpinner_extendPopupBackground)
        popupElevation = a.getDimensionPixelSize(R.styleable.ExtendSpinner_extendPopupElevation, 0)
        popupMinHeight = a.getDimensionPixelSize(R.styleable.ExtendSpinner_extendPopupMinHeight, context.dp(200f).toInt())
        popupHorizontalOffset = a.getDimensionPixelSize(R.styleable.ExtendSpinner_extendPopupHorizontalOffset, 0)
        popupVerticalOffset = a.getDimensionPixelSize(R.styleable.ExtendSpinner_extendPopupVerticalOffset, 0)
        popupAdditionWidth = a.getDimensionPixelSize(R.styleable.ExtendSpinner_extendPopupAdditionWidth, 0)
        if (isInEditMode) {
            val listItem = a.getResourceId(R.styleable.ExtendSpinner_toolsItem, -1)
            @Suppress("LeakingThis")
            if (listItem > 0) LayoutInflater.from(context).inflate(listItem, this, true)
        }
        a.recycle()
        setOnClickListener { if (adapter != null && isEnabled) showPopup() }
    }

    fun showPopup() {
        if (adapter == null) return
        val window = PopupWindow(context)
        val content = ListView(context)
        content.divider = null
        content.dividerHeight = 0
        content.adapter = adapter
        if (selection >= 0 && selection < adapter!!.count) content.setSelection(selection)
        content.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            selection = position
            window.dismiss()
        }
        window.contentView = content
        window.isFocusable = true
        window.isOutsideTouchable = true
        window.width = width + popupAdditionWidth
        val location = intArrayOf(0, 0)
        getLocationOnScreen(location)
        val size = Rect()
        rootView.getWindowVisibleDisplayFrame(size)
        val actualHeightDown = size.bottom - location[1] - height
        val actualHeightUp = location[1] - size.top
        content.measure(MeasureSpec.makeMeasureSpec(window.width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size.height(), MeasureSpec.AT_MOST))
        val contentMinHeight = minOf(content.measuredHeight, popupMinHeight)
        window.height = minOf(content.measuredHeight, if (actualHeightDown >= contentMinHeight) actualHeightDown else actualHeightUp)
        window.setBackgroundDrawable(popupBackground ?: ColorDrawable(Color.WHITE))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.elevation = popupElevation.toFloat()
        popupShownListener?.invoke(window)
        window.showAsDropDown(this, popupHorizontalOffset, popupVerticalOffset)

    }

    fun setOnSelectionChangedListener(listener: OnItemSelectedListener?) {
        itemSelectedListener = listener
    }

    fun setPopupShownListener(listener: OnPopupShownListener?) {
        popupShownListener = listener
    }

    abstract class ExtendSpinnerAdapter<T>(var data: List<T>, private val idProvider: (T.() -> Long)?) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: getDropDownView(parent)
            view.layoutParams = AbsListView.LayoutParams(parent.width, -2)
            bindDropDownView(position, getItem(position), view)
            return view
        }

        override fun hasStableIds(): Boolean {
            return idProvider != null
        }

        override fun getItem(position: Int) = data[position]

        override fun getItemId(position: Int) = idProvider?.invoke(getItem(position)) ?: 0

        override fun getCount() = data.size

        abstract fun getSpinnerNoSelectionView(parent: ViewGroup): View?

        abstract fun getSpinnerView(pos: Int, parent: ViewGroup): View

        abstract fun getDropDownView(parent: ViewGroup): View

        abstract fun bindDropDownView(pos: Int, item: T, view: View)
    }
}