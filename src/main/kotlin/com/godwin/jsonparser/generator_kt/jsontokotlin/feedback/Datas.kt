package com.godwin.jsonparser.generator_kt.jsontokotlin.feedback

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DefaultValueStrategy
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by Godwin on 2024/12/20
 */
data class ConfigInfo(
    val uuid: String = UUID,
    val pluginVersion: String = PLUGIN_VERSION,
    val isPropertiesVar: Boolean = KotlinConfigManager.isPropertiesVar,
    val isCommentOff: Boolean = KotlinConfigManager.isCommentOff,
    val isAppendOriginalJson: Boolean = KotlinConfigManager.isAppendOriginalJson,
    val isOrderByAlphabetical: Boolean = KotlinConfigManager.isOrderByAlphabetical,
    val propertyTypeStrategy: String = KotlinConfigManager.propertyTypeStrategy.name,
    val defaultValueStrategy: DefaultValueStrategy = KotlinConfigManager.defaultValueStrategy,
    val targetJsonConverterLib: String = KotlinConfigManager.targetJsonConverterLib.name,
    val isInnerClassMode: Boolean = KotlinConfigManager.isInnerClassModel,
    val customAnnotationImportClassString: String = KotlinConfigManager.customAnnotationClassImportdeclarationString,
    val customClassAnnotationFormatString: String = KotlinConfigManager.customClassAnnotationFormatString,
    val customPropertyAnnotationFormatString: String = KotlinConfigManager.customPropertyAnnotationFormatString,
    val enableMapType: Boolean = KotlinConfigManager.enableMapType,
    val enableMinimalAnnotation: Boolean = KotlinConfigManager.enableMinimalAnnotation,
    val autoDetectJsonScheme: Boolean = KotlinConfigManager.autoDetectJsonScheme,
    val parenClassTemplate: String = KotlinConfigManager.parenClassTemplate,
    val extensionsConfig: String = KotlinConfigManager.extensionsConfig,
    val timeStamp: String = Date().time.toString(),
    val daytime: String = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())

)
