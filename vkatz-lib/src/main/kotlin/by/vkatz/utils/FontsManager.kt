package by.vkatz.utils

import android.content.Context
import android.graphics.Typeface
import java.util.*

/**
 * User: Katz
 * Date: 06.02.14
 * Time: 19:52
 */
class FontsManager private constructor() {
    companion object {
        val instance: FontsManager by lazy { FontsManager() }
    }

    private val fonts = HashMap<String, Typeface>()

    fun getFont(context: Context, fontName: String?): Typeface? {
        if (fontName == null || fontName == "") return null
        try {
            if (fonts.containsKey(fontName))
                return fonts[fontName]
            else {
                val font = Typeface.createFromAsset(context.assets, fontName)
                fonts.put(fontName, font)
                return font
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }
}
