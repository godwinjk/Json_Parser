package com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal

import com.godwin.jsonparser.generator.common.ui.jCheckBox
import com.godwin.jsonparser.generator.common.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator.jsontokotlin.model.codeelements.getDefaultValue
import com.godwin.jsonparser.generator.jsontokotlin.utils.TYPE_STRING
import javax.swing.JPanel

/**
 * This extension make the default value of data class property to be json value
 * This extension should be put at last
 * Created by Godwin on 2024/12/20
 */
object ForceInitDefaultValueWithOriginJsonValueSupport :
    com.godwin.jsonparser.generator.jsontokotlin.extensions.Extension() {

    /**
     * Config key can't be private, as it will be accessed from `library` module
     */
    @Suppress("MemberVisibilityCanBePrivate")
    const val configKey = "wu.seal.force_init_default_value_with_origin_json_value"

    override fun createUI(): JPanel {
        return jHorizontalLinearLayout {
            jCheckBox(
                "Force init Default Value With Origin Json Value",
                getConfig(configKey).toBoolean(),
                { isSelected ->
                    setConfig(
                        configKey, isSelected.toString()
                    )
                })
            fillSpace()
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {

            return if (getConfig(configKey).toBoolean()) {
                val newP = kotlinClass.properties.map {
                    val newV = if (it.originJsonValue.isNullOrBlank()) getDefaultValue(it.type) else {
                        if (it.type == TYPE_STRING) {
                            "\"\"\"" + it.originJsonValue.replace("$", "\${\"\$\"}") + "\"\"\""
                        } else {
                            it.originJsonValue
                        }
                    }
                    it.copy(value = newV)
                }
                kotlinClass.copy(properties = newP)
            } else {
                kotlinClass
            }
        } else {
            return kotlinClass
        }
    }
}