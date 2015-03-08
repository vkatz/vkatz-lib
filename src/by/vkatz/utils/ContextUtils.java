package by.vkatz.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * User: Katz
 * Date: 23.12.13
 * Time: 22:54
 */
@SuppressWarnings("unused")
public class ContextUtils {
    public static View getView(Context context, int layoutResID) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layoutResID, null);
    }
}
