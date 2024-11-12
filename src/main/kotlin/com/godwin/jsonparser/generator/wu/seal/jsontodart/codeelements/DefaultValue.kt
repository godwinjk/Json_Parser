package wu.seal.jsontodart.codeelements

import wu.seal.jsontodart.utils.*

/**
 * Default Value relative
 * Created by Seal.wu on 2017/9/25.
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

