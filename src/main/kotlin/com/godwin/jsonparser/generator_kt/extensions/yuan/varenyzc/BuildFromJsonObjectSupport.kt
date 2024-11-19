package com.godwin.jsonparser.generator_kt.extensions.yuan.varenyzc

import com.godwin.jsonparser.generator_kt.extensions.Extension
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jCheckBox
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jLink
import javax.swing.JPanel

object BuildFromJsonObjectSupport : Extension() {

    const val configKey = "top.varenyzc.build_from_json_enable"

    override fun createUI(): JPanel {
        return jHorizontalLinearLayout {
            jCheckBox(
                "",
                getConfig(configKey).toBoolean(),
                { isSelected -> setConfig(configKey, isSelected.toString()) }
            )
            jLink(
                "Make a static function that can build from JSONObject",
                "https://github.com/wuseal/JsonToKotlinClass/blob/master/doc/build_from_jsonobject_tip.md"
            )
            fillSpace()
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        if (getConfig(configKey).toBoolean())
            if (kotlinClass is DataClass) {
                return kotlinClass.copy(codeBuilder = DataClassCodeBuilderForAddingBuildFromJsonObject(kotlinClass.codeBuilder))
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