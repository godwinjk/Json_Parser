package com.godwin.jsonparser.generator.jsontodart.utils

import com.godwin.jsonparser.generator.jsontodart.codeelements.KClassName
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import java.util.*

/**
 * Type helper deal with type string
 * Created by Godwin on 2024/12/20.
 */

const val TYPE_STRING = "String"
const val TYPE_INT = "int"
const val TYPE_LONG = "int"
const val TYPE_DOUBLE = "double"
const val TYPE_BOOLEAN = "bool"
const val TYPE_ANY = "dynamic"

const val MAP_DEFAULT_OBJECT_VALUE_TYPE = "MapValue"
const val MAP_DEFAULT_ARRAY_ITEM_VALUE_TYPE = "Item"

const val BACKSTAGE_NULLABLE_POSTFIX = "_&^#"

/**
 * the default type
 */
const val DEFAULT_TYPE = TYPE_ANY

fun getPrimitiveType(jsonPrimitive: JsonPrimitive): String {
    return when {
        jsonPrimitive.isBoolean -> TYPE_BOOLEAN
        jsonPrimitive.isNumber -> when {
            jsonPrimitive.asString.contains(".") -> TYPE_DOUBLE
            jsonPrimitive.asLong > Integer.MAX_VALUE -> TYPE_LONG
            else -> TYPE_INT
        }

        jsonPrimitive.isString -> TYPE_STRING
        else -> TYPE_STRING
    }
}

fun String.isPrimitiveType(): Boolean {
    return this == TYPE_STRING || this == TYPE_LONG || this == TYPE_INT || this == TYPE_DOUBLE || this == TYPE_BOOLEAN || this == TYPE_ANY
}

fun String.isListType(): Boolean {
    return this.startsWith("List<")
}

fun getJsonObjectType(type: String): String {
    return KClassName.getName(type)
}


/**
 * get the inmost child type of array type
 */
fun getChildType(arrayType: String): String = arrayType.replace(Regex("List<|>"), "")

/**
 * get the type string without '?' character
 */
fun getRawType(outputType: String): String = outputType.replace("?", "").replace(".*\\.".toRegex(), "")

fun getArrayType(propertyName: String, jsonElementValue: JsonArray): String {
    val preSubType = adjustPropertyNameForGettingArrayChildType(propertyName)
    var subType = DEFAULT_TYPE
    if (isArrayContainsDifferentTypes(jsonElementValue)) {
        return "List<$subType>"
    }
    val iterator = jsonElementValue.iterator()
    if (iterator.hasNext()) {
        val next = iterator.next()
        subType = when {
            next.isJsonPrimitive -> getPrimitiveType(next.asJsonPrimitive)
            next.isJsonObject -> getJsonObjectType(preSubType)
            next.isJsonArray && jsonElementValue.size() == 1 -> getArrayType(preSubType, next.asJsonArray)
            else -> DEFAULT_TYPE
        }
        return "List<$subType>"
    }
    return "List<$subType>"
}

fun isArrayContainsDifferentTypes(jsonArray: JsonArray): Boolean {
    val typesSet = mutableSetOf<String>()

    // Iterate over all elements in the array
    for (jsonElement in jsonArray) {
        val type = when {
            jsonElement.isJsonPrimitive -> {
                when {
                    jsonElement.asJsonPrimitive.isNumber -> "Number"  // for Int, Double, etc.
                    jsonElement.asJsonPrimitive.isBoolean -> "Boolean"
                    jsonElement.asJsonPrimitive.isString -> "String"
                    else -> "UnknownPrimitive"
                }
            }

            jsonElement.isJsonObject -> "jsonObject"
            jsonElement.isJsonArray -> "jsonArray"
            else -> "unknown"
        }

        // Add the detected type to the set
        typesSet.add(type)

        // If we already have more than one type, return true
        if (typesSet.size > 1) {
            return true
        }
    }

    // If only one type is found, return false
    return false
}

fun isExpectedJsonObjArrayType(jsonElementArray: JsonArray): Boolean {
    return jsonElementArray.firstOrNull()?.isJsonObject ?: false
}

