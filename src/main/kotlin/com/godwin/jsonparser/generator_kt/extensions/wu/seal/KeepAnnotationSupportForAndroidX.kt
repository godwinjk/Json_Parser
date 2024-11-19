package com.godwin.jsonparser.generator_kt.extensions.wu.seal

import com.godwin.jsonparser.generator_kt.extensions.Extension
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jCheckBox
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.runWhenDataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Annotation
import javax.swing.JPanel

/**
 * @author Seal.Wu
 * create at 2019/11/03
 * description:
 */
object KeepAnnotationSupportForAndroidX : Extension() {

    /**
     * Config key can't be private, as it will be accessed from `library` module
     */
    @Suppress("MemberVisibilityCanBePrivate")
    const val configKey = "wu.seal.add_keep_annotation_enable_androidx"

    override fun createUI(): JPanel {
        return jHorizontalLinearLayout {
            jCheckBox("Add @Keep Annotation On Class (AndroidX)", getConfig(configKey).toBoolean(), { isSelected -> setConfig(
                configKey, isSelected.toString()) })
            fillSpace()
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        return kotlinClass.runWhenDataClass {
            if (getConfig(configKey).toBoolean()) {

                val classAnnotationString = "@Keep"

                val classAnnotation = Annotation.fromAnnotationString(classAnnotationString)

                val newAnnotations = mutableListOf(classAnnotation).also { it.addAll(annotations) }

                copy(annotations = newAnnotations)
            } else {
                this
            }
        } ?: kotlinClass
    }

    override fun intercept(originClassImportDeclaration: String): String {

        val classAnnotationImportClassString = "import androidx.annotation.Keep"

        return if (getConfig(configKey).toBoolean()) {
            originClassImportDeclaration.append(classAnnotationImportClassString)
        } else {
            originClassImportDeclaration
        }
    }
}