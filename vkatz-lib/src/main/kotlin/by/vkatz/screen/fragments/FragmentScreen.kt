package by.vkatz.screen.fragments

import android.animation.Animator
import android.animation.AnimatorInflater
import android.app.Fragment
import android.app.FragmentTransaction
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.vkatz.screen.BackStack
import by.vkatz.screen.Screen

abstract class FragmentScreen : Fragment(), Screen<FragmentScreen> {
    private var forward = true
    private var animators: List<Int>? = null

    override var parent: BackStack<FragmentScreen>? = null
    override var name: String? = null
    override var storeInBackStack = true
    override var active = false

    var holdView = true

    private var root: View? = null
    var transactionConfig: ((FragmentTransaction) -> Unit)? = null

    override fun onOpen(navigation: Screen.Navigation) {
        forward = navigation == Screen.Navigation.forward
        active = true
    }

    override fun onClose(navigation: Screen.Navigation) {
        forward = navigation == Screen.Navigation.forward
        active = false
    }

    override fun onRelease() {
        active = false
    }

    fun inflate(resId: Int): View = LayoutInflater.from(activity).inflate(resId, null, false)

    abstract fun createView(): View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (holdView) {
            if (root == null) root = createView()
            root
        } else createView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (root != null && root!!.parent != null) (root!!.parent as ViewGroup).removeView(root)
    }

    open fun onBackPressed(): Boolean = false

    open fun getTransactionAnimator(isForward: Boolean, isEntering: Boolean): Int? = null

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        val animator = getTransactionAnimator(forward, enter)
        return if (animator != null) AnimatorInflater.loadAnimator(activity, animator) else super.onCreateAnimator(transit, enter, nextAnim)
    }
}
