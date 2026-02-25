package com.godwin.jsonparser.generator.jsontokotlin.extensions.yuan.varenyzc

import com.godwin.jsonparser.generator.common.ui.jCheckBox
import com.godwin.jsonparser.generator.common.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator.common.ui.jLabel
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import javax.swing.JPanel

object BuildFromJsonObjectSupport : com.godwin.jsonparser.generator.jsontokotlin.extensions.Extension() {

    const val configKey = "top.varenyzc.build_from_json_enable"

    override fun createUI(): JPanel {
        return jHorizontalLinearLayout {
            jCheckBox(
                "",
                getConfig(configKey).toBoolean(),
                { isSelected -> setConfig(configKey, isSelected.toString()) }
            )
            jLabel(
                "Make a static function that can build from JSONObject",
            )
            fillSpace()
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        if (getConfig(configKey).toBoolean())
            if (kotlinClass is DataClass) {
                return kotlinClass.copy(
                    codeBuilder = DataClassCodeBuilderForAddingBuildFromJsonObject(
                        kotlinClass.codeBuilder
                    )
                )
            }
        return kotlinClass
    }

    override fun intercept(originClassImportDeclaration: String): String {

        val classImportClassString = "import org.json.JSONObject"
        return if (getConfig(configKey).toBoolean()) {
            originClassImportDeclaration.append(classImportClassString).append("\n")
        } else {
            originClassImportDeclaration
        }
    }
}