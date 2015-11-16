package by.vkatz.samples;

import android.widget.Toast;

import by.vkatz.screens.ScreensActivity;

public class MainUI extends ScreensActivity {
    private boolean exit = false;

    @Override
    public void init() {
        init(R.layout.main, R.id.screen_layout);
        setData("settings", Settings.load(this));
        go(new SplashScreen());
    }


    @Override
    public void backPressed() {
        if (exit) super.backPressed();
        else {
            exit = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            postDelayer(3000, new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            });
        }
    }
}
