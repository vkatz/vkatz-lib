package by.vkatz.katzext.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.SearchView
import by.vkatz.katzext.utils.AppLiveData

/**
 * SearchView with search query sa LiveData
 */
open class ExtSearchView : SearchView {

    private var queryListener: OnQueryTextListener? = null
    private val pQuery = AppLiveData(QueryData("", false))

    val query get() = pQuery.asImmutable()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        super.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                pQuery.value = QueryData(query, true)
                return queryListener?.onQueryTextSubmit(query) ?: true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                pQuery.value = QueryData(newText, false)
                return queryListener?.onQueryTextChange(newText) ?: true
            }
        })
    }

    override fun setOnQueryTextListener(listener: OnQueryTextListener?) {
        this.queryListener = listener
    }

    data class QueryData(val query: String, val isSubmit: Boolean)
}