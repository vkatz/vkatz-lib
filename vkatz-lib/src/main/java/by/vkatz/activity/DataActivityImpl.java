package by.vkatz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by vKatz on 04.05.2015.
 */
public abstract class DataActivityImpl {
    DataActivity activity;

    void setActivity(DataActivity activity) {
        this.activity = activity;
    }

    public DataActivity getParent() {
        return activity;
    }

    public void onCreate(Bundle savedInstanceState) {

    }

    protected void onResume() {
    }

    protected void onPause() {
    }

    protected void onSaveInstanceState(Bundle outState) {
    }

    protected void onStart() {
    }

    protected void onStop() {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    protected void onDestroy() {
    }

    public boolean onBackPressed() {
        return false;
    }

    public boolean onMenuPressed() {
        return false;
    }

    public void setContentView(int layoutResID) {
        getParent().setContentView(layoutResID);
    }

    public void setContentView(View view) {
        getParent().setContentView(view);
    }

    public View findViewById(int id) {
        return getParent().findViewById(id);
    }
}
