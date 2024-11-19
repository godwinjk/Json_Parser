package com.godwin.jsonparser.generator_kt.jsontokotlin.utils

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.ConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.jsonschema.JsonSchema
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.classgenerator.DataClassGeneratorByJSONObject
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.classgenerator.DataClassGeneratorByJSONSchema
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.classgenerator.ListClassGeneratorByJSONArray
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class KotlinClassMaker(private val rootClassName: String, private val json: String) {

    fun makeKotlinClass(): KotlinClass {
        return if (ConfigManager.autoDetectJsonScheme && json.isJSONSchema()) {
            val jsonSchema = Gson().fromJson(json, JsonSchema::class.java)
            DataClassGeneratorByJSONSchema(rootClassName, jsonSchema).generate()
        } else {
            when {
                json.isJSONObject() -> DataClassGeneratorByJSONObject(rootClassName, Gson().fromJson(json, JsonObject::class.java)).generate(isTop = true)
                json.isJSONArray() -> ListClassGeneratorByJSONArray(rootClassName, json).generate()
                else -> throw IllegalStateException("Can't generate Kotlin Data Class from a no JSON Object/JSON Object Array")
            }
        }
    }

    private fun String.isJSONObject(): Boolean {
        val jsonElement = Gson().fromJson(this, JsonElement::class.java)
        return jsonElement.isJsonObject
    }

    private fun String.isJSONArray(): Boolean {
        val jsonElement = Gson().fromJson(this, JsonElement::class.java)
        return jsonElement.isJsonArray
    }
}


