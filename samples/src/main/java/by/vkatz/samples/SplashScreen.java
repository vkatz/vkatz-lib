package by.vkatz.samples;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import by.vkatz.screens.Screen;

/**
 * Created by vKatz on 08.03.2015.
 */
public class SplashScreen extends Screen {
    @Override
    public View createView() {
        TextView textView = new TextView(getContext());
        textView.setBackgroundColor(Color.WHITE);
        textView.setTextColor(Color.BLACK);
        return textView;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        ((TextView) view).setText("Hi");
        getParent().postDelayer(3000, new Runnable() {
            @Override
            public void run() {
                getParent().go(new MainScreen(), false);
            }
        });
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        return ObjectAnimator.ofFloat(null, "alpha", enter ? 0 : 1, enter ? 1 : 0);
    }
}
