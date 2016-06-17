package by.vkatz.samples.activity;

import android.app.Activity;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

import by.vkatz.samples.R;
import by.vkatz.utils.ActivityNavigator;

/**
 * Created by Katz on 17.06.2016.
 */

public class ActivityB extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        findViewById(R.id.item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityNavigator.forActivity(ActivityB.this).withData("extra", "String from activity B").backWithResult(1);
            }
        });
        Object o = ((ObjectBinder) ActivityNavigator.getData(this).getBinder("asd")).getObject();
        Log.i("AAA", o.toString());
        Log.i("AAAA", Arrays.toString(((float[]) o)));
    }


    static class ObjectBinder extends Binder{
        private Object object;

        ObjectBinder(Object object) {
            this.object = object;
        }
        Object getObject() {
            return object;
        }
    }

}
