package com.godwin.jsonparser.generator.extensions

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass
import com.godwin.jsonparser.generator.jsontodart.utils.getChildType
import com.godwin.jsonparser.generator.jsontodart.utils.getRawType
import com.intellij.ui.layout.panel
import com.intellij.util.ui.JBDimension
import com.intellij.util.ui.JBEmptyBorder
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextField

object ClassNameSuffixSupport : Extension() {

    private const val suffixKeyEnable = "wu.seal.class_name_suffix_enable"
    private const val suffixKey = "wu.seal.class_name_suffix"

    override fun createUI(): JPanel {
        val prefixJField = JTextField().apply {
            text = getConfig(suffixKey)

            addFocusListener(object : FocusListener {
                override fun focusGained(e: FocusEvent?) {
                }

                override fun focusLost(e: FocusEvent?) {
                    if (getConfig(suffixKeyEnable).toBoolean()) {
                        setConfig(suffixKey, text)
                    }
                }
            })

            minimumSize = JBDimension(150, 25)

            isEnabled = getConfig(suffixKeyEnable).toBoolean()

        }

        val checkBox = JCheckBox("Suffix append after every class name: ").apply {
            isSelected = getConfig(suffixKeyEnable).toBoolean()
            addActionListener {
                setConfig(suffixKeyEnable, isSelected.toString())
                prefixJField.isEnabled = isSelected
            }
        }

        return panel {
            row {
                checkBox()
                prefixJField()
            }
        }.apply {
            border = JBEmptyBorder(6, 0, 0, 0)
        }
    }


    override fun intercept(dartClass: DartClass): DartClass {

        val suffix = getConfig(suffixKey)

        return if (getConfig(suffixKeyEnable).toBoolean() && suffix.isNotEmpty()) {
            val standTypes = listOf("Int", "Double", "Long", "String", "Boolean")
            val originName = dartClass.name
            val newPropertyTypes =
                dartClass.properties.map {
                    val rawSubType = getChildType(getRawType(it.type))
                    when {
                        it.type.isMapType() -> {
                            it.type//currently don't support map type
                        }

                        standTypes.contains(rawSubType) -> it.type
                        else -> it.type.replace(rawSubType, rawSubType + suffix)
                    }
                }

            val newPropertyDefaultValues = dartClass.properties.map {
                val rawSubType = getChildType(getRawType(it.type))
                when {
                    it.value.isEmpty() -> it.value
                    it.type.isMapType() -> {
                        it.value//currently don't support map type
                    }

                    standTypes.contains(rawSubType) -> it.value
                    else -> it.value.replace(rawSubType, rawSubType + suffix)
                }
            }

            val newProperties = dartClass.properties.mapIndexed { index, property ->

                val newType = newPropertyTypes[index]

                val newValue = newPropertyDefaultValues[index]

                property.copy(type = newType, value = newValue)
            }


            dartClass.copy(name = originName + suffix, properties = newProperties)

        } else {
            dartClass
        }

    }

    private fun String.isMapType(): Boolean {

        return matches(Regex("Map<.+,.+>"))
    }


}
