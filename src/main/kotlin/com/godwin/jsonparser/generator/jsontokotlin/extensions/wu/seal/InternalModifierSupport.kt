package com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal

import com.godwin.jsonparser.generator.common.ui.jCheckBox
import com.godwin.jsonparser.generator.common.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import javax.swing.JPanel

object InternalModifierSupport : com.godwin.jsonparser.generator.jsontokotlin.extensions.Extension() {

    const val CONFIG_KEY = "wu.seal.internal_modifier_support"

    override fun createUI(): JPanel {

        return jHorizontalLinearLayout {
            jCheckBox(
                "Let classes to be internal",
                getConfig(CONFIG_KEY).toBoolean(),
                { isSelected -> setConfig(CONFIG_KEY, isSelected.toString()) })
            fillSpace()
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        if (getConfig(CONFIG_KEY).toBoolean())
            if (kotlinClass is DataClass) {
                return kotlinClass.copy(
                    codeBuilder = DataClassCodeBuilderForInternalClass(
                        kotlinClass.codeBuilder
                    )
                )
            }
        return kotlinClass
    }
}