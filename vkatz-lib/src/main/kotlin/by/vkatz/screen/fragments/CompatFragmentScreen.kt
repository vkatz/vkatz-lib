package by.vkatz.screen.fragments

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Forward activity onRequestPermissionsResult
 */
abstract class CompatFragmentScreen : FragmentScreen(), LifecycleOwner {
    private lateinit var lifecycle: LifecycleRegistry
    private val permissionRequestHandlers = HashMap<Int, Continuation<Boolean>>()
    private val activityResultHandlers = HashMap<Int, (Int, Intent?) -> Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle = LifecycleRegistry(this)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    fun startActivityForResult(intent: Intent?, requestCode: Int, handler: (resultCode: Int, data: Intent?) -> Unit) {
        activityResultHandlers.put(requestCode, handler)
        super.startActivityForResult(intent, requestCode)
    }

    fun isPermissionNeedExplanation(permission: String): Boolean =
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            else false

    suspend fun requestPermission(permission: String, code: Int) = suspendCoroutine<Boolean> { result ->
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionRequestHandlers.put(code, result)
            ActivityCompat.requestPermissions(activity, arrayOf(permission), code)
        } else result.resume(true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionRequestHandlers[requestCode]?.let {
            it.resume(grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })
            permissionRequestHandlers.remove(requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultHandlers[requestCode]?.let {
            it.invoke(resultCode, data)
            activityResultHandlers.remove(requestCode)
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onStop() {
        super.onStop()
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onPause() {
        super.onPause()
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onResume() {
        super.onResume()
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun getLifecycle(): Lifecycle = lifecycle
}