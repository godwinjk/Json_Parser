package com.godwin.jsonparser.generator_kt.jsontokotlin.utils.classgenerator

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Property
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.*
import com.google.gson.JsonObject

/**
 * Created by Godwin on 2024/12/20
 * Description: Generate Kotlin Data class Struct from JSON Object
 */
class DataClassGeneratorByJSONObject(private val className: String, private val jsonObject: JsonObject) {

    fun generate(isTop: Boolean = false): DataClass {
        if (maybeJsonObjectBeMapType(jsonObject) && KotlinConfigManager.enableMapType) {
            throw IllegalArgumentException("Can't generate data class from a Map type JSONObjcet when enable Map Type : $jsonObject")
        }
        val properties = mutableListOf<Property>()

        jsonObject.entrySet().forEach { (jsonKey, jsonValue) ->
            when {
                jsonValue.isJsonNull -> {
                    val jsonValueNullProperty =
                        Property(
                            originName = jsonKey,
                            originJsonValue = null,
                            type = KotlinClass.ANY.name,
                            comment = "null",
                            typeObject = KotlinClass.ANY
                        )
                    properties.add(jsonValueNullProperty)
                }

                jsonValue.isJsonPrimitive -> {
                    val type = jsonValue.asJsonPrimitive.toKotlinClass()
                    val jsonValuePrimitiveProperty =
                        Property(
                            originName = jsonKey,
                            originJsonValue = jsonValue.asString,
                            type = type.name,
                            comment = jsonValue.asString,
                            typeObject = type
                        )
                    properties.add(jsonValuePrimitiveProperty)
                }

                jsonValue.isJsonArray -> {
                    val arrayType = GenericListClassGeneratorByJSONArray(jsonKey, jsonValue.toString()).generate()
                    val jsonValueArrayProperty =
                        Property(originName = jsonKey, value = "", type = arrayType.name, typeObject = arrayType)
                    properties.add(jsonValueArrayProperty)
                }

                jsonValue.isJsonObject -> {
                    jsonValue.asJsonObject.run {
                        if (KotlinConfigManager.enableMapType && maybeJsonObjectBeMapType(this)) {
                            var refDataClass: DataClass? = null
                            val mapKeyType = getMapKeyTypeConvertFromJsonObject(this)
                            val mapValueType = getMapValueTypeConvertFromJsonObject(this)
                            if (mapValueIsObjectType(mapValueType)) {
                                val targetJsonElement =
                                    TargetJsonElement(entrySet().first().value).getTargetJsonElementForGeneratingCode()
                                if (targetJsonElement.isJsonObject) {
                                    refDataClass = DataClassGeneratorByJSONObject(
                                        getChildType(mapValueType),
                                        targetJsonElement.asJsonObject
                                    ).generate()
                                } else {
                                    throw IllegalStateException("Don't support No JSON Object Type for Generate Kotlin Data Class")
                                }
                            }
                            val mapType = "Map<$mapKeyType,$mapValueType>"
                            val jsonValueObjectMapTypeProperty = Property(
                                originName = jsonKey,
                                originJsonValue = "",
                                type = mapType,
                                typeObject = refDataClass ?: KotlinClass.ANY
                            )
                            properties.add(jsonValueObjectMapTypeProperty)
                        } else {
                            var refDataClass: DataClass? = null
                            val type = getJsonObjectType(jsonKey)
                            val targetJsonElement =
                                TargetJsonElement(this).getTargetJsonElementForGeneratingCode()
                            if (targetJsonElement.isJsonObject) {
                                refDataClass = DataClassGeneratorByJSONObject(
                                    getRawType(type),
                                    targetJsonElement.asJsonObject
                                ).generate()

                            } else {
                                throw IllegalStateException("Don't support No JSON Object Type for Generate Kotlin Data Class")
                            }

                            val jsonValueObjectProperty = Property(
                                originName = jsonKey,
                                originJsonValue = "",
                                type = type,
                                typeObject = refDataClass
                            )
                            properties.add(jsonValueObjectProperty)
                        }
                    }
                }
            }
        }

        val propertiesAfterConsumeBackStageProperties = properties.consumeBackstageProperties()
        return DataClass(name = className, properties = propertiesAfterConsumeBackStageProperties, isTop = isTop)
    }

    private fun mapValueIsObjectType(mapValueType: String) = (mapValueType == MAP_DEFAULT_OBJECT_VALUE_TYPE
            || mapValueType.contains(MAP_DEFAULT_ARRAY_ITEM_VALUE_TYPE))

    /**
     * Consume the properties whose name end with [BACKSTAGE_NULLABLE_POSTFIX],
     * After call this method, all properties's name end with [BACKSTAGE_NULLABLE_POSTFIX] will be removed
     * And the corresponding properies whose name without 【BACKSTAGE_NULLABLE_POSTFIX】it's value will be set
     * to null,for example:
     * remove property -> name = demoProperty__&^#
     * set null value  -> demoProperty.value = null
     */
    private fun List<Property>.consumeBackstageProperties(): List<Property> {
        val newProperties = mutableListOf<Property>()
        val nullableBackstagePropertiesNames = filter { it.name.endsWith(BACKSTAGE_NULLABLE_POSTFIX) }.map { it.name }
        val nullablePropertiesNames =
            nullableBackstagePropertiesNames.map { it.removeSuffix(BACKSTAGE_NULLABLE_POSTFIX) }
        forEach {
            when {
                nullablePropertiesNames.contains(it.name) -> newProperties.add(
                    it.copy(
                        originJsonValue = null,
                        value = ""
                    )
                )

                nullableBackstagePropertiesNames.contains(it.name) -> {
                    //when hit the backstage property just continue, don't add it to new properties
                }

                else -> newProperties.add(it)
            }
        }
        return newProperties
    }
}


