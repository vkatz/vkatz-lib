package by.vkatz.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Katz on 17.06.2016.
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class ActivityNavigator<A extends Activity> {
    private A activity;
    private Bundle bundle;
    private Functions.Func1<Void, Intent> intentConfigurator = null;

    private ActivityNavigator(A activity) {
        this.activity = activity;
        bundle = new Bundle();
    }

    public static <A extends Activity> ActivityNavigator forActivity(A activity) {
        return new ActivityNavigator<>(activity);
    }

    public static Bundle getData(Activity activity) {
        return activity.getIntent().getExtras();
    }

    public ActivityNavigator withData(Bundle data) {
        bundle.putAll(data);
        return this;
    }

    public ActivityNavigator withData(String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public ActivityNavigator withData(String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public ActivityNavigator withData(String key, float value) {
        bundle.putFloat(key, value);
        return this;
    }

    public ActivityNavigator withData(String key, double value) {
        bundle.putDouble(key, value);
        return this;
    }

    public ActivityNavigator withData(String key, Serializable value) {
        bundle.putSerializable(key, value);
        return this;
    }

    public ActivityNavigator withData(String key, Parcelable value) {
        bundle.putParcelable(key, value);
        return this;
    }

    public ActivityNavigator withFillData(Functions.Func1<Void, Bundle> fillBundle) {
        fillBundle.execute(bundle);
        return this;
    }

    public ActivityNavigator configureIntent(Functions.Func1<Void, Intent> intentConfigurator) {
        this.intentConfigurator = intentConfigurator;
        return this;
    }

    private <T extends Activity> Intent getGoIntent(Class<T> activity) {
        Intent intent = new Intent(this.activity, activity);
        if (intentConfigurator != null) intentConfigurator.execute(intent);
        intent.putExtras(bundle);
        return intent;
    }

    public <T extends Activity> void go(Class<T> activity) {
        go(activity, null);
    }

    public <T extends Activity> void go(Class<T> activity, Bundle options) {
        this.activity.startActivity(getGoIntent(activity), options);
    }

    public <T extends Activity> void goForResult(Class<T> activity, int requestCode) {
        goForResult(activity, requestCode, null);
    }

    public <T extends Activity> void goForResult(Class<T> activity, int requestCode, Bundle options) {
        this.activity.startActivityForResult(getGoIntent(activity), requestCode, options);
    }

    public void back() {
        activity.finish();
    }

    public void backWithResult(int resultCode) {
        Intent intent = new Intent();
        intent.putExtras(bundle);
        activity.setResult(resultCode, intent);
        activity.finish();
    }

    public void finishActivities(int requestCode) {
        activity.finishActivity(requestCode);
    }
}