/**
 * when get the child type in an array
 * ,we need to make the property name be legal and then modify the property name to make it's type name looks like a child type.
 * filter the sequence as 'list' ,"List'
 * and remove the last character 's' to make it like a child rather than a list
 */
fun adjustPropertyNameForGettingArrayChildType(property: String): String {
    var innerProperty = KClassName.getLegalClassName(property)
    when {
        innerProperty.endsWith("ies") -> {
            innerProperty = innerProperty.substring(0, innerProperty.length - 3) + "y"
        }

        innerProperty.contains("List") -> {
            val firstLatterAfterListIndex = innerProperty.lastIndexOf("List") + 4
            if (innerProperty.length > firstLatterAfterListIndex && innerProperty[firstLatterAfterListIndex] in 'A'..'Z') {
                innerProperty = innerProperty.replaceFirst("List", "", false)
            } else if (innerProperty.length == firstLatterAfterListIndex) {
                innerProperty = innerProperty.substring(0, innerProperty.lastIndexOf("List"))
            }
        }

        innerProperty.contains("list") -> {
            if (innerProperty == "list") {
                innerProperty = "Item${Date().time.toString().last()}"
            } else if (innerProperty.indexOf("list") == 0 && innerProperty.length >= 5) {
                val end = innerProperty.substring(5)
                val pre = (innerProperty[4] + "").lowercase(Locale.getDefault())
                innerProperty = pre + end
            }
        }

        innerProperty.endsWith("s") -> {
            innerProperty = innerProperty.substring(0, innerProperty.length - 1)
        }
    }

    return innerProperty
}

/**
 * if the jsonObject maybe a Map Instance
 */
fun maybeJsonObjectBeMapType(jsonObject: JsonObject): Boolean {
    var maybeMapType = true
    if (jsonObject.entrySet().isEmpty()) {
        maybeMapType = false
    } else {
        jsonObject.entrySet().forEach {
            val isPrimitiveNotStringType = try {
                JsonParser().parse(it.key).asJsonPrimitive.isString.not()
            } catch (e: Exception) {
                false
            }
            maybeMapType = isPrimitiveNotStringType and maybeMapType
        }
    }
    return maybeMapType
}

/**
 * get the Key Type of Map type converted from jsonObject
 */
fun getMapKeyTypeConvertFromJsonObject(jsonObject: JsonObject): String {
    return getPrimitiveType(JsonParser().parse(jsonObject.entrySet().first().key).asJsonPrimitive)
}

/**
 * get Map Type Value Type from JsonObject object struct
 */
fun getMapValueTypeConvertFromJsonObject(jsonObject: JsonObject): String {
    var valueType = ""
    jsonObject.entrySet().forEach {
        val jsonElement = it.value
        if (jsonElement.isJsonPrimitive) {
            val currentValueType = getPrimitiveType(jsonElement.asJsonPrimitive)
            if (valueType.isEmpty()) {
                valueType = currentValueType
            } else {
                if (currentValueType != valueType) {
                    return DEFAULT_TYPE
                }
            }
        } else if (jsonElement.isJsonObject) {
            if (valueType.isEmpty()) {
                valueType = MAP_DEFAULT_OBJECT_VALUE_TYPE
            } else {
                if (valueType != MAP_DEFAULT_OBJECT_VALUE_TYPE) {
                    return DEFAULT_TYPE
                }
            }
        } else if (jsonElement.isJsonArray) {

            if (valueType.isEmpty()) {
                valueType = getArrayType(MAP_DEFAULT_ARRAY_ITEM_VALUE_TYPE, jsonElement.asJsonArray)
            } else {
                if (valueType != getArrayType(MAP_DEFAULT_ARRAY_ITEM_VALUE_TYPE, jsonElement.asJsonArray)) {
                    return DEFAULT_TYPE
                }
            }
        }
    }
    if (valueType.isEmpty()) {
        valueType = DEFAULT_TYPE
    }
    return valueType
}
