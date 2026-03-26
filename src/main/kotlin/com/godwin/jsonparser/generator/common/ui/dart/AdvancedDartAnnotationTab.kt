package com.godwin.jsonparser.generator.common.ui.dart

import com.godwin.jsonparser.generator.common.ui.*
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.model.TargetJsonConverter
import com.intellij.util.ui.JBDimension
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants

/**
 * JSON Converter Annotation Tab View
 */
class AdvancedDartAnnotationTab(isDoubleBuffered: Boolean) : JPanel(BorderLayout(), isDoubleBuffered) {

    init {
        val customizeAnnotationConfigPanel = jVerticalLinearLayout(addToParent = false) {
            alignLeftComponent { jLabel("Annotation Import Class: ") }
            jTextAreaInput(DartConfigManager.customAnnotationClassImportdeclarationString) {
                DartConfigManager.customAnnotationClassImportdeclarationString = it.text
            }
            alignLeftComponent { jLabel("Class Annotation Format:") }
            jTextAreaInput(DartConfigManager.customClassAnnotationFormatString) {
                DartConfigManager.customClassAnnotationFormatString = it.text
            }
            alignLeftComponent { jLabel("Property Annotation Format:") }
            jTextAreaInput(DartConfigManager.customPropertyAnnotationFormatString) {
                DartConfigManager.customPropertyAnnotationFormatString = it.text
            }
        }

        // Callback invoked when both checkboxes are unchecked — resets to None
        var onAllUnchecked: () -> Unit = {}

        val dartPackagePanel = jGridLayout(1, 2) {
            jCheckBox("json_serializable", DartConfigManager.isJsonSerializationAnnotation, { isSelected ->
                DartConfigManager.isJsonSerializationAnnotation = isSelected
                if (isSelected) {
                    DartConfigManager.targetJsonConverterLib = TargetJsonConverter.DartPackage
                } else if (!DartConfigManager.isFreezedAnnotation) {
                    onAllUnchecked()
                }
            })
            jCheckBox("freezed", DartConfigManager.isFreezedAnnotation, { isSelected ->
                DartConfigManager.isFreezedAnnotation = isSelected
                if (isSelected) {
                    DartConfigManager.targetJsonConverterLib = TargetJsonConverter.DartPackage
                } else if (!DartConfigManager.isJsonSerializationAnnotation) {
                    onAllUnchecked()
                }
            })
        }

        jScrollPanel(JBDimension(500, 300)) {
            jVerticalLinearLayout {
                val annotationsSelectPanel = AnnotationsSelectPanel { converter ->
                    when (converter) {
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
                }

                // Wire the callback: when both checkboxes unchecked, click None radio
                onAllUnchecked = {
                    DartConfigManager.targetJsonConverterLib = TargetJsonConverter.None
                    dartPackagePanel.isVisible = false
                    annotationsSelectPanel.selectNone()
                }

                add(annotationsSelectPanel)
                add(dartPackagePanel)
                add(customizeAnnotationConfigPanel)
                dartPackagePanel.isVisible =
                    DartConfigManager.targetJsonConverterLib == TargetJsonConverter.DartPackage
                customizeAnnotationConfigPanel.isVisible =
                    DartConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom
            }
        }.apply {
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        }
    }

    /**
     * Target JsonLib ConfigPanel
     */
    class AnnotationsSelectPanel(callBack: (TargetJsonConverter) -> Unit) : JPanel(BorderLayout(), true) {

        private lateinit var noneRadio: javax.swing.JRadioButton

        fun selectNone() {
            noneRadio.isSelected = true
        }

        init {
            jGridLayout(5, 2) {
                jButtonGroup {
                    noneRadio = jRadioButton(
                        "None",
                        TargetJsonConverter.None == DartConfigManager.targetJsonConverterLib,
                        {
                            DartConfigManager.isJsonSerializationAnnotation = false
                            DartConfigManager.isFreezedAnnotation = false
                            DartConfigManager.targetJsonConverterLib = TargetJsonConverter.None
                            callBack(TargetJsonConverter.None)
                        })
                    jRadioButton(
                        "Package",
                        TargetJsonConverter.DartPackage == DartConfigManager.targetJsonConverterLib,
                        {
                            DartConfigManager.isJsonSerializationAnnotation = false
                            DartConfigManager.isFreezedAnnotation = false
                            DartConfigManager.targetJsonConverterLib = TargetJsonConverter.DartPackage
                            callBack(TargetJsonConverter.DartPackage)
                        })
                    jRadioButton(
                        "Custom",
                        TargetJsonConverter.Custom == DartConfigManager.targetJsonConverterLib,
                        {
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
