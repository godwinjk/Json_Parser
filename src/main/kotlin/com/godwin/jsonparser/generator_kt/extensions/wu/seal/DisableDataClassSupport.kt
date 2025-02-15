package com.godwin.jsonparser.generator_kt.extensions.wu.seal

import com.godwin.jsonparser.generator_kt.extensions.Extension
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jCheckBox
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jHorizontalLinearLayout
import javax.swing.JPanel

/**
 * Extension support disable kotlin data class, after enable this, all kotlin data classes will remove 'data' modifier
 */
object DisableDataClassSupport : Extension() {

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
                return kotlinClass.copy(codeBuilder = DataClassCodeBuilderDisableDataClass(kotlinClass.codeBuilder))
            }
        return kotlinClass
    }
}