package by.vkatz.utils

import android.content.Context
import android.graphics.Typeface
import java.util.*

class FontsManager private constructor() {
    companion object {
        val instance: FontsManager by lazy { FontsManager() }
    }

    private val fonts = HashMap<String, Typeface>()

    fun getFont(context: Context, fontName: String?): Typeface? {
        if (fontName == null || fontName == "") return null
        return try {
            if (fonts.containsKey(fontName)) fonts[fontName]
            else {
                val font = Typeface.createFromAsset(context.assets, fontName)
                fonts.put(fontName, font)
                font
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
