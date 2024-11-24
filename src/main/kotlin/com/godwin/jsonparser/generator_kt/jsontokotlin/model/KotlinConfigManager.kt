package com.godwin.jsonparser.generator_kt.jsontokotlin.model

import com.godwin.jsonparser.generator_kt.jsontokotlin.test.TestConfig
import com.godwin.jsonparser.generator_kt.jsontokotlin.test.TestConfig.isTestModel
import com.intellij.ide.util.PropertiesComponent

/**
 * Config Manager
 * Created by Seal.Wu on 2018/2/7.
 */
object KotlinConfigManager : IConfigManager {

    private const val INDENT_KEY = "jsonParser_kt_indent-space-number"
    private const val ENABLE_MAP_TYP_KEY = "jsonParser_kt_enable-map-type"
    private const val ENABLE_MINIMAL_ANNOTATION = "jsonParser_kt_enable-minimal-annotation"
    private const val PARENT_CLASS_TEMPLATE = "jsonParser_kt_parent-class-template"
    private const val KEYWORD_PROPERTY_EXTENSIONS_CONFIG = "jsonParser_kt_keyword-extensions-config"
    private const val AUTO_DETECT_JSON_SCHEMA = "jsonParser_kt_auto-detect-json-schema"
    private const val PARENT_CLASS_TEMPLATE_DART = "jsonParser_kt_parent-class-template"
    private const val IS_PROPERTIES_VAR_KEY = "jsonParser_kt_isPropertiesVar_key"
    private const val TARGET_JSON_CONVERTER_LIB_KEY = "jsonParser_kt_target_json_converter_lib_key"
    private const val IS_COMMENT_OFF = "jsonParser_kt_need_comment_key"
    private const val IS_APPEND_ORIGINAL_JSON = "jsonParser_kt_is_append_original_json"
    private const val IS_ORDER_BY_ALPHABETICAL = "jsonParser_kt_is_order_by_alphabetical"
    private const val PROPERTY_TYPE_STRATEGY_KEY = "jsonParser_kt_is_property_property_type_strategy_key"
    private const val INIT_WITH_DEFAULT_VALUE_KEY = "jsonParser_kt_init_with_default_value_key"
    private const val DEFAULT_VALUE_STRATEGY_KEY = "jsonParser_kt_default_value_strategy_key"
    private const val USER_UUID_KEY = "jsonParser_kt_user_uuid_value_key"
    private const val USER_CUSTOM_JSON_LIB_ANNOTATION_IMPORT_CLASS =
        "jsonParser_kt_user_custom_json_lib_annotation_import_class"
    private const val USER_CUSTOM_JSON_LIB_ANNOTATION_FORMAT_STRING =
        "jsonParser_kt_user_custom_json_lib_annotation_format_string"
    private const val USER_CUSTOM_JSON_LIB_CLASS_ANNOTATION_FORMAT_STRING =
        "jsonParser_kt_user_custom_json_lib_class_annotation_format_string"
    private const val INNER_CLASS_MODEL_KEY = "jsonParser_kt_inner_class_model_key"

    var indent: Int
        get() = if (TestConfig.isTestModel) TestConfig.indent else PropertiesComponent.getInstance().getInt(
            INDENT_KEY, 4
        )
        set(value) = if (TestConfig.isTestModel) {
            TestConfig.indent = value
        } else PropertiesComponent.getInstance().setValue(INDENT_KEY, value, 4)

    var enableMapType: Boolean
        get() = if (TestConfig.isTestModel) TestConfig.enableMapType else PropertiesComponent.getInstance().getBoolean(
            ENABLE_MAP_TYP_KEY, false
        )
        set(value) = if (TestConfig.isTestModel) {
            TestConfig.enableMapType = value
        } else PropertiesComponent.getInstance().setValue(ENABLE_MAP_TYP_KEY, value, false)

    var enableMinimalAnnotation: Boolean
        get() = if (TestConfig.isTestModel) {
            TestConfig.enableMinimalAnnotation
        } else {
            PropertiesComponent.getInstance().getBoolean(ENABLE_MINIMAL_ANNOTATION, false)
        }
        set(value) {
            if (TestConfig.isTestModel) {
                TestConfig.enableMinimalAnnotation = value
            } else {
                PropertiesComponent.getInstance().setValue(ENABLE_MINIMAL_ANNOTATION, value, false)
            }
        }

