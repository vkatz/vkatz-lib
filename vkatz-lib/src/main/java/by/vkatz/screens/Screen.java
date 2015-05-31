package by.vkatz.screens;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

/**
 * User: Katz
 * Date: 23.12.13
 * Time: 16:43
 */
@SuppressWarnings("unused")
public abstract class Screen {
    private ScreensLayout parent;

    public final Context getContext() {
        return getParent() == null ? null : getParent().getContext();
    }

    public abstract View getView();

    public void onShow(View view) {
    }

    public void onHide(View view) {
    }

    public void release() {
    }

    public void onActivityResult(View view, int requestCode, int resultCode, Intent data) {
    }

    public boolean onBackPressed() {
        return false;
    }

    public boolean onMenuPressed() {
        return false;
    }

    public void onActivityNewIntent(Intent intent) {
    }

    public void onActivityResume() {
    }

    public void onActivityPause() {
    }

    public void onActivityDestroy() {
    }

    public void onActivitySaveInstanceState(Bundle outState) {
    }

    public void onActivityLowMemory() {
    }

    public void onActivityConfigurationChanged(Configuration newConfig) {
    }

    public final ScreensLayout getParent() {
        return parent;
    }

    void setParent(ScreensLayout parent) {
        this.parent = parent;
    }
}
