package com.godwin.jsonparser.generator_kt.jsontokotlin.ui.dart

import com.godwin.jsonparser.generator.jsontodart.filetype.GenFileType
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.ConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DefaultValueStrategy
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.PropertyTypeStrategy
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.*
import com.intellij.util.ui.JBDimension
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 *
 * Created by Seal.Wu on 2018/2/7.
 */
class AdvancedDartPropertyTab( isDoubleBuffered: Boolean) : JPanel(BorderLayout(), isDoubleBuffered) {

    init {
        jScrollPanel(JBDimension(500, 300)) {
            jVerticalLinearLayout {
                jLabel("Keyword")
                jButtonGroup {
                    jRadioButton("none", !ConfigManager.isPropertyFinal, { ConfigManager.isPropertiesVar = false })
                    jRadioButton("final", ConfigManager.isPropertyFinal, { ConfigManager.isPropertiesVar = true })
                }
                jLine()
                jLabel("Type")
                jButtonGroup {

                    jRadioButton("Non Nullable", !ConfigManager.isPropertyNullable ,
                            { ConfigManager.isPropertyNullable = false })
                    jRadioButton("Optional", ConfigManager.isPropertyNullable,
                            { ConfigManager.isPropertyNullable = true })
                }
            }
        }
    }
}
