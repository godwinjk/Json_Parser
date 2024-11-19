package com.godwin.jsonparser.generator_kt.extensions.jose.han

import com.godwin.jsonparser.generator_kt.extensions.Extension
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jCheckBox
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jLink
import javax.swing.JPanel
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Annotation
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass

/**
 *  @author jose.han
 *  @Date 2019/7/27Ø
 */
object ParcelableAnnotationSupport : Extension() {

    /**
     * Config key can't be private, as it will be accessed from `library` module
     */
    @Suppress("MemberVisibilityCanBePrivate")
    const val configKey = "jose.han.add_parcelable_annotatioin_enable"

    override fun createUI(): JPanel {
        return jHorizontalLinearLayout {
            jCheckBox("Enable Parcelable Support ", getConfig(configKey).toBoolean(), { isSelected -> setConfig(
                configKey, isSelected.toString()) })
            jLink("May Need Some Config", "https://github.com/wuseal/JsonToKotlinClass/blob/master/doc/parceable_support_tip.md")
            fillSpace()
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {
            if (getConfig(configKey).toBoolean()) {
                val classAnnotation = Annotation.fromAnnotationString("@Parcelize")
                val newAnnotations = kotlinClass.annotations.plus(classAnnotation)

                return kotlinClass.copy(annotations = newAnnotations, parentClassTemplate = "Parcelable")
            }
        }
        return kotlinClass
    }

    override fun intercept(originClassImportDeclaration: String): String {

        val classAnnotationImportClassString = "import kotlinx.parcelize.Parcelize".append("import android.os.Parcelable")

        return if (getConfig(configKey).toBoolean()) {
            originClassImportDeclaration.append(classAnnotationImportClassString)
        } else {
            originClassImportDeclaration
        }
    }
}
