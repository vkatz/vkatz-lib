package by.vkatz.screen.fragments

import android.app.Fragment
import android.app.FragmentTransaction
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.vkatz.screen.BackStack
import by.vkatz.screen.Screen

abstract class FragmentScreen : Fragment(), Screen<FragmentScreen> {

    override var parent: BackStack<FragmentScreen>? = null
    override var name: String? = null
    override var storeInBackStack = true
    override var active = false

    var holdView = true

    private var root: View? = null
    var transactionConfig: ((FragmentTransaction) -> Unit)? = null

    override fun onOpen(navigation: Screen.Navigation) {
        active = true
    }

    override fun onClose(navigation: Screen.Navigation) {
        active = false
    }

    override fun onRelease() {
        active = false
    }

    fun inflate(resId: Int): View {
        return LayoutInflater.from(activity).inflate(resId, null, false)
    }

    abstract fun createView(): View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (holdView) {
            if (root == null) root = createView()
            return root
        } else return createView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (root != null && root!!.parent != null) (root!!.parent as ViewGroup).removeView(root)
    }

    fun onBackPressed(): Boolean {
        return false
    }
}
