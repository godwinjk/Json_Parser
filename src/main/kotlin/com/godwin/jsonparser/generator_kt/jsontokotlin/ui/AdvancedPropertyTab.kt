package com.godwin.jsonparser.generator_kt.jsontokotlin.ui

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DefaultValueStrategy
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.PropertyTypeStrategy
import com.intellij.util.ui.JBDimension
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 *
 * Created by Seal.Wu on 2018/2/7.
 */
class AdvancedPropertyTab(isDoubleBuffered: Boolean) : JPanel(BorderLayout(), isDoubleBuffered) {

    init {
        jScrollPanel(JBDimension(500, 300)) {
            jVerticalLinearLayout {
                jLabel("Keyword")
                jButtonGroup {
                    jRadioButton(
                        "Val",
                        !KotlinConfigManager.isPropertiesVar,
                        { KotlinConfigManager.isPropertiesVar = false })
                    jRadioButton(
                        "Var",
                        KotlinConfigManager.isPropertiesVar,
                        { KotlinConfigManager.isPropertiesVar = true })
                }
                jLine()
                jLabel("Type")
                jButtonGroup {
                    jRadioButton("Non-Nullable",
                        KotlinConfigManager.propertyTypeStrategy == PropertyTypeStrategy.NotNullable,
                        { KotlinConfigManager.propertyTypeStrategy = PropertyTypeStrategy.NotNullable })
                    jRadioButton("Nullable", KotlinConfigManager.propertyTypeStrategy == PropertyTypeStrategy.Nullable,
                        { KotlinConfigManager.propertyTypeStrategy = PropertyTypeStrategy.Nullable })
                    jRadioButton("Auto Determine Nullable Or Not From JSON Value",
                        KotlinConfigManager.propertyTypeStrategy == PropertyTypeStrategy.AutoDeterMineNullableOrNot,
                        { KotlinConfigManager.propertyTypeStrategy = PropertyTypeStrategy.AutoDeterMineNullableOrNot })
                }
                jLine()
                jLabel("Default Value Strategy")
                jButtonGroup {
                    jRadioButton("Don't Init With Default Value",
                        KotlinConfigManager.defaultValueStrategy == DefaultValueStrategy.None,
                        { KotlinConfigManager.defaultValueStrategy = DefaultValueStrategy.None })
                    jRadioButton("Init With Non-Null Default Value (Avoid Null)",
                        KotlinConfigManager.defaultValueStrategy == DefaultValueStrategy.AvoidNull,
                        { KotlinConfigManager.defaultValueStrategy = DefaultValueStrategy.AvoidNull })
                    jRadioButton("Init With Default Value Null When Property Is Nullable",
                        KotlinConfigManager.defaultValueStrategy == DefaultValueStrategy.AllowNull,
                        { KotlinConfigManager.defaultValueStrategy = DefaultValueStrategy.AllowNull })
                }
            }
        }
    }
}
