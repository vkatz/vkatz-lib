package by.vkatz.utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by Vkatz
 */
@SuppressWarnings("unused")
public class AnimationBuilder {
    public static Animation alpha(int from, int to, int duration) {
        Animation animation = new AlphaAnimation(from, to);
        animation.setDuration(duration);
        return animation;
    }

    public static Animation rotate(int from, int to, int duration, boolean infinity) {
        RotateAnimation animation = new RotateAnimation(from, to, 1, 0.5f, 1, 0.5f);
        animation.setDuration(duration);
        if (infinity) {
            animation.setRepeatCount(Animation.INFINITE);
            animation.setInterpolator(new LinearInterpolator());
        }
        return animation;
    }
}
