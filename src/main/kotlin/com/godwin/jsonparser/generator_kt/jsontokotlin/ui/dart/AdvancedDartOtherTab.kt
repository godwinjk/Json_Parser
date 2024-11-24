package com.godwin.jsonparser.generator_kt.jsontokotlin.ui.dart

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.*
import com.intellij.util.ui.JBDimension
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * others settings tab in config settings dialog
 * Created by Seal.Wu on 2018/2/6.
 */
class AdvancedDartOtherTab(isDoubleBuffered: Boolean) : JPanel(BorderLayout(), isDoubleBuffered) {
    init {
        jVerticalLinearLayout {
            alignLeftComponent {
                jCheckBox(
                    "Append original JSON",
                    DartConfigManager.isAppendOriginalJson,
                    { isSelected -> DartConfigManager.isAppendOriginalJson = isSelected })

                jCheckBox(
                    "Enable Comment",
                    DartConfigManager.isCommentOff.not(),
                    { isSelected -> DartConfigManager.isCommentOff = isSelected.not() })

                jCheckBox(
                    "Enable Order By Alphabetical",
                    DartConfigManager.isOrderByAlphabetical,
                    { isSelected -> DartConfigManager.isOrderByAlphabetical = isSelected })
                jCheckBox(
                    "Enable dart file name convention",
                    DartConfigManager.isDartModelClassName,
                    { isSelected -> DartConfigManager.isDartModelClassName = isSelected })
                jCheckBox(
                    "Enable inner class generation",
                    DartConfigManager.isInnerClassModel,
                    { isSelected -> DartConfigManager.isInnerClassModel = isSelected })

                jHorizontalLinearLayout {
                    jLabel("Indent (number of space): ")
                    jTextInput(DartConfigManager.indent.toString()) {
                        columns = 2
                        addFocusLostListener {
                            DartConfigManager.indent = try {
                                text.toInt()
                            } catch (e: Exception) {
                                text = DartConfigManager.indent.toString()
                                DartConfigManager.indent
                            }
                        }
                    }
                }
            }

            jHorizontalLinearLayout {
                jLabel("Parent Class Template: ")
                jTextInput(DartConfigManager.parenClassTemplateDart) {
                    addFocusLostListener {
                        DartConfigManager.parenClassTemplateDart = text
                    }
                    maximumSize = JBDimension(400, 30)
                }
            }
        }
    }
}
