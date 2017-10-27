package by.vkatz.samples.activity

import android.view.View
import by.vkatz.utils.inflate

/**
 * Created by L-TECH on 02.10.2017.
 */
class AppViewScreen private constructor() : AppScreen() {

    companion object {
        fun create(id: Int) = AppViewScreen().apply { viewRId = id }
    }

    private var viewRId: Int = 0

    override fun createView(): View = activity.inflate(viewRId)
}