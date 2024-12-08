package com.godwin.jsonparser.generator.jsontodart.codeelements

import com.godwin.jsonparser.generator.jsontodart.utils.*

/**
 * Default Value relative
 * Created by Godwin on 2024/12/20.
 */

fun getDefaultValue(propertyType: String): String {

    val rawType = getRawType(propertyType)

    return when {
        rawType == TYPE_INT -> 0.toString()
        rawType == TYPE_LONG -> 0L.toString()
        rawType == TYPE_STRING -> "\"\""
        rawType == TYPE_DOUBLE -> 0.0.toString()
        rawType == TYPE_BOOLEAN -> false.toString()
        rawType.contains("List<") -> "new List()"
        rawType.contains("Map<") -> "mapOf()"
        rawType == TYPE_ANY -> "Object()"
        rawType.contains("Array<") -> "emptyArray()"
        else -> "$rawType()"
    }
}


fun getDefaultValueAllowNull(propertyType: String) =
    if (propertyType.endsWith("?")) "null" else getDefaultValue(propertyType)

