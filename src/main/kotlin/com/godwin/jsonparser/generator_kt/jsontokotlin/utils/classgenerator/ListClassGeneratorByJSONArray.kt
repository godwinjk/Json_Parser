package com.godwin.jsonparser.generator_kt.jsontokotlin.utils.classgenerator

import com.godwin.jsonparser.generator.jsontodart.utils.LogUtil
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.ListClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.*
import com.google.gson.JsonArray

/**
 * Created by Godwin on 2024/12/20
 * Generate List Class from JsonArray String
 */
class ListClassGeneratorByJSONArray(private val className: String, jsonArrayString: String) {

    private val tag = "ListClassGeneratorByJSONArray"
    private val jsonArray: JsonArray = readJsonArray(jsonArrayString)
    private val jsonArrayExcludeNull = jsonArray.filterOutNullElement()
    private val hasNulls get() = jsonArray.size() != jsonArrayExcludeNull.size()

    fun generate(): ListClass {

        when {
            jsonArray.size() == 0 -> {
                LogUtil.i("$tag jsonArray size is 0, return ListClass with generic type ANY")
                return ListClass(name = className, generic = KotlinClass.ANY, nullableElements = true)
            }

            jsonArray.allItemAreNullElement() -> {
                LogUtil.i("$tag jsonArray allItemAreNullElement, return ListClass with generic type ${KotlinClass.ANY.name}")
                return ListClass(name = className, generic = KotlinClass.ANY, nullableElements = true)
            }

            jsonArrayExcludeNull.allElementAreSamePrimitiveType() -> {

                // if all elements are numbers, we need to select the larger scope of Kotlin types among the elements
                // e.g. [1,2,3.1] should return Double as it's type

                val p = jsonArrayExcludeNull[0].asJsonPrimitive
                val elementKotlinClass =
                    if (p.isNumber) getKotlinNumberClass(jsonArrayExcludeNull) else p.toKotlinClass()
                LogUtil.i("$tag jsonArray allElementAreSamePrimitiveType, return ListClass with generic type ${elementKotlinClass.name}")
                return ListClass(name = className, generic = elementKotlinClass, nullableElements = hasNulls)
            }

            jsonArrayExcludeNull.allItemAreObjectElement() -> {
                val fatJsonObject = jsonArrayExcludeNull.getFatJsonObject()
                val itemObjClassName = "${className}Item"
                val dataClassFromJsonObj = DataClassGeneratorByJSONObject(itemObjClassName, fatJsonObject).generate()
                LogUtil.i("$tag jsonArray allItemAreObjectElement, return ListClass with generic type ${dataClassFromJsonObj.name}")
                return ListClass(className, dataClassFromJsonObj, nullableElements = hasNulls)
            }

            jsonArrayExcludeNull.allItemAreArrayElement() -> {
                val fatJsonArray = jsonArrayExcludeNull.getFatJsonArray()
                val itemArrayClassName = "${className}SubList"
                val listClassFromFatJsonArray =
                    ListClassGeneratorByJSONArray(itemArrayClassName, fatJsonArray.toString()).generate()
                LogUtil.i("$tag jsonArray allItemAreArrayElement, return ListClass with generic type ${listClassFromFatJsonArray.name}")
                return ListClass(className, listClassFromFatJsonArray, nullableElements = hasNulls)
            }

            else -> {
                LogUtil.i("$tag jsonArray exception shouldn't come here, return ListClass with generic type ANY")
                return ListClass(name = className, generic = KotlinClass.ANY, nullableElements = true)
            }
        }
    }
}
