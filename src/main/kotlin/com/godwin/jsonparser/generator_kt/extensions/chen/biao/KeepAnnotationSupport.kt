package com.godwin.jsonparser.generator_kt.extensions.chen.biao

import com.godwin.jsonparser.generator_kt.extensions.Extension
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jCheckBox
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Annotation
import javax.swing.JPanel

/**
 * @author chenbiao
 * create at 2019/5/16
 * description:
 */
object KeepAnnotationSupport : Extension() {


    @Suppress("MemberVisibilityCanBePrivate")
    /**
     * Config key can't be private, as it will be accessed from `library` module
     */
    const val configKey = "chen.biao.add_keep_annotation_enable"

    override fun createUI(): JPanel {
        return jHorizontalLinearLayout {
            jCheckBox("Add @Keep Annotation On Class ", getConfig(configKey).toBoolean(), { isSelected -> setConfig(
                configKey, isSelected.toString()) })
            fillSpace()
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {
            return if (getConfig(configKey).toBoolean()) {

                val classAnnotationString = "@Keep"

                val classAnnotation = Annotation.fromAnnotationString(classAnnotationString)

                val newAnnotations = mutableListOf(classAnnotation).also { it.addAll(kotlinClass.annotations) }

                return kotlinClass.copy(annotations = newAnnotations)
            } else {
                kotlinClass
            }
        } else {
            return kotlinClass
        }

    }

    override fun intercept(originClassImportDeclaration: String): String {

        val classAnnotationImportClassString = "import android.support.annotation.Keep"

        return if (getConfig(configKey).toBoolean()) {
            originClassImportDeclaration.append(classAnnotationImportClassString)
        } else {
            originClassImportDeclaration
        }
    }
}