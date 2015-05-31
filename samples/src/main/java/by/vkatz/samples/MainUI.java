package by.vkatz.samples;

import by.vkatz.screens.ScreensLayoutActivity;
import by.vkatz.utils.AnimationBuilder;

public class MainUI extends ScreensLayoutActivity {
    @Override
    protected void init() {
        init(R.layout.main, R.id.screen_layout);
        getScreensLayout().setData("settings", Settings.load(this));
        getScreensLayout().setAlternativeGoAnimations(AnimationBuilder.alpha(0, 1, 250), null);
        getScreensLayout().go(new SplashScreen(), false);
    }
}
