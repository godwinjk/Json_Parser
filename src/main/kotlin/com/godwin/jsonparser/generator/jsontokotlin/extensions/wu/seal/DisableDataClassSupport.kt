package com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal

import com.godwin.jsonparser.generator.common.ui.jCheckBox
import com.godwin.jsonparser.generator.common.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import javax.swing.JPanel

/**
 * Extension support disable kotlin data class, after enable this, all kotlin data classes will remove 'data' modifier
 */
object DisableDataClassSupport : com.godwin.jsonparser.generator.jsontokotlin.extensions.Extension() {

    const val configKey = "wu.seal.disable_data_class_support"

    override fun createUI(): JPanel {

        return jHorizontalLinearLayout {
            jCheckBox(
                "Disable Kotlin Data Class",
                getConfig(configKey).toBoolean(),
                { isSelected -> setConfig(configKey, isSelected.toString()) })
            fillSpace()
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        if (getConfig(configKey).toBoolean())
            if (kotlinClass is DataClass) {
                return kotlinClass.copy(
                    codeBuilder = DataClassCodeBuilderDisableDataClass(
                        kotlinClass.codeBuilder
                    )
                )
            }
        return kotlinClass
    }
}