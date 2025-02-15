package com.godwin.jsonparser.generator_kt.extensions.yuan.varenyzc

import com.godwin.jsonparser.generator_kt.extensions.Extension
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.*
import javax.swing.JPanel

object NeedNonNullableClassesSupport : Extension() {

    const val prefixKeyEnable = "top.varenyzc.need_nonnullable_classes_enable"
    const val prefixKey = "top.varenyzc.need_nonnullable_classes"

    override fun createUI(): JPanel {

        val prefixJField = jTextInput(getConfig(prefixKey), getConfig(prefixKeyEnable).toBoolean()) {
            addFocusLostListener {
                if (getConfig(prefixKeyEnable).toBoolean()) {
                    setConfig(prefixKey, text)
                }
            }
        }

        return jHorizontalLinearLayout {
            jCheckBox("", getConfig(prefixKeyEnable).toBoolean(), { isSelected ->
                setConfig(prefixKeyEnable, isSelected.toString())
                prefixJField.isEnabled = isSelected
            })
            jLabel(
                text = "Classes non-nullable: "
            )
            add(prefixJField)
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        return if (kotlinClass is DataClass) {
            if (getConfig(prefixKeyEnable).toBoolean()) {
                val list = getConfig(prefixKey).split(',')
                if (list.isEmpty()) {
                    return kotlinClass
                }
                val originProperties = kotlinClass.properties
                val newProperties = originProperties.map {
                    val oldType = it.type
                    if (oldType.isNotEmpty()) {
                        val newType = if (oldType.contains("List<")) {
                            val innerType = oldType.substring(oldType.indexOf('<') + 1, oldType.indexOf('>'))
                            when {
                                list.contains(innerType) -> {
                                    "List<$innerType>?"
                                }

                                list.contains("List") -> {
                                    "List<$innerType?>"
                                }

                                else -> {
                                    "List<$innerType?>?"
                                }
                            }
                        } else {
                            if (list.contains(oldType.replace("?", ""))) {
                                oldType.replace("?", "")
                            } else {
                                "${oldType}?"
                            }
                        }
                        it.copy(type = newType)
                    } else {
                        it
                    }
                }
                kotlinClass.copy(properties = newProperties)
            } else {
                kotlinClass
            }
        } else {
            kotlinClass
        }
    }
}