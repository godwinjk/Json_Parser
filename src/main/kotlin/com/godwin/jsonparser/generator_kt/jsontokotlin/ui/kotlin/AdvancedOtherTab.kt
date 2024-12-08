package com.godwin.jsonparser.generator_kt.jsontokotlin.ui.kotlin

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.*
import com.intellij.util.ui.JBDimension
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * others settings tab in config settings dialog
 * Created by Godwin on 2024/12/20
 */
class AdvancedOtherTab(isDoubleBuffered: Boolean) : JPanel(BorderLayout(), isDoubleBuffered) {
    init {
        jVerticalLinearLayout {

            alignLeftComponent {

                jCheckBox(
                    "Append original JSON",
                    KotlinConfigManager.isAppendOriginalJson,
                    { isSelected -> KotlinConfigManager.isAppendOriginalJson = isSelected })

                jCheckBox(
                    "Enable Comment",
                    KotlinConfigManager.isCommentOff.not(),
                    { isSelected -> KotlinConfigManager.isCommentOff = isSelected.not() })

                jCheckBox(
                    "Enable Order By Alphabetical",
                    KotlinConfigManager.isOrderByAlphabetical,
                    { isSelected -> KotlinConfigManager.isOrderByAlphabetical = isSelected })

                jCheckBox(
                    "Enable Inner Class Model",
                    KotlinConfigManager.isInnerClassModel,
                    { isSelected -> KotlinConfigManager.isInnerClassModel = isSelected })

                jCheckBox(
                    "Enable Map Type when JSON Field Key Is Primitive Type",
                    KotlinConfigManager.enableMapType,
                    { isSelected -> KotlinConfigManager.enableMapType = isSelected })

                jCheckBox(
                    "Only create annotations when needed",
                    KotlinConfigManager.enableMinimalAnnotation,
                    { isSelected -> KotlinConfigManager.enableMinimalAnnotation = isSelected })

                jCheckBox(
                    "Auto detect JSON Scheme",
                    KotlinConfigManager.autoDetectJsonScheme,
                    { isSelected -> KotlinConfigManager.autoDetectJsonScheme = isSelected })

                jHorizontalLinearLayout {
                    jLabel("Indent (number of space): ")
                    jTextInput(KotlinConfigManager.indent.toString()) {
                        columns = 2
                        addFocusLostListener {
                            KotlinConfigManager.indent = try {
                                text.toInt()
                            } catch (e: Exception) {
                                text = KotlinConfigManager.indent.toString()
                                KotlinConfigManager.indent
                            }
                        }
                    }
                }
            }

            jHorizontalLinearLayout {
                jLabel("Parent Class Template: ")
                jTextInput(KotlinConfigManager.parenClassTemplate) {
                    addFocusLostListener {
                        KotlinConfigManager.parenClassTemplate = text
                    }
                    maximumSize = JBDimension(400, 30)
                }
            }
        }
    }
}