    var autoDetectJsonScheme: Boolean
        get() = if (TestConfig.isTestModel) {
            TestConfig.autoDetectJsonScheme
        } else {
            PropertiesComponent.getInstance().getBoolean(AUTO_DETECT_JSON_SCHEMA, true)
        }
        set(value) {
            if (TestConfig.isTestModel) {
                TestConfig.autoDetectJsonScheme = value
            } else {
                PropertiesComponent.getInstance().setValue(AUTO_DETECT_JSON_SCHEMA, value, true)
            }
        }

    var parenClassTemplate: String
        get() = if (TestConfig.isTestModel) {
            TestConfig.parenClassTemplate
        } else {
            PropertiesComponent.getInstance().getValue(PARENT_CLASS_TEMPLATE, "")
        }
        set(value) {
            if (TestConfig.isTestModel) {
                TestConfig.parenClassTemplate = value
            } else {
                PropertiesComponent.getInstance().setValue(PARENT_CLASS_TEMPLATE, value, "")
            }
        }
    var parenClassTemplateDart: String
        get() = if (TestConfig.isTestModel) {
            TestConfig.parenClassTemplate
        } else {
            PropertiesComponent.getInstance().getValue(PARENT_CLASS_TEMPLATE_DART, "")
        }
        set(value) {
            if (TestConfig.isTestModel) {
                TestConfig.parenClassTemplate = value
            } else {
                PropertiesComponent.getInstance().setValue(PARENT_CLASS_TEMPLATE_DART, value, "")
            }
        }

    var extensionsConfig: String
        get() = if (TestConfig.isTestModel) {
            TestConfig.extensionsConfig
        } else {
            PropertiesComponent.getInstance().getValue(KEYWORD_PROPERTY_EXTENSIONS_CONFIG, "")
        }
        set(value) {
            if (TestConfig.isTestModel) {
                TestConfig.extensionsConfig = value
            } else {
                PropertiesComponent.getInstance().setValue(KEYWORD_PROPERTY_EXTENSIONS_CONFIG, value, "")
            }
        }

    var isPropertiesVar: Boolean
        get() = if (isTestModel) TestConfig.isPropertiesVar else PropertiesComponent.getInstance().isTrueValue(
            IS_PROPERTIES_VAR_KEY
        )
        set(value) = if (isTestModel) {
            TestConfig.isPropertiesVar = value
        } else {
            PropertiesComponent.getInstance().setValue(IS_PROPERTIES_VAR_KEY, value)
        }

    var isAppendOriginalJson: Boolean
        get() = if (isTestModel) TestConfig.isAppendOriginalJson else PropertiesComponent.getInstance().getBoolean(
            IS_APPEND_ORIGINAL_JSON, false
        )
        set(value) = if (isTestModel) {
            TestConfig.isAppendOriginalJson = value
        } else {
            PropertiesComponent.getInstance().setValue(IS_APPEND_ORIGINAL_JSON, value)
        }

    var isCommentOff: Boolean
        get() = if (isTestModel) TestConfig.isCommentOff else PropertiesComponent.getInstance().getBoolean(
            IS_COMMENT_OFF, true
        )
        set(value) = if (isTestModel) {
            TestConfig.isCommentOff = value
        } else {
            PropertiesComponent.getInstance().setValue(IS_COMMENT_OFF, value, true)
        }

    var isOrderByAlphabetical: Boolean
        get() = if (isTestModel) TestConfig.isOrderByAlphabetical else PropertiesComponent.getInstance().getBoolean(
            IS_ORDER_BY_ALPHABETICAL, true
        )
        set(value) = if (isTestModel) {
            TestConfig.isOrderByAlphabetical = value
        } else {
            PropertiesComponent.getInstance().setValue(IS_ORDER_BY_ALPHABETICAL, value, true)
        }

    var targetJsonConverterLib: TargetJsonConverter
        get() = if (isTestModel) TestConfig.targetJsonConvertLib else {
            val value = PropertiesComponent.getInstance().getValue(TARGET_JSON_CONVERTER_LIB_KEY)
            //Next step try to keep compatible with 3.5.1 and before version of plugin,
            //Please see : https://github.com/wuseal/JsonToKotlinClass/issues/284
            val compatibleValue = if (value == "Serilizable") "Serializable" else value
            try {
                TargetJsonConverter.valueOf(
                    compatibleValue ?: TargetJsonConverter.None.name
                )
            } catch (e: Exception) {
                TargetJsonConverter.None
            }
        }
        set(value) = if (isTestModel) {
            TestConfig.targetJsonConvertLib = value
        } else {
            PropertiesComponent.getInstance().setValue(TARGET_JSON_CONVERTER_LIB_KEY, value.name)
        }

