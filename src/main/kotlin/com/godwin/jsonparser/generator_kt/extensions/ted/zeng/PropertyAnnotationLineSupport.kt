package com.godwin.jsonparser.generator_kt.extensions.ted.zeng

import com.godwin.jsonparser.generator_kt.extensions.Extension
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Property
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jCheckBox
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jHorizontalLinearLayout
import javax.swing.JPanel

/**
 * Created by ted on 2019-06-13 18:10.
 */
object PropertyAnnotationLineSupport : Extension() {

    /**
     * Config key can't be private, as it will be accessed from `library` module
     */
    @Suppress("MemberVisibilityCanBePrivate")
    const val configKey = "ted.zeng.property_annotation_in_same_line_enable"

    override fun createUI(): JPanel {
        return jHorizontalLinearLayout {
            jCheckBox("Keep Annotation And Property In Same Line", getConfig(configKey).toBoolean(), { isSelected -> setConfig(
                configKey, isSelected.toString()) })
            fillSpace()
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        return if (kotlinClass is DataClass) {
            if (getConfig(configKey).toBoolean()) {
                kotlinClass.properties.forEach(Property::letLastAnnotationStayInSameLine)
            }
            kotlinClass
        } else {
            kotlinClass
        }
    }

}