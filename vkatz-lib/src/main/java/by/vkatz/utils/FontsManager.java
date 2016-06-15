package by.vkatz.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * User: Katz
 * Date: 06.02.14
 * Time: 19:52
 */
@SuppressWarnings("WeakerAccess")
public class FontsManager {
    private static FontsManager _this;
    private HashMap<String, Typeface> fonts = new HashMap<>();

    private FontsManager() {
    }

    public static FontsManager instance() {
        if (_this == null) _this = new FontsManager();
        return _this;
    }

    public Typeface getFont(Context context, String fontName) {
        if (fontName == null || fontName.equals("")) return null;
        try {
            if (fonts.containsKey(fontName)) return fonts.get(fontName);
            else {
                Typeface font = Typeface.createFromAsset(context.getAssets(), fontName);
                fonts.put(fontName, font);
                return font;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
