package com.godwin.jsonparser.generator.jsontokotlin.extensions.wu.seal

import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.Annotation
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator.common.ui.jCheckBox
import com.godwin.jsonparser.generator.common.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator.jsontokotlin.utils.runWhenDataClass
import javax.swing.JPanel

/**
 * @author Godwin
 * create at 2024/12/20
 * description:
 */
object KeepAnnotationSupportForAndroidX : com.godwin.jsonparser.generator.jsontokotlin.extensions.Extension() {

    /**
     * Config key can't be private, as it will be accessed from `library` module
     */
    @Suppress("MemberVisibilityCanBePrivate")
    const val configKey = "wu.seal.add_keep_annotation_enable_androidx"

    override fun createUI(): JPanel {
        return jHorizontalLinearLayout {
            jCheckBox("Add @Keep Annotation On Class (AndroidX)", getConfig(configKey).toBoolean(), { isSelected ->
                setConfig(
                    configKey, isSelected.toString()
                )
            })
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