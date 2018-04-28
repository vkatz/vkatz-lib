package by.vkatz.katzilla.helpers

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import by.vkatz.katzilla.FragmentBackStack

/**
 * Created by V on 24.04.2018.
 */
abstract class KatzillaActivity : AppCompatActivity() {
    companion object {
        private val backStackMap = HashMap<String, FragmentBackStack>()

        fun getBackStackFor(who: Activity): FragmentBackStack {
            val key = who::class.java.canonicalName
            return if (backStackMap.containsKey(key)) {
                backStackMap[key]!!
            } else {
                val newBackStack = FragmentBackStack()
                backStackMap[key] = newBackStack
                newBackStack
            }
        }
    }

    private lateinit var backStack: FragmentBackStack

    abstract fun initContent(backStack: FragmentBackStack)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backStack = getBackStackFor(this)
        initContent(backStack)
    }

    override fun onDestroy() {
        super.onDestroy()
        backStack.onActivityDestroyed()
    }

    override fun onBackPressed() {
        when {
            (backStack.currentScreen as? KatzillaFragment<*>)?.onBackPressed() == true -> {
                /*screen handle back action, do nothing*/
            }
            backStack.isBackPossible() -> backStack.back()
            else -> super.onBackPressed()
        }
    }
}