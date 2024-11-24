package com.godwin.jsonparser.generator_kt.jsontokotlin.ui

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.TargetJsonConverter
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
class AdvancedAnnotationTab(isDoubleBuffered: Boolean) : JPanel(BorderLayout(), isDoubleBuffered) {

    init {

        val customizeAnnotationConfigPanel =
            jVerticalLinearLayout(addToParent = false) {
                alignLeftComponent {
                    jLabel("Annotation Import Class : ")
                }
                jTextAreaInput(KotlinConfigManager.customAnnotationClassImportdeclarationString) {
                    KotlinConfigManager.customAnnotationClassImportdeclarationString = it.text
                }
                alignLeftComponent {
                    jLabel("Class Annotation Format:")
                }
                jTextAreaInput(KotlinConfigManager.customClassAnnotationFormatString) {
                    KotlinConfigManager.customClassAnnotationFormatString = it.text
                }
                alignLeftComponent {
                    jLabel("Property Annotation Format:")
                }
                jTextAreaInput(KotlinConfigManager.customPropertyAnnotationFormatString) {
                    KotlinConfigManager.customPropertyAnnotationFormatString = it.text
                }
            }

        jScrollPanel(JBDimension(500, 300)) {
            jVerticalLinearLayout {
                add(AnnotationsSelectPanel {
                    customizeAnnotationConfigPanel.isVisible = it
                })
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
    class AnnotationsSelectPanel(callBack: (isCustomizeAnnotationSelected: Boolean) -> Unit) :
        JPanel(BorderLayout(), true) {
        init {
            jGridLayout(5, 2) {
                jButtonGroup {
                    jRadioButton("None", KotlinConfigManager.targetJsonConverterLib == TargetJsonConverter.None, {
                        KotlinConfigManager.targetJsonConverterLib = TargetJsonConverter.None
                    })

                    jRadioButton(
                        "None (Camel Case)",
                        KotlinConfigManager.targetJsonConverterLib == TargetJsonConverter.NoneWithCamelCase,
                        {
                            KotlinConfigManager.targetJsonConverterLib = TargetJsonConverter.NoneWithCamelCase
                        })

                    jRadioButton("Gson", KotlinConfigManager.targetJsonConverterLib == TargetJsonConverter.Gson, {
                        KotlinConfigManager.targetJsonConverterLib = TargetJsonConverter.Gson
                    })

                    jRadioButton("Jackson", KotlinConfigManager.targetJsonConverterLib == TargetJsonConverter.Jackson, {
                        KotlinConfigManager.targetJsonConverterLib = TargetJsonConverter.Jackson
                    })

                    jRadioButton(
                        "Fastjson",
                        KotlinConfigManager.targetJsonConverterLib == TargetJsonConverter.FastJson,
                        {
                            KotlinConfigManager.targetJsonConverterLib = TargetJsonConverter.FastJson
                        })

                    jRadioButton(
                        "MoShi (Reflect)",
                        KotlinConfigManager.targetJsonConverterLib == TargetJsonConverter.MoShi,
                        {
                            KotlinConfigManager.targetJsonConverterLib = TargetJsonConverter.MoShi
                        })

                    jRadioButton(
                        "MoShi (Codegen)",
                        KotlinConfigManager.targetJsonConverterLib == TargetJsonConverter.MoshiCodeGen,
                        {
                            KotlinConfigManager.targetJsonConverterLib = TargetJsonConverter.MoshiCodeGen
                        })
                    jRadioButton(
                        "LoganSquare",
                        KotlinConfigManager.targetJsonConverterLib == TargetJsonConverter.LoganSquare,
                        {
                            KotlinConfigManager.targetJsonConverterLib = TargetJsonConverter.LoganSquare
                        })
                    jRadioButton(
                        "kotlinx.serialization",
                        KotlinConfigManager.targetJsonConverterLib == TargetJsonConverter.Serializable,
                        {
                            KotlinConfigManager.targetJsonConverterLib = TargetJsonConverter.Serializable
                        })
                    jRadioButton(
                        "Others by customize",
                        KotlinConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom,
                        {
                            KotlinConfigManager.targetJsonConverterLib = TargetJsonConverter.Custom
                        }) {
                        addChangeListener {
                            callBack(isSelected)
                        }
                    }
                    callBack(KotlinConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom)
                }
            }
        }
    }
}
