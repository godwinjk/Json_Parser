package com.godwin.jsonparser.generator_kt.jsontokotlin.test

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.gson.AddGsonAnnotationClassImportDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DefaultValueStrategy
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.PropertyTypeStrategy
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.TargetJsonConverter
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeannotations.GsonPropertyAnnotationTemplate

/**
 *
 * Created by Godwin on 2024/12/20
 */
/** 
 * config for test unit
 */
object TestConfig {
    /**
     * If it is in test model
     */
    var isTestModel = false
    var isAppendOriginalJson = false
    var isCommentOff = false
    var isOrderByAlphabetical = false
    var isPropertiesVar = false
    var targetJsonConvertLib = TargetJsonConverter.Gson
    var propertyTypeStrategy = PropertyTypeStrategy.NotNullable
    var defaultValueStrategy = DefaultValueStrategy.AvoidNull
    var isNestedClassModel = true

    var customPropertyAnnotationFormatString = "@SerialName(\"%s\")"
    var customAnnotaionImportClassString = "import kotlinx.serialization.SerialName\n" +
            "import kotlinx.serialization.Serializable"

    var customClassAnnotationFormatString = "@Serializable"

    var indent: Int = 4

    var enableMapType: Boolean = true

    var enableMinimalAnnotation = false

    var parenClassTemplate = ""

    var isKeywordPropertyValid = true

    var extensionsConfig = ""

    var autoDetectJsonScheme = true

    private var state = State()

    fun setToTestInitState() {
        isTestModel = true
        isCommentOff = false
        isOrderByAlphabetical = true
        isPropertiesVar = false
        targetJsonConvertLib = TargetJsonConverter.Gson
        propertyTypeStrategy = PropertyTypeStrategy.NotNullable
        defaultValueStrategy = DefaultValueStrategy.AvoidNull
        isNestedClassModel = true
        customPropertyAnnotationFormatString = "@SerialName(\"%s\")"
        customAnnotaionImportClassString = "import kotlinx.serialization.SerialName\n" +
                "import kotlinx.serialization.Serializable"

        customClassAnnotationFormatString = "@Serializable"

        enableMinimalAnnotation = false

        indent = 4

        parenClassTemplate = ""

        isKeywordPropertyValid = true

        extensionsConfig = ""

        autoDetectJsonScheme = true
    }

    fun setToTestInitStateForJsonSchema() {
        isTestModel = true
        isCommentOff = false
        isOrderByAlphabetical = false
        isPropertiesVar = false
        targetJsonConvertLib = TargetJsonConverter.None
        propertyTypeStrategy = PropertyTypeStrategy.AutoDeterMineNullableOrNot
        defaultValueStrategy = DefaultValueStrategy.None
        isNestedClassModel = false
        customPropertyAnnotationFormatString = "@SerialName(\"%s\")"
        customAnnotaionImportClassString = "import kotlinx.serialization.SerialName\n" +
                "import kotlinx.serialization.Serializable"

        customClassAnnotationFormatString = "@Serializable"

        enableMinimalAnnotation = false

        indent = 4

        parenClassTemplate = ""

        isKeywordPropertyValid = true

        extensionsConfig = ""

        autoDetectJsonScheme = true
    }


    fun saveState() {
        val newState = State()
        newState.isTestModel = isTestModel
        newState.isCommentOff = isCommentOff
        newState.isOrderByAlphabetical = isOrderByAlphabetical
        newState.isPropertiesVar = isPropertiesVar
        newState.targetJsonConvertLib = targetJsonConvertLib
        newState.propertyTypeStrategy = propertyTypeStrategy
        newState.defaultValueStrategy = defaultValueStrategy
        newState.isNestedClassModel = isNestedClassModel

        newState.customPropertyAnnotationFormatString = customPropertyAnnotationFormatString
        newState.customClassAnnotationFormatString = customClassAnnotationFormatString
        newState.customAnnotationImportClassString = customAnnotaionImportClassString
        newState.enableMinimalAnnotation = enableMinimalAnnotation
        newState.parenClassTemplate = parenClassTemplate
        newState.isKeywordPropertyValid = isKeywordPropertyValid
        newState.extensionsConfig = extensionsConfig
        newState.autoDetectJsonScheme = autoDetectJsonScheme
        state = newState
    }

    fun restoreState() {
        isTestModel = state.isTestModel
        isCommentOff = state.isCommentOff
        isOrderByAlphabetical = state.isOrderByAlphabetical
        isPropertiesVar = state.isPropertiesVar
        targetJsonConvertLib = state.targetJsonConvertLib
        propertyTypeStrategy = state.propertyTypeStrategy
        defaultValueStrategy = state.defaultValueStrategy
        isNestedClassModel = state.isNestedClassModel
        customPropertyAnnotationFormatString = state.customPropertyAnnotationFormatString
        customClassAnnotationFormatString = state.customClassAnnotationFormatString
        customAnnotaionImportClassString = state.customAnnotationImportClassString
        enableMinimalAnnotation = state.enableMinimalAnnotation
        parenClassTemplate = state.parenClassTemplate
        isKeywordPropertyValid = state.isKeywordPropertyValid
        extensionsConfig = state.extensionsConfig
        autoDetectJsonScheme = state.autoDetectJsonScheme
    }

    class State {
        var isTestModel = false
        var isCommentOff = false
        var isOrderByAlphabetical = true
        var isPropertiesVar = false
        var targetJsonConvertLib = TargetJsonConverter.Gson
        var propertyTypeStrategy = PropertyTypeStrategy.NotNullable
        var defaultValueStrategy = DefaultValueStrategy.AvoidNull
        var isNestedClassModel = true

        var customPropertyAnnotationFormatString = GsonPropertyAnnotationTemplate.propertyAnnotationFormat
        var customClassAnnotationFormatString = ""
        var customAnnotationImportClassString =
            AddGsonAnnotationClassImportDeclarationInterceptor.propertyAnnotationImportClassString

        var indent: Int = 4

        var enableMapType: Boolean = true

        var enableMinimalAnnotation = false

        var parenClassTemplate = ""

        var isKeywordPropertyValid = true

        var extensionsConfig = ""

        var autoDetectJsonScheme = true

    }
}
