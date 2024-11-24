package com.godwin.jsonparser.generator_kt.jsontokotlin.ui.dart

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DefaultValueStrategy
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.*
import com.intellij.util.ui.JBDimension
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 *
 * Created by Seal.Wu on 2018/2/7.
 */
class AdvancedDartPropertyTab(isDoubleBuffered: Boolean) : JPanel(BorderLayout(), isDoubleBuffered) {

    init {
        jScrollPanel(JBDimension(500, 300)) {
            jVerticalLinearLayout {
                jLabel("Keyword")
                jButtonGroup {
                    jRadioButton("none",
                        !DartConfigManager.isPropertyFinal,
                        { DartConfigManager.isPropertyFinal = false })
                    jRadioButton("final",
                        DartConfigManager.isPropertyFinal,
                        { DartConfigManager.isPropertyFinal = true })
                }
                jLine()
                jLabel("Null Safety")
                jButtonGroup {
                    jRadioButton("Non Nullable",
                        !DartConfigManager.isPropertyNullable,
                        { DartConfigManager.isPropertyNullable = false })
                    jRadioButton("Nullable",
                        DartConfigManager.isPropertyNullable,
                        { DartConfigManager.isPropertyNullable = true })
                }
                jLine()
                jLabel("Required or Optional")
                jButtonGroup {
                    jRadioButton("Optional",
                        !DartConfigManager.isPropertyOptional,
                        { DartConfigManager.isPropertyOptional = false })
                    jRadioButton("Required",
                        DartConfigManager.isPropertyOptional,
                        { DartConfigManager.isPropertyOptional = true })
                }
                jLine()
                jLabel("Default Value Strategy")
                jButtonGroup {
                    jRadioButton("Don't Init With Default Value",
                        DartConfigManager.defaultValueStrategy == DefaultValueStrategy.None,
                        { DartConfigManager.defaultValueStrategy = DefaultValueStrategy.None })
                    jRadioButton("Init With Non-Null Default Value (Avoid Null)",
                        DartConfigManager.defaultValueStrategy == DefaultValueStrategy.AvoidNull,
                        { DartConfigManager.defaultValueStrategy = DefaultValueStrategy.AvoidNull })
                    jRadioButton("Init With Default Value Null When Property Is Nullable",
                        DartConfigManager.defaultValueStrategy == DefaultValueStrategy.AllowNull,
                        { DartConfigManager.defaultValueStrategy = DefaultValueStrategy.AllowNull })
                }
            }
        }
    }
}
