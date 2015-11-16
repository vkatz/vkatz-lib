package by.vkatz.samples;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import by.vkatz.screens.Screen;
import by.vkatz.screens.ScreensActivity;

/**
 * Created by vKatz on 16.11.2015.
 */
public abstract class BaseScreen extends Screen {
    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        AnimatorSet set = new AnimatorSet();
        int from, to;
        if (getParent().getTransactionType() == ScreensActivity.TransactionType.New) {
            from = enter ? 200 : 0;
            to = enter ? 0 : -200;
        } else {
            from = enter ? -200 : 0;
            to = enter ? 0 : 200;
        }
        set.playTogether(
                ObjectAnimator.ofFloat(null, "translationX", from, to),
                ObjectAnimator.ofFloat(null, "alpha", enter ? 0 : 1, enter ? 1 : 0));
        return set;
    }
}
