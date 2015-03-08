package by.vkatz.samples;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import by.vkatz.screens.Screen;

/**
 * Created by vKatz on 08.03.2015.
 */
public class SplashScreen extends Screen {
    @Override
    public View getView() {
        TextView textView = new TextView(getContext());
        textView.setBackgroundColor(Color.WHITE);
        textView.setTextColor(Color.BLACK);
        return textView;
    }

    @Override
    public void onShow(View view) {
        super.onShow(view);
        getParent().executeWhenPossible(new Runnable() {
            @Override
            public void run() {
                getParent().back();
            }
        });
        ((TextView) view).setText("Hi");
        getParent().postDelayed(new Runnable() {
            @Override
            public void run() {
                getParent().go(new MainScreen());
            }
        }, 3000);
    }
}
