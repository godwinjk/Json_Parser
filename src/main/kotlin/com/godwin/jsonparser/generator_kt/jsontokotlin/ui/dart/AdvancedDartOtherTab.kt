package com.godwin.jsonparser.generator_kt.jsontokotlin.ui.dart

import com.godwin.jsonparser.generator.jsontodart.filetype.GenFileType
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.ConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.*
import com.intellij.util.ui.JBDimension
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * others settings tab in config settings dialog
 * Created by Seal.Wu on 2018/2/6.
 */
class AdvancedDartOtherTab( isDoubleBuffered: Boolean) : JPanel(BorderLayout(), isDoubleBuffered) {
    init {
        jVerticalLinearLayout {

            alignLeftComponent {

                jCheckBox("Append original JSON", ConfigManager.isAppendOriginalJson, { isSelected -> ConfigManager.isAppendOriginalJson = isSelected })

                jCheckBox("Enable Comment", ConfigManager.isCommentOff.not(), { isSelected -> ConfigManager.isCommentOff = isSelected.not() })

                jCheckBox("Enable Order By Alphabetical", ConfigManager.isOrderByAlphabetical, { isSelected -> ConfigManager.isOrderByAlphabetical = isSelected })

                jHorizontalLinearLayout {
                    jLabel("Indent (number of space): ")
                    jTextInput(ConfigManager.indent.toString()) {
                        columns = 2
                        addFocusLostListener {
                            ConfigManager.indent = try {
                                text.toInt()
                            } catch (e: Exception) {
                                text = ConfigManager.indent.toString()
                                ConfigManager.indent
                            }
                        }
                    }
                }
            }

            jHorizontalLinearLayout {
                jLabel("Parent Class Template: ")
                jTextInput(ConfigManager.parenClassTemplateDart) {
                    addFocusLostListener {
                        ConfigManager.parenClassTemplateDart = text
                    }
                    maximumSize = JBDimension(400, 30)
                }
            }
        }
    }
}
