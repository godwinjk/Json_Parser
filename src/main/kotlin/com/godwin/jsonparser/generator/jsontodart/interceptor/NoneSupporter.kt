package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.utils.getIndent
import java.util.*

/**
 *
 * Created by Godwin on 2024/12/20.
 */

interface INoneLibSupporter {
    fun getNoneLibSupporterProperty(rawPropertyName: String, propertyType: String): String

    fun getNoneLibSupporterClassName(rawClassName: String): String
}


object NoneSupporter : INoneLibSupporter {
    override fun getNoneLibSupporterClassName(rawClassName: String): String {
        return ""
    }

    override fun getNoneLibSupporterProperty(rawPropertyName: String, propertyType: String): String {
        val blockBuilder = StringBuilder()
        blockBuilder.append(getIndent())
        blockBuilder.append(propertyType)
        blockBuilder.append(" ")
        blockBuilder.append(rawPropertyName.replaceFirstChar { it.lowercase(Locale.getDefault()) })
        blockBuilder.append(";")
        return blockBuilder.toString()
    }
}

