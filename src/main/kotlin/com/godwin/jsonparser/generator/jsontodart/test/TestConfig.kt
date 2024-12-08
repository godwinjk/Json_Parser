package com.godwin.jsonparser.generator.jsontodart.test

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DefaultValueStrategy
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.TargetJsonConverter

/**
 *
 * Created by Godwin on 2024/12/20.
 */
/**
 * config for test unit
 */
object TestConfig {
    /**
     * If it is in test model
     */
    var isTestModel = false
    var isCommentOff = false
    var isOrderByAlphabetical = false
    var isPropertiesFinal = false
    var targetJsonConvertLib = TargetJsonConverter.None
    var defaultValueStrategy = DefaultValueStrategy.AvoidNull

    var customPropertyAnnotationFormatString = "@Optional\n@SerialName(\"%s\")"
    var customAnnotaionImportClassString = "import kotlinx.serialization.SerialName\n" +
            "import kotlinx.serialization.Serializable" + "\n" + "import kotlinx.serialization.Optional"

    var customClassAnnotationFormatString = "@Serializable"

    var indent: Int = 4

    var enableMapType: Boolean = true

    var enableMinimalAnnotation = false

    var parenClassTemplate = ""

    var isKeywordPropertyValid = true

    var extensionsConfig = ""
}