    var propertyTypeStrategy: PropertyTypeStrategy
        get() = if (isTestModel) TestConfig.propertyTypeStrategy else PropertyTypeStrategy.valueOf(
            PropertiesComponent.getInstance().getValue(
                PROPERTY_TYPE_STRATEGY_KEY, PropertyTypeStrategy.NotNullable.name
            )
        )
        set(value) = if (isTestModel) {
            TestConfig.propertyTypeStrategy = value
        } else PropertiesComponent.getInstance().setValue(PROPERTY_TYPE_STRATEGY_KEY, value.name)

    var defaultValueStrategy: DefaultValueStrategy
        get() = when {
            isTestModel -> TestConfig.defaultValueStrategy
            // set defaultValueStrategy = AvoidNull when 'init with default value' was selected in version before 3.3.0
            PropertiesComponent.getInstance().getBoolean(INIT_WITH_DEFAULT_VALUE_KEY, false) -> {
                PropertiesComponent.getInstance().unsetValue(INIT_WITH_DEFAULT_VALUE_KEY)
                DefaultValueStrategy.AvoidNull.also {
                    PropertiesComponent.getInstance().setValue(DEFAULT_VALUE_STRATEGY_KEY, it.name)
                }
            }

            else -> DefaultValueStrategy.valueOf(
                PropertiesComponent.getInstance().getValue(DEFAULT_VALUE_STRATEGY_KEY) ?: DefaultValueStrategy.None.name
            )
        }
        set(value) = if (isTestModel) {
            TestConfig.defaultValueStrategy = value
        } else {
            PropertiesComponent.getInstance().setValue(DEFAULT_VALUE_STRATEGY_KEY, value.name)
        }

    var userUUID: String
        get() = if (isTestModel) "" else PropertiesComponent.getInstance().getValue(USER_UUID_KEY, "")
        set(value) = if (isTestModel) {
        } else {
            PropertiesComponent.getInstance().setValue(USER_UUID_KEY, value)
        }

    var customAnnotationClassImportdeclarationString: String
        get() = if (isTestModel) TestConfig.customAnnotaionImportClassString else PropertiesComponent.getInstance()
            .getValue(
                USER_CUSTOM_JSON_LIB_ANNOTATION_IMPORT_CLASS,
                "import kotlinx.serialization.SerialName\n" + "import kotlinx.serialization.Serializable"
            )
        set(value) = if (isTestModel) {
            TestConfig.customAnnotaionImportClassString = value
        } else {
            PropertiesComponent.getInstance().setValue(USER_CUSTOM_JSON_LIB_ANNOTATION_IMPORT_CLASS, value)
        }

    var customPropertyAnnotationFormatString: String
        get() = if (isTestModel) TestConfig.customPropertyAnnotationFormatString else PropertiesComponent.getInstance()
            .getValue(
                USER_CUSTOM_JSON_LIB_ANNOTATION_FORMAT_STRING, "@SerialName(\"%s\")"
            )
        set(value) = if (isTestModel) {
            TestConfig.customPropertyAnnotationFormatString = value
        } else {
            PropertiesComponent.getInstance().setValue(USER_CUSTOM_JSON_LIB_ANNOTATION_FORMAT_STRING, value)
        }

    var customClassAnnotationFormatString: String
        get() = if (isTestModel) TestConfig.customClassAnnotationFormatString else PropertiesComponent.getInstance()
            .getValue(
                USER_CUSTOM_JSON_LIB_CLASS_ANNOTATION_FORMAT_STRING, "@Serializable"
            )
        set(value) = if (isTestModel) {
            TestConfig.customClassAnnotationFormatString = value
        } else {
            PropertiesComponent.getInstance().setValue(USER_CUSTOM_JSON_LIB_CLASS_ANNOTATION_FORMAT_STRING, value)
        }

    var isInnerClassModel: Boolean
        get() = if (isTestModel) TestConfig.isNestedClassModel else PropertiesComponent.getInstance().getBoolean(
            INNER_CLASS_MODEL_KEY, false
        )
        set(value) = if (isTestModel) {
            TestConfig.isNestedClassModel = value
        } else {
            PropertiesComponent.getInstance().setValue(INNER_CLASS_MODEL_KEY, value)
        }
}