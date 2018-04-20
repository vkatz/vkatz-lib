package by.vkatz.katzilla.screen.fragments

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v7.app.AppCompatActivity

/**
 * Created by V on 08.11.2017.
 */
class CompatFragmentBackStack(activity: AppCompatActivity, containerId: Int) : FragmentBackStack(activity, containerId), LifecycleObserver {

    init {
        activity.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun onPause() {
        super.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume() {
        super.onResume()
    }
}