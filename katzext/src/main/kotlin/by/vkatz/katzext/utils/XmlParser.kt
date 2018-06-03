package by.vkatz.katzext.utils

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream


object XmlParser {
    fun parse(input: InputStream, creator: XmlEntity.(HashMap<String, String>) -> Unit) {
        input.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            val rootEntity = XmlEntity()
            creator(rootEntity, HashMap())
            parser.next()
            parsePart(parser, rootEntity)
            Unit
        }
    }

    private fun parsePart(xml: XmlPullParser, entity: XmlEntity) {
        if (xml.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        val name = xml.name
        val handler = entity.objHandlers[name]
        if (handler == null) {
            skip(xml)
            return
        }
        val attrs = HashMap<String, String>()
        (0 until xml.attributeCount).forEach {
            attrs[xml.getAttributeName(it)] = xml.getAttributeValue(it)
        }
        val entityHandler = XmlEntity()
        handler(entityHandler, attrs)
        xml.next()
        while (xml.eventType != XmlPullParser.END_TAG) {
            when (xml.eventType) {
                XmlPullParser.START_TAG -> parsePart(xml, entityHandler)
                XmlPullParser.TEXT -> {
                    entityHandler.valueHandler?.invoke(xml.text)
                    xml.next()
                }
                else -> xml.next()
            }
        }
        xml.next()
    }

    private fun skip(xml: XmlPullParser) {
        if (xml.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (xml.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
        xml.next()
    }

    class XmlEntity {
        var objHandlers = HashMap<String, XmlEntity.(HashMap<String, String>) -> Unit>()
        var valueHandler: ((String) -> Unit)? = null

        fun item(obj: String, handler: XmlEntity.(attrs: HashMap<String, String>) -> Unit) {
            objHandlers[obj] = handler
        }

        fun value(handler: (value: String?) -> Unit) {
            valueHandler = handler
        }

        fun itemValue(obj: String, handler: (value: String?) -> Unit) {
            item(obj) { value(handler) }
        }
    }
}