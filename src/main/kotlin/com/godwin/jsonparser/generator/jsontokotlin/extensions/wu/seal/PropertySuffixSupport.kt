package com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal

import com.godwin.jsonparser.generator.common.ui.NamingConventionDocument
import com.godwin.jsonparser.generator.common.ui.addFocusLostListener
import com.godwin.jsonparser.generator.common.ui.jCheckBox
import com.godwin.jsonparser.generator.common.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator.common.ui.jTextInput
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import javax.swing.JPanel

object PropertySuffixSupport : com.godwin.jsonparser.generator.jsontokotlin.extensions.Extension() {

    /**
     * Config key can't be private, as it will be accessed from `library` module
     */
    @Suppress("MemberVisibilityCanBePrivate")
    const val suffixKeyEnable = "wu.seal.property_suffix_enable"

    @Suppress("MemberVisibilityCanBePrivate")
    const val suffixKey = "wu.seal.property_suffix"

    override fun createUI(): JPanel {

        val prefixJField = jTextInput(getConfig(suffixKey), getConfig(suffixKeyEnable).toBoolean()) {
            addFocusLostListener {
                if (getConfig(suffixKeyEnable).toBoolean()) {
                    setConfig(suffixKey, text)
                }
            }
            document = NamingConventionDocument(80)
        }

        return jHorizontalLinearLayout {
            jCheckBox("Suffix append after every property: ", getConfig(suffixKeyEnable).toBoolean(), { isSelected ->
                setConfig(suffixKeyEnable, isSelected.toString())
                prefixJField.isEnabled = isSelected
            })
            add(prefixJField)
        }
    }


    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        if (kotlinClass is DataClass) {

            return if (getConfig(suffixKeyEnable).toBoolean() && getConfig(suffixKey).isNotEmpty()) {
                val originProperties = kotlinClass.properties
                val newProperties = originProperties.map {
                    val suffix = getConfig(suffixKey)
                    if (it.name.isNotEmpty()) {
                        val newName = it.name + suffix.first().uppercaseChar() + suffix.substring(1)
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