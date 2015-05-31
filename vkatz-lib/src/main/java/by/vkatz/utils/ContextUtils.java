package by.vkatz.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * User: Katz
 * Date: 23.12.13
 * Time: 22:54
 */
@SuppressWarnings("unused")
public class ContextUtils {
    public static View getView(Context context, int layoutResID) {
        return getView(context, layoutResID, null, false);
    }

    public static View getView(Context context, int layoutResID, ViewGroup parent, boolean attachToParent) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layoutResID, parent, attachToParent);
    }
}
