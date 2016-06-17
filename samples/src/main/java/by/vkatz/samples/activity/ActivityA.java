package by.vkatz.samples.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Toast;

import by.vkatz.samples.R;
import by.vkatz.utils.ActivityNavigator;
import by.vkatz.utils.Functions;

/**
 * Created by Katz on 17.06.2016.
 */

public class ActivityA extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        Bundle data = ActivityNavigator.getData(this);
        Toast.makeText(this, "" + data.getString("a"), Toast.LENGTH_SHORT).show();
        //animate
        findViewById(R.id.root).addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                v.removeOnAttachStateChangeListener(this);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(Color.TRANSPARENT);
                    getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    getWindow().setStatusBarColor(Color.TRANSPARENT);
                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(findViewById(R.id.root), getResources().getDisplayMetrics().widthPixels / 2, 50, 0,
                            Math.max(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels));
                    circularReveal.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }
                    });
                    circularReveal.setDuration(1000);
                    circularReveal.start();
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });

        //end of animate
        findViewById(R.id.item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityNavigator.forActivity(ActivityA.this).withFillData(new Functions.Func1<Void, Bundle>() {
                    @Override
                    public Void execute(Bundle bundle) {
                        Object o = new float[]{0, 1, 2};
                        Log.i("AAA", o.toString());
                        bundle.putBinder("asd", new ActivityB.ObjectBinder(o));
                        return null;
                    }
                }).goForResult(ActivityB.class, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null)
            Toast.makeText(this, "ResultCode:" + resultCode + " " + data.getStringExtra("extra"), Toast.LENGTH_SHORT).show();
    }
}
