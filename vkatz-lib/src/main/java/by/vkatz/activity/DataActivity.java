package by.vkatz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

/**
 * Created by vKatz on 04.05.2015.
 */
public class DataActivity extends Activity {
    private static final String SCREEN_NAME = "_screenName";

    private DataActivityImpl impl = null;
    private String screenName = null;
    private Handler handler;
    private int fIn = 0, fOut = 0, bIn = 0, bOut = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        if (getIntent().hasExtra(SCREEN_NAME)) {
            screenName = getIntent().getStringExtra(SCREEN_NAME);
            impl = ((DataActivityApplication) getApplication()).getImpl(screenName);
        } else impl = ((DataActivityApplication) getApplication()).getFirst();
        if (impl == null) {
            finish();
            return;
        }
        impl.setActivity(this);
        impl.onCreate(savedInstanceState);
    }

    public final boolean post(Runnable r) {
        return handler.post(r);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return handler.postDelayed(r, delayMillis);
    }

    public final void setAnimations(int fIn, int fOut, int bIn, int bOut) {
        this.fIn = fIn;
        this.fOut = fOut;
        this.bIn = bIn;
        this.bOut = bOut;
    }

    public final void go(DataActivityImpl impl) {
        go(impl, true);
    }

    public final void go(DataActivityImpl impl, boolean history) {
        go(impl, history, null);
    }

    public final void go(DataActivityImpl impl, boolean history, Bundle data) {
        go(impl, history, data, false);
    }

    public final void go(DataActivityImpl impl, boolean history, Bundle data, boolean clearTop) {
        go(this, impl, history, data, clearTop);
        if (fIn != 0 && fOut != 0) overridePendingTransition(fIn, fOut);
    }

    public static void go(Activity activity, DataActivityImpl impl, boolean history, Bundle data, boolean clearTop) {
        if (clearTop) ((DataActivityApplication) activity.getApplication()).clearImpls();
        String name = ((DataActivityApplication) activity.getApplication()).setImpl(impl);
        Intent intent = new Intent(activity, DataActivity.class);
        intent.putExtra(SCREEN_NAME, name);
        if (clearTop) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!history) intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        activity.startActivity(intent, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        impl.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        impl.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        impl.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        impl.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        impl.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        impl.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU && impl.onMenuPressed() || super.onKeyUp(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        if (!impl.onBackPressed()) {
            super.onBackPressed();
            if (bIn != 0 && bOut != 0) overridePendingTransition(bIn, bOut);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (screenName != null) ((DataActivityApplication) getApplication()).finishImpl(screenName);
        if (impl != null) impl.onDestroy();
    }
}
