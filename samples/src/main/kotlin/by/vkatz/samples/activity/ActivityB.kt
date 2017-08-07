package by.vkatz.samples.activity

import android.app.Activity
import android.os.Binder
import android.os.Bundle
import android.util.Log
import by.vkatz.samples.R
import by.vkatz.utils.ActivityNavigator
import java.util.*

/**
 * Created by Katz on 17.06.2016.
 */

class ActivityB : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)
        findViewById(R.id.item).setOnClickListener { ActivityNavigator.forActivity(this@ActivityB).withData("extra", "String from activity B").backWithResult(1) }
        val o = (ActivityNavigator.getData(this)!!.getBinder("asd") as ObjectBinder).obj
        Log.i("AAA", o.toString())
        Log.i("AAAA", Arrays.toString(o as FloatArray))
    }


    internal class ObjectBinder(val obj: Any) : Binder()
}
