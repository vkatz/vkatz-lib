package by.vkatz.samples.activity

import android.view.LayoutInflater
import android.view.View

/**
 * Created by L-TECH on 02.10.2017.
 */
class AppViewScreen : AppScreen() {

    companion object {
        fun create(id: Int) = AppViewScreen().apply { viewRId = id }
    }

    private var viewRId: Int = 0

    override fun createView(): View = LayoutInflater.from(activity).inflate(viewRId, null, false)
}