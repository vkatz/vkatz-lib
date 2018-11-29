package by.vkatz.katzext.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.vkatz.katzext.R
import by.vkatz.katzext.adapters.SimpleRecyclerViewAdapter
import by.vkatz.katzext.adapters.SimpleViewHolder
import by.vkatz.katzext.adapters.SimpleViewHolderProvider
import by.vkatz.katzext.adapters.ViewBinder

typealias OnPopupShownListener = (popup: PopupWindow) -> Unit
typealias OnItemSelectedListener = (item: Any, sender: ExtendSpinner) -> Unit

/**
 * Custom spinner that allow to use custom views for current item / selected item / no item
 */
open class ExtendSpinner : RelativeLayout {

    var popupBackground: Drawable? = null
    var popupElevation = 0
    var popupHorizontalOffset = 0
    var popupVerticalOffset = 0
    var popupAdditionWidth = 0

    private var adapter: SimpleRecyclerViewAdapter<*>? = null
    private var popupWindow: PopupWindow? = null
    private var popupShownListener: OnPopupShownListener? = null
    private var itemSelectedListener: OnItemSelectedListener? = null
    private var noSelectionViewProvider: ((parent: ViewGroup) -> View)? = null
    private var selectedViewProvider: ((parent: ViewGroup, item: Any) -> View)? = null
    private var spinnerView: View? = null

    var selection = -1
        set(value) {
            field = value
            notifySelectionChanged()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtendSpinner, defStyle, 0)
        popupBackground = a.getDrawable(R.styleable.ExtendSpinner_extendPopupBackground)
        popupElevation = a.getDimensionPixelSize(R.styleable.ExtendSpinner_extendPopupElevation, 0)
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

    fun <T> setUpSpinner(data: List<T>,
                         noSelectionViewProvider: ((parent: ViewGroup) -> View)?,
                         selectedViewProvider: ((parent: ViewGroup, item: T) -> View)?,
                         @LayoutRes dropDownLayoutId: Int,
                         dropDownBinder: ViewBinder<T>
                        ) = setUpSpinner(data, noSelectionViewProvider, selectedViewProvider, { parent -> SimpleViewHolder(dropDownLayoutId, parent, dropDownBinder) })

    @Suppress("UNCHECKED_CAST")
    fun <T> setUpSpinner(data: List<T>,
                         noSelectionViewProvider: ((parent: ViewGroup) -> View)?,
                         selectedViewProvider: ((parent: ViewGroup, item: T) -> View)?,
                         dropDownHolderProvider: SimpleViewHolderProvider<T>) {
        this.noSelectionViewProvider = noSelectionViewProvider
        this.selectedViewProvider = (selectedViewProvider as? (parent: ViewGroup, item: Any) -> View) ?: { parent, item ->
            dropDownHolderProvider(parent).apply { bind(item as T) }.itemView
        }
        adapter = SimpleRecyclerViewAdapter(data, null) { parent: ViewGroup ->
            dropDownHolderProvider(parent).apply { itemView.setOnClickListener { dropDownItemPressed(itemData) } }
        }
        notifySelectionChanged()
    }

    fun showPopup() {
        if (adapter == null) return
        val content = RecyclerView(context)
        val window = PopupWindow(content, -2, -2, true)
        window.isOutsideTouchable = true
        window.setBackgroundDrawable(popupBackground ?: ColorDrawable(Color.WHITE))
        window.setOnDismissListener { popupWindow = null }
        window.elevation = popupElevation.toFloat()
        content.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        content.adapter = adapter
        popupWindow = window
        popupShownListener?.invoke(window)
        window.showAsDropDown(this, popupHorizontalOffset, popupVerticalOffset)
    }

    private fun dropDownItemPressed(item: Any?) {
        popupWindow?.dismiss()
        selection = adapter?.data?.indexOf(item) ?: -1
    }

    private fun notifySelectionChanged() {
        if (adapter != null) {
            val item = if (selection >= 0 && selection < adapter!!.itemCount) adapter?.getItemAt(selection) else null
            if (spinnerView != null) removeView(spinnerView)
            spinnerView = if (item != null) {
                itemSelectedListener?.invoke(item, this)
                selectedViewProvider?.invoke(this, item)
            } else noSelectionViewProvider?.invoke(this)
            if (spinnerView != null) addView(spinnerView)
        }
    }

    fun setOnSelectionChangedListener(listener: OnItemSelectedListener?) {
        itemSelectedListener = listener
    }

    fun setPopupShownListener(listener: OnPopupShownListener?) {
        popupShownListener = listener
    }
}