package by.vkatz.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * User: Katz
 * Date: 06.02.14
 * Time: 19:52
 */
public class FontsManager {
    private static FontsManager _this;

    public static FontsManager instance() {
        if (_this == null) _this = new FontsManager();
        return _this;
    }

    @SuppressWarnings("unused")
    public static void release() {
        _this = null;
        System.gc();
    }

    private HashMap<String, Typeface> fonts = new HashMap<>();

    public FontsManager() {
        fonts = new HashMap<>();
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
