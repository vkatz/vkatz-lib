package by.vkatz.screens;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

/**
 * Created by vKatz on 17.09.2015.
 */
@SuppressWarnings({"unused", "unchecked"})
public abstract class Screen extends Fragment {
    @Override
    public Context getContext() {
        return getParent();
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView();
        initView(view);
        return view;
    }

    public <Activity extends ScreensActivity> Activity getParent() {
        try {
            return (Activity) getActivity();
        } catch (Exception e) {
            return null;
        }
    }

    public abstract View createView();

    public void initView(View view) {
    }

    public final <This extends Screen, Data extends Serializable> This withData(String key, Data data) {
        if (getArguments() == null) setArguments(new Bundle());
        getArguments().putSerializable(key, data);
        return (This) this;
    }

    public final <This extends Screen, Data extends Parcelable> This withData(String key, Data data) {
        if (getArguments() == null) setArguments(new Bundle());
        getArguments().putParcelable(key, data);
        return (This) this;
    }

    public Object getRawData(String key) {
        return getArguments().get(key);
    }

    public final <T> T getData(String key) {
        return (T) getRawData(key);
    }

    public boolean onBackPressed() {
        return false;
    }
}
