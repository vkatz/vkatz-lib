package by.vkatz.samples

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import by.vkatz.adapters.SimpleExtendSpinnerAdapter
import by.vkatz.adapters.SimpleExtendSpinnerArrayAdapter
import by.vkatz.samples.activity.AppScreen
import by.vkatz.utils.asTextView
import by.vkatz.utils.get
import by.vkatz.utils.inflate
import by.vkatz.widgets.ExtendSpinner

/**
 * Created by vKatz on 28.10.2017.
 */
class ExtSpinnerScreen : AppScreen() {
    override fun createView(): View {
        val view = activity.inflate(R.layout.ext_spinner)
        (view[R.id.spinner1] as ExtendSpinner).apply {
            adapter = SimpleExtendSpinnerArrayAdapter(context, R.array.spinner_items, R.layout.spinner_item)
            selection = 0
        }
        (view[R.id.spinner2] as ExtendSpinner).apply {
            adapter = SimpleExtendSpinnerAdapter(resources.getStringArray(R.array.spinner_items).toList(), "Nothing selected", null,
                    { it.inflate(R.layout.spinner_item) },
                    { view, item -> view.asTextView().text = item })
            selection = -1
        }
        (view[R.id.spinner3] as ExtendSpinner).apply {
            adapter = object : ExtendSpinner.ExtendSpinnerAdapter<String>(resources.getStringArray(R.array.spinner_items).toList(), null) {
                override fun getSpinnerNoSelectionView(parent: ViewGroup) = ImageView(context).apply { setImageResource(R.drawable.ic_launcher) }

                override fun getSpinnerView(pos: Int, parent: ViewGroup): View {
                    val v = parent.inflate(R.layout.spinner_item_alter).asTextView()
                    v.text = getItem(pos)
                    return v
                }

                override fun getDropDownView(parent: ViewGroup): View = parent.inflate(R.layout.spinner_item)

                override fun bindDropDownView(pos: Int, item: String, view: View) {
                    view.asTextView().text = item
                }
            }
            selection = -1
        }
        return view
    }
}