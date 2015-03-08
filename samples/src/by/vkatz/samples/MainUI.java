package by.vkatz.samples;

import by.vkatz.utils.AnimationBuilder;
import by.vkatz.utils.VkatzActivity;

public class MainUI extends VkatzActivity {
    @Override
    protected void init() {
        init(R.layout.main, R.id.screen_layout);
        getScreensLayout().setData("settings", Settings.load(this));
        getScreensLayout().setAlternativeGoAnimations(AnimationBuilder.alpha(0, 1, 250), null);
        getScreensLayout().go(new SplashScreen(), false);
    }
}
