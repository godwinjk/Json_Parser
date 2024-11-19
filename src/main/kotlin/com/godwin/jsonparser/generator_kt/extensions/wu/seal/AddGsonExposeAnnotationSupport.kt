package com.godwin.jsonparser.generator_kt.extensions.wu.seal

import com.google.gson.annotations.Expose
import com.godwin.jsonparser.generator_kt.extensions.Extension
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.addSelectListener
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jCheckBox
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Annotation
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jHorizontalLinearLayout
import javax.swing.JPanel

object AddGsonExposeAnnotationSupport : Extension() {

    @Expose
    val config_key = "wu.seal.add_gson_expose_annotation"

    override fun createUI(): JPanel {
        return jHorizontalLinearLayout {
            jCheckBox("Add Gson @Expose Annotation", config_key.booleanConfigValue) {
                addSelectListener { setConfig(config_key, it.toString()) }
            }
            fillSpace()
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        return if (config_key.booleanConfigValue) {
            if (kotlinClass is DataClass) {
                val newProperties = kotlinClass.properties.map {
                    val newAnnotations = it.annotations + Annotation.fromAnnotationString("@Expose")
                    it.copy(annotations = newAnnotations)
                }
                kotlinClass.copy(properties = newProperties)
            } else kotlinClass
        } else kotlinClass
    }

    override fun intercept(originClassImportDeclaration: String): String {
        return if (config_key.booleanConfigValue) {
            originClassImportDeclaration.append("import com.google.gson.annotations.Expose")
        } else originClassImportDeclaration
    }
}