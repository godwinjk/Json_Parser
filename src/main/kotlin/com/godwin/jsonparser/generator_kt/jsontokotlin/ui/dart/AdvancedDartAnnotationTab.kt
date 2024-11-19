package com.godwin.jsonparser.generator_kt.jsontokotlin.ui.dart

import com.godwin.jsonparser.generator.jsontodart.filetype.GenFileType
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.ConfigManager
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
        val customizeAnnotationConfigPanel =
                jVerticalLinearLayout(addToParent = false) {
                    alignLeftComponent {
                        jLabel("Annotation Import Class : ")
                    }
                    jTextAreaInput(ConfigManager.customAnnotationClassImportdeclarationString) {
                        ConfigManager.customAnnotationClassImportdeclarationString = it.text
                    }
                    alignLeftComponent {
                        jLabel("Class Annotation Format:")
                    }
                    jTextAreaInput(ConfigManager.customClassAnnotationFormatString) {
                        ConfigManager.customClassAnnotationFormatString = it.text
                    }
                    alignLeftComponent {
                        jLabel("Property Annotation Format:")
                    }
                    jTextAreaInput(ConfigManager.customPropertyAnnotationFormatString) {
                        ConfigManager.customPropertyAnnotationFormatString = it.text
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
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS //make sure when open customize annotation panel it's layout view will no change
        }
    }

    /**
     * Target JsonLib ConfigPanel
     */
    class AnnotationsSelectPanel(callBack: (isCustomizeAnnotationSelected: Boolean) -> Unit) : JPanel(BorderLayout(), true) {
        init {
            jGridLayout(5, 2) {
                jButtonGroup {
                    jRadioButton("None", ConfigManager.targetJsonConverterDart == TargetJsonConverter.None, {
                        ConfigManager.targetJsonConverterDart = TargetJsonConverter.None
                    })

                    jRadioButton("json_serializable", ConfigManager.targetJsonConverterDart == TargetJsonConverter.Gson, {
                        ConfigManager.targetJsonConverterDart = TargetJsonConverter.JsonSerializable
                    })

                    jRadioButton("freezed", ConfigManager.targetJsonConverterDart == TargetJsonConverter.Jackson, {
                        ConfigManager.targetJsonConverterDart = TargetJsonConverter.Freezed
                    })
                }
            }
        }
    }
}
