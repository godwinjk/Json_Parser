package com.godwin.jsonparser.generator_kt.jsontokotlin.ui.dart

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.TargetJsonConverter
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.*
import com.intellij.util.ui.JBDimension
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants

/**
 *
 * Created by Seal.Wu on 2018/2/7.
 */
/**
 * JSON Converter Annotation Tab View
 */
class AdvancedDartAnnotationTab(isDoubleBuffered: Boolean) : JPanel(BorderLayout(), isDoubleBuffered) {

    init {
        val customizeAnnotationConfigPanel = jVerticalLinearLayout(addToParent = false) {
            alignLeftComponent {
                jLabel("Annotation Import Class : ")
            }
            jTextAreaInput(DartConfigManager.customAnnotationClassImportdeclarationString) {
                DartConfigManager.customAnnotationClassImportdeclarationString = it.text
            }
            alignLeftComponent {
                jLabel("Class Annotation Format:")
            }
            jTextAreaInput(DartConfigManager.customClassAnnotationFormatString) {
                DartConfigManager.customClassAnnotationFormatString = it.text
            }
            alignLeftComponent {
                jLabel("Property Annotation Format:")
            }
            jTextAreaInput(DartConfigManager.customPropertyAnnotationFormatString) {
                DartConfigManager.customPropertyAnnotationFormatString = it.text
            }
        }
        val dartPackagePanel = jGridLayout(1, 2) {

            jCheckBox("json_serializable", DartConfigManager.isJsonSerializationAnnotation, { isSelected ->
                DartConfigManager.isJsonSerializationAnnotation = isSelected
                if (isSelected || DartConfigManager.isFreezedAnnotation) {
                    DartConfigManager.targetJsonConverterLib = TargetJsonConverter.DartPackage
                }
            })
            jCheckBox("freezed", DartConfigManager.isFreezedAnnotation, { isSelected ->
                DartConfigManager.isFreezedAnnotation = isSelected
                if (isSelected || DartConfigManager.isJsonSerializationAnnotation) {
                    DartConfigManager.targetJsonConverterLib = TargetJsonConverter.DartPackage
                }
            })

        }
        jScrollPanel(JBDimension(500, 300)) {
            jVerticalLinearLayout {
                add(AnnotationsSelectPanel {
                    when (it) {
                        TargetJsonConverter.Custom -> {
                            customizeAnnotationConfigPanel.isVisible = true
                            dartPackagePanel.isVisible = false
                        }

                        TargetJsonConverter.DartPackage -> {
                            customizeAnnotationConfigPanel.isVisible = false
                            dartPackagePanel.isVisible = true
                        }

                        else -> {
                            customizeAnnotationConfigPanel.isVisible = false
                            dartPackagePanel.isVisible = false
                        }
                    }
                })
                add(dartPackagePanel)
                add(customizeAnnotationConfigPanel)
            }
        }.apply {
            verticalScrollBarPolicy =
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS //make sure when open customize annotation panel it's layout view will no change
        }
    }

    /**
     * Target JsonLib ConfigPanel
     */
    class AnnotationsSelectPanel(callBack: (isCustomizeAnnotationSelected: TargetJsonConverter) -> Unit) :
        JPanel(BorderLayout(), true) {
        init {
            jGridLayout(5, 2) {
                jButtonGroup {
                    jRadioButton("None", TargetJsonConverter.None == DartConfigManager.targetJsonConverterLib, {
                        DartConfigManager.isJsonSerializationAnnotation = false
                        DartConfigManager.isFreezedAnnotation = false
                        DartConfigManager.targetJsonConverterLib = TargetJsonConverter.None

                        callBack(TargetJsonConverter.None)
                    })
                    jRadioButton("Package",
                        TargetJsonConverter.DartPackage == DartConfigManager.targetJsonConverterLib,
                        {
                            DartConfigManager.isJsonSerializationAnnotation = false
                            DartConfigManager.isFreezedAnnotation = false
                            DartConfigManager.targetJsonConverterLib = TargetJsonConverter.DartPackage

                            callBack(TargetJsonConverter.DartPackage)
                        })
                    jRadioButton("Custom", TargetJsonConverter.Custom == DartConfigManager.targetJsonConverterLib, {
                        DartConfigManager.isJsonSerializationAnnotation = false
                        DartConfigManager.isFreezedAnnotation = false
                        DartConfigManager.targetJsonConverterLib = TargetJsonConverter.Custom

                        callBack(TargetJsonConverter.Custom)
                    })
                }

            }
        }
    }
}
