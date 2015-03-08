package by.vkatz.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import by.vkatz.screens.ScreensLayout;

/**
 * User: Katz
 * Date: 04.02.14
 * Time: 11:29
 */
@SuppressWarnings("unused")
public abstract class VkatzActivity extends Activity {
    private ViewGroup content;
    private ScreensLayout screensLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        screensLayout.onActivityCreate(savedInstanceState);
    }

    public void reload() {
        init();
        screensLayout.onActivityCreate(null);
    }

    @Override
    public View findViewById(int id) {
        return content.findViewById(id);
    }

    /**
     * Should call one of {@link #init(int, int)} or {@link #init(android.view.ViewGroup, by.vkatz.screens.ScreensLayout)}
     * And {@link #getScreensLayout()}.{@link by.vkatz.screens.ScreensLayout#go go()}
     */
    protected abstract void init();

    /**
     * Should pass ids of layout and id of ScreenLayout from passed view reference
     *
     * @param layoutResId       id of layout. Layout root element should be extended of ViewGroup
     * @param screenLayoutResId id of ScreenLayout (will be call findViewById)
     */
    protected void init(int layoutResId, int screenLayoutResId) {
        ViewGroup view = (ViewGroup) ContextUtils.getView(this, layoutResId);
        init(view, (ScreensLayout) view.findViewById(screenLayoutResId));
    }

    /**
     * @param content       root view
     * @param screensLayout screen layout (should be child view of content)
     */
    protected void init(ViewGroup content, ScreensLayout screensLayout) {
        setContentView(content);
        this.content = content;
        this.screensLayout = screensLayout;
        screensLayout.setActivity(this);
    }

    protected ScreensLayout getScreensLayout() {
        return screensLayout;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        screensLayout.onActivityResult(requestCode, resultCode, data);
    }

    public void activityOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (!screensLayout.onBackPressed()) {
            if (screensLayout.isBackPossible()) screensLayout.back();
            else activityOnBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        screensLayout.onActivityConfigurationChanged(newConfig);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        screensLayout.onActivityNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        screensLayout.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        screensLayout.onActivityPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        screensLayout.onActivityDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        screensLayout.onActivityLowMemory();
    }
}
