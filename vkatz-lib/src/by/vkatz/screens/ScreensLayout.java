package by.vkatz.screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Stack;

/**
 * User: Katz
 * Date: 23.12.13
 * Time: 16:43
 */
@SuppressWarnings("unused")
public class ScreensLayout extends RelativeLayout {

    private static final int DEFAULT_ANIMATION_DURATION = 300;

    public enum LayersOrdering {Default, ComingOnTop, ExistOnTop}

    private RelativeLayout layer1, layer2;
    private Stack<Pair<Screen, String>> history;
    private Animation fIn, fOut, bIn, bOut, aIn, aOut;
    private Pair<Screen, String> currentScreen;
    private boolean storeInHistory, useAlternativeAnimation;
    private View currentView;
    private int lock;
    private LayersOrdering order;
    private HashMap<String, Object> data;
    private Runnable execute;
    private Handler handler;
    private Bundle savedInstanceState;
    private Activity activity;

    public ScreensLayout(Context context) {
        super(context);
        init(context);
    }

    public ScreensLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScreensLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        removeAllViews();
        handler = new Handler();
        addView(layer2 = new RelativeLayout(context), -1, -1);
        addView(layer1 = new RelativeLayout(context), -1, -1);
        lock = 0;
        order = LayersOrdering.Default;
        history = new Stack<>();
        currentScreen = null;
        useAlternativeAnimation = false;
        fIn = new TranslateAnimation(1, 1, 1, 0, 0, 0, 0, 0);
        fOut = new TranslateAnimation(1, 0, 1, -1, 0, 0, 0, 0);
        bIn = new TranslateAnimation(1, -1, 1, 0, 0, 0, 0, 0);
        bOut = new TranslateAnimation(1, 0, 1, 1, 0, 0, 0, 0);
        fIn.setDuration(DEFAULT_ANIMATION_DURATION);
        fOut.setDuration(DEFAULT_ANIMATION_DURATION);
        bIn.setDuration(DEFAULT_ANIMATION_DURATION);
        bOut.setDuration(DEFAULT_ANIMATION_DURATION);
        aIn = aOut = null;
        data = new HashMap<>();
    }

    public void go(Screen screen) {
        go(screen, true, null);
    }

    public void go(Screen screen, boolean storeInHistory) {
        go(screen, storeInHistory, null);
    }

    public void go(Screen screen, boolean storeInHistory, String name) {
        animate(screen, storeInHistory, name, true);
    }

    public void back() {
        if (isLocked()) return;
        if (!history.empty()) {
            Pair<Screen, String> data = history.pop();
            animate(data.first, true, data.second, false);
        }
    }

    public void backTo(String name) {
        if (isLocked()) return;
        if (!history.empty()) {
            Pair<Screen, String> i = currentScreen;
            while (!name.equals(i.second) && !history.empty()) i = history.pop();
            if (name.equals(i.second)) animate(i.first, true, i.second, false);
        }
    }

    @SuppressWarnings("All")
    private void animate(Screen to, boolean storeInHistory, String name, final boolean isForward) {
        if (isLocked()) return;
        lock++;
        if (isForward) to.setParent(this);
        if (isForward && this.storeInHistory && currentScreen != null) history.push(currentScreen);
        View view = to.getView();
        final RelativeLayout inLayer, outLayer;
        Animation inAnim, outAnim;
        switch (order) {
            case ComingOnTop:
                moveLayerContent(inLayer = layer1, outLayer = layer2);
                break;
            case ExistOnTop:
                moveLayerContent(inLayer = layer2, outLayer = layer1);
                break;
            case Default:
            default:
                if (isForward) moveLayerContent(inLayer = layer1, outLayer = layer2);
                else moveLayerContent(inLayer = layer2, outLayer = layer1);
        }
        inLayer.addView(view, -1, -1);
        if (useAlternativeAnimation) inAnim = aIn;
        else inAnim = isForward ? fIn : bIn;
        if (useAlternativeAnimation) outAnim = aOut;
        else outAnim = isForward ? fOut : bOut;
        useAlternativeAnimation = false;
        aIn = aOut = null;
        to.onShow(view);
        Handler handler = new Handler() {
            Screen screen = currentScreen == null ? null : currentScreen.first;
            View view = currentView;

            @Override
            public void handleMessage(Message msg) {
                if (!isLocked()) {
                    outLayer.removeAllViews();
                    if (screen != null && view != null) screen.onHide(view);
                    if (screen != null && !isForward) {
                        screen.setParent(null);
                        screen.release();
                    }
                    if (execute != null) {
                        Runnable r = execute;
                        execute = null;
                        r.run();
                    }
                }
            }
        };
        if (inAnim != null) inLayer.startAnimation(prepareAnimation(inAnim, handler));
        if (outAnim != null) outLayer.startAnimation(prepareAnimation(outAnim, handler));
        currentScreen = new Pair<>(to, name);
        currentView = view;
        this.storeInHistory = storeInHistory;
        lock--;
        handler.sendEmptyMessage(0);
    }

    private Animation prepareAnimation(Animation animation, final Handler handler) {
        if (animation == null) return null;
        lock++;
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                lock--;
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }

    private void moveLayerContent(RelativeLayout from, RelativeLayout to) {
        if (from.getChildCount() > 0) {
            View v = from.getChildAt(0);
            from.removeView(v);
            to.addView(v, -1, -1);
        }
    }

    public ScreensLayout clearHistory() {
        for (Pair<Screen, String> i : history) i.first.release();
        history.clear();
        return this;
    }

    public ScreensLayout clearHistoryUntil(String name) {
        if (!history.empty()) {
            Pair<Screen, String> i = history.pop();
            while ((i.second == null || !i.second.equals(name)) && !history.empty()) {
                i.first.release();
                i = history.pop();
            }
            if (i.second != null && i.second.equals(name)) history.push(i);
        }
        return this;
    }

    public ScreensLayout setGoAnimations(Animation fIn, Animation fOut, Animation bIn, Animation bOut) {
        this.fIn = fIn;
        this.fOut = fOut;
        this.bIn = bIn;
        this.bOut = bOut;
        return this;
    }

    public ScreensLayout setAlternativeGoAnimations(Animation aIn, Animation aOut) {
        this.aIn = aIn;
        this.aOut = aOut;
        useAlternativeAnimation = true;
        return this;
    }

    public ScreensLayout clearAlternativeGoAnimation() {
        useAlternativeAnimation = false;
        aIn = null;
        aOut = null;
        return this;
    }

    public ScreensLayout setOrder(LayersOrdering order) {
        this.order = order;
        return this;
    }

    public boolean containScreen(String name) {
        for (Pair<Screen, String> i : history) if (i.second.equals(name)) return true;
        return false;
    }

    public boolean isLocked() {
        return lock != 0;
    }

    public boolean isBackPossible() {
        return !history.empty();
    }

    public void setData(String key, Object data) {
        this.data.put(key, data);
    }

    public Object getData(String key) {
        return data.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key, Class<T> T) {
        return (T) getData(key);
    }

    public void removeData(String key) {
        data.remove(key);
    }

    public void clearData() {
        data.clear();
    }

    /**
     * Execute now if not locked. Otherwise will executed on unlock
     */
    public void executeWhenPossible(Runnable execute) {
        if (isLocked()) this.execute = execute;
        else execute.run();
    }

    public void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    //Activity compatibility block


    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void onActivityCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    /**
     * Pass this event to current screen
     */
    public void onActivityResume() {
        if (currentScreen != null && currentScreen.first != null) currentScreen.first.onActivityResume();
    }

    /**
     * Pass this event to current screen
     *
     * @param intent received intent
     */
    public void onActivityNewIntent(Intent intent) {
        if (currentScreen != null && currentScreen.first != null) currentScreen.first.onActivityNewIntent(intent);
    }


    /**
     * Pass this event to current screen
     */
    public void onActivityPause() {
        if (currentScreen != null && currentScreen.first != null) currentScreen.first.onActivityPause();
    }

    /**
     * Pass this event to current screen
     */
    public void onActivityDestroy() {
        if (currentScreen != null && currentScreen.first != null) currentScreen.first.onActivityDestroy();
    }

    /**
     * Pass this event to current screen
     */
    public void onActivitySaveInstanceState(Bundle outState) {
        if (currentScreen != null && currentScreen.first != null)
            currentScreen.first.onActivitySaveInstanceState(outState);
    }

    /**
     * Pass this event to current screen
     */
    public void onActivityLowMemory() {
        if (currentScreen != null && currentScreen.first != null) currentScreen.first.onActivityLowMemory();
    }

    /**
     * Pass this event to current screen
     *
     * @return true if current screen handle this event({@link Screen#onBackPressed()} return true), false otherwise
     */
    public boolean onBackPressed() {
        return currentScreen != null && currentScreen.first != null && currentScreen.first.onBackPressed();
    }

    public void onActivityConfigurationChanged(Configuration newConfig) {
        if (currentScreen != null && currentScreen.first != null)
            currentScreen.first.onActivityConfigurationChanged(newConfig);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (currentScreen != null) currentScreen.first.onActivityResult(currentView, requestCode, resultCode, data);
    }
}
