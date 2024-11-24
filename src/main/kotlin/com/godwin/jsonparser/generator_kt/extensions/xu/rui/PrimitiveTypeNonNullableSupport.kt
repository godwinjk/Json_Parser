package com.godwin.jsonparser.generator_kt.extensions.xu.rui

import com.godwin.jsonparser.generator_kt.extensions.Extension
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DefaultValueStrategy
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeelements.getDefaultValue
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jCheckBox
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.NULLABLE_PRIMITIVE_TYPES
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.getNonNullPrimitiveType
import javax.swing.JPanel

object PrimitiveTypeNonNullableSupport : Extension() {

    /**
     * Config key can't be private, as it will be accessed from `library` module
     */
    @Suppress("MemberVisibilityCanBePrivate")
    const val configKey = "xu.rui.force_primitive_type_non-nullable"


    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (getConfig(configKey).toBoolean().not()) {
            return kotlinClass
        }

        if (kotlinClass is DataClass) {

            val primitiveTypeNonNullableProperties = kotlinClass.properties.map {
                if (it.type in NULLABLE_PRIMITIVE_TYPES) {
                    val newType = getNonNullPrimitiveType(it.type)
                    if (KotlinConfigManager.defaultValueStrategy != DefaultValueStrategy.None) {
                        it.copy(type = newType, value = getDefaultValue(newType))
                    } else {
                        it.copy(type = newType)
                    }
                } else {
                    it
                }
            }

            return kotlinClass.copy(properties = primitiveTypeNonNullableProperties)
        } else {
            return kotlinClass
        }
    }

    override fun createUI(): JPanel {
        return jHorizontalLinearLayout {
            jCheckBox("Force Primitive Type Property Non-Nullable", getConfig(configKey).toBoolean(), { isSelected ->
                setConfig(
                    configKey, isSelected.toString()
                )
            })
            fillSpace()
        }
    }

}