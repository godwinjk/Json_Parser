package com.godwin.jsonparser.generator.jsontodart

import com.godwin.jsonparser.generator.jsontodart.bean.jsonschema.GSON
import com.godwin.jsonparser.generator.jsontodart.bean.jsonschema.JsonSchema
import com.godwin.jsonparser.generator.jsontodart.codeelements.KProperty
import com.godwin.jsonparser.generator.jsontodart.utils.*
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * Kotlin code maker
 * Created by Godwin on 2024/12/20.
 */
class DartCodeMaker {

    private var className: String
    private var inputText: String
    private var inputElement: JsonElement

    private var originElement: JsonElement

    private val toBeAppend = HashSet<String>()

    constructor(className: String, inputText: String) {
        originElement = Gson().fromJson<JsonElement>(inputText, JsonElement::class.java)
        this.inputElement = TargetJsonElement(inputText).getTargetJsonElementForGeneratingCode()
        this.className = className
        this.inputText = inputText
    }

    constructor(className: String, jsonElement: JsonElement) {
        originElement = jsonElement
        this.inputElement = TargetJsonElement(jsonElement).getTargetJsonElementForGeneratingCode()
        this.className = className
        this.inputText = jsonElement.toString()
    }

    private fun parseJSONSchemaOrNull(className: String, json: String): String? {
        return try {
            val jsonSchema = GSON.fromJson<JsonSchema>(json, JsonSchema::class.java)
            if (jsonSchema.schema?.isNotBlank() != true) {
                throw IllegalArgumentException("input string is not valid json schema")
            }
            val generator = JsonSchemaDataClassGenerator(jsonSchema)
            generator.generate(className)
            generator.classes.joinToString("\n") { it.toString() }
        } catch (t: Throwable) {
            null
        }
    }

    private fun parseJSONString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\n")

        val jsonElement = inputElement
        checkIsNotEmptyObjectJSONElement(jsonElement)

        appendClassName(stringBuilder)
        appendCodeMember(stringBuilder, jsonElement.asJsonObject!!)

        stringBuilder.append("}")
        if (toBeAppend.isNotEmpty()) {
            appendSubClassCode(stringBuilder)
        }

        return stringBuilder.toString()
    }

    fun makeDartClassData(): String {
        return parseJSONSchemaOrNull(className, inputText)
            ?: parseJSONString()
    }

    //the fucking code
    private fun checkIsNotEmptyObjectJSONElement(jsonElement: JsonElement?) {
        if (jsonElement!!.isJsonObject) {
            if (jsonElement.asJsonObject.entrySet().isEmpty() && originElement.isJsonArray) {
                //when [[[{}]]]
                if (originElement.asJsonArray.onlyHasOneElementRecursive()) {
                    val unSupportJsonException = UnSupportJsonException("Unsupported Json String")
                    val adviceType =
                        getArrayType("dynamic", originElement.asJsonArray).replace(
                            Regex("Int|Float|String|Boolean"),
                            "dynamic"
                        )
                    unSupportJsonException.adviceType = adviceType
                    unSupportJsonException.advice =
                        """No need converting, just use $adviceType is enough for your json string"""
                    throw unSupportJsonException
                } else {
                    //when [1,"a"]
                    val unSupportJsonException = UnSupportJsonException("Unsupported Json String")
                    unSupportJsonException.adviceType = "List<dynamic>"
                    unSupportJsonException.advice =
                        """No need converting,  List<dynamic> may be a good class type for your json string"""
                    throw unSupportJsonException
                }
            }
        } else {
            /**
             * in this condition the only result it that we just give the json a List<Any> type is enough, No need to
             * do any convert to make class type
             */
            val unSupportJsonException = UnSupportJsonException("Unsupported Json String")
            val adviceType = getArrayType("Any", originElement.asJsonArray).replace("AnyX", "Any")
            unSupportJsonException.advice =
                """No need converting, just use $adviceType is enough for your json string"""
            throw unSupportJsonException
        }
    }

    private fun appendSubClassCode(stringBuilder: StringBuilder) {
        appendNormalSubClassCode(stringBuilder)
    }

    private fun appendNormalSubClassCode(stringBuilder: StringBuilder) {
        for (append in toBeAppend) {
            stringBuilder.append("\n")
            stringBuilder.append(append)
        }
    }

    private fun appendClassName(stringBuilder: StringBuilder) {
        stringBuilder.append("class ").append(className).append(" {\n")
    }

    private fun appendCodeMember(stringBuilder: StringBuilder, jsonObject: JsonObject) {
        val size = jsonObject.entrySet().size
        jsonObject.entrySet().forEachIndexed { index, (property, jsonElementValue) ->

            val isLast = (index == size - 1)
            when {
                jsonElementValue.isJsonNull -> addProperty(stringBuilder, property, DEFAULT_TYPE, null, isLast)

                jsonElementValue.isJsonPrimitive -> {
                    val type = getPrimitiveType(jsonElementValue.asJsonPrimitive)
                    addProperty(stringBuilder, property, type, jsonElementValue.asString, isLast)
                }

                jsonElementValue.isJsonArray -> {
                    jsonElementValue.asJsonArray.run {
                        var type = getArrayType(property, this)
                        if (isExpectedJsonObjArrayType(this) || onlyHasOneObjectElementRecursive()
                            || onlyHasOneSubArrayAndAllItemsAreObjectElementRecursive()
                        ) {
                            val subCode = try {
                                DartCodeMaker(getChildType(getRawType(type)), jsonElementValue).makeDartClassData()
                            } catch (e: UnSupportJsonException) {
                                type = e.adviceType
                                ""
                            }
                            toBeAppend.add(subCode)
                        }
                        addProperty(stringBuilder, property, type, "", isLast)
                    }
                }

                jsonElementValue.isJsonObject -> {
                    jsonElementValue.asJsonObject.run {
                        if (DartConfigManager.enableMapType && maybeJsonObjectBeMapType(this)) {
                            val mapKeyType = getMapKeyTypeConvertFromJsonObject(this)
                            var mapValueType = getMapValueTypeConvertFromJsonObject(this)
                            if (mapValueIsObjectType(mapValueType)) {
                                val subCode = try {
                                    DartCodeMaker(
                                        getChildType(mapValueType),
                                        entrySet().first().value
                                    ).makeDartClassData()
                                } catch (e: UnSupportJsonException) {
                                    mapValueType = e.adviceType
                                    ""
                                }
                                toBeAppend.add(subCode)
                            }
                            val mapType = "Map<$mapKeyType,$mapValueType>"
                            addProperty(stringBuilder, property, mapType, "", isLast)

                        } else {
                            var type = getJsonObjectType(property)
                            val subCode = try {
                                DartCodeMaker(getRawType(type), jsonElementValue).makeDartClassData()
                            } catch (e: UnSupportJsonException) {
                                type = e.adviceType
                                ""
                            }
                            toBeAppend.add(subCode)
                            addProperty(stringBuilder, property, type, "", isLast)
                        }
                    }
                }
            }
        }
    }

    private fun mapValueIsObjectType(mapValueType: String) = (mapValueType == MAP_DEFAULT_OBJECT_VALUE_TYPE
            || mapValueType.contains(MAP_DEFAULT_ARRAY_ITEM_VALUE_TYPE))


    private fun addProperty(
        stringBuilder: StringBuilder,
        property: String,
        type: String,
        value: String?,
        isLast: Boolean = false
    ) {

        val p = KProperty(property, type, value ?: "null")
        stringBuilder.apply {
            append(p.getPropertyStringBlock())
            val propertyComment = p.getPropertyComment()
            if (propertyComment.isNotBlank()) {
                append(" // ")
                append(getCommentCode(propertyComment))
            }
            append("\n")
        }
    }

}
