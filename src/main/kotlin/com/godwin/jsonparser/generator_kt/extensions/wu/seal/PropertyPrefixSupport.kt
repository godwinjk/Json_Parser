package com.godwin.jsonparser.generator_kt.extensions.wu.seal

import com.godwin.jsonparser.generator_kt.extensions.Extension
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.*
import javax.swing.JPanel

object PropertyPrefixSupport : Extension() {

    const val prefixKeyEnable = "wu.seal.property_prefix_enable"
    const val prefixKey = "wu.seal.property_prefix"

    override fun createUI(): JPanel {
        val prefixJField = jTextInput(getConfig(prefixKey), getConfig(prefixKeyEnable).toBoolean()) {
            addFocusLostListener {
                if (getConfig(prefixKeyEnable).toBoolean()) {
                    setConfig(prefixKey, text)
                }
            }
            document = NamingConventionDocument(80)
        }

        return jHorizontalLinearLayout {
            jCheckBox("Prefix append before every property: ", getConfig(prefixKeyEnable).toBoolean(), { isSelected ->
                setConfig(prefixKeyEnable, isSelected.toString())
                prefixJField.isEnabled = isSelected
            })
            add(prefixJField)
        }
    }


    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {

            return if (getConfig(prefixKeyEnable).toBoolean() && getConfig(prefixKey).isNotEmpty()) {
                val originProperties = kotlinClass.properties
                val newProperties = originProperties.map {
                    val prefix = getConfig(prefixKey)
                    if (it.name.isNotEmpty()) {
                        val newName = prefix + it.name.first().uppercaseChar() + it.name.substring(1)
                        it.copy(name = newName)
                    } else it
                }
                kotlinClass.copy(properties = newProperties)
            } else {
                kotlinClass
            }
        } else {
            return kotlinClass
        }

    }
}