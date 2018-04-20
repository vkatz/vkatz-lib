package by.vkatz.samples.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.Toast

import by.vkatz.samples.R
import by.vkatz.katzext.utils.ActivityNavigator

/**
 * Created by Katz on 17.06.2016.
 */

class ActivityA : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a)
        val data = ActivityNavigator.getData(this)
        Toast.makeText(this, "" + data!!.getString("a")!!, Toast.LENGTH_SHORT).show()
        //animate
        findViewById<View>(R.id.root).addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    window.statusBarColor = Color.TRANSPARENT
                    window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    window.statusBarColor = Color.TRANSPARENT
                    val circularReveal = ViewAnimationUtils.createCircularReveal(findViewById(R.id.root), resources.displayMetrics.widthPixels / 2, 50, 0f,
                            Math.max(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels).toFloat())
                    circularReveal.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {}
                    })
                    circularReveal.duration = 1000
                    circularReveal.start()
                }
            }

            override fun onViewDetachedFromWindow(v: View) {

            }
        })

        //end of animate
        findViewById<View>(R.id.item).setOnClickListener {
            ActivityNavigator.forActivity(this@ActivityA).withFillData { bundle ->
                val o = floatArrayOf(0f, 1f, 2f)
                Log.i("AAA", o.toString())
                bundle.putBinder("asd", ActivityB.ObjectBinder(o))
            }.goForResult(ActivityB::class.java, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && data != null)
            Toast.makeText(this, "ResultCode:" + resultCode + " " + data.getStringExtra("extra"), Toast.LENGTH_SHORT).show()
    }
}
