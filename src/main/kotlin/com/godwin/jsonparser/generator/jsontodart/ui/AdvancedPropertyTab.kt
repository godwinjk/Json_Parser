package com.godwin.jsonparser.generator.jsontodart.ui

import com.intellij.util.ui.JBDimension
import com.intellij.util.ui.JBUI
import com.godwin.jsonparser.generator.jsontodart.ConfigManager
import com.godwin.jsonparser.generator.jsontodart.utils.addComponentIntoVerticalBoxAlignmentLeft
import java.awt.FlowLayout
import java.awt.LayoutManager
import javax.swing.*
import javax.swing.border.EmptyBorder

/**
 *
 * Created by Seal.Wu on 2018/2/7.
 */
class AdvancedPropertyTab(layout: LayoutManager?, isDoubleBuffered: Boolean) : JPanel(layout, isDoubleBuffered) {

    constructor(isDoubleBuffered: Boolean) : this(FlowLayout(), isDoubleBuffered)

    init {
        val boxLayout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        setLayout(boxLayout)
        val bordWidth = JBUI.scale(10)
        border = EmptyBorder(bordWidth, bordWidth, 0, bordWidth)

        size = JBDimension(500, 320)
        preferredSize = JBDimension(500, 320)
        maximumSize = JBDimension(500, 320)
        maximumSize = JBDimension(500, 320)
        val keywordLable = JLabel("Keyword")

        val radioButtonNone = JRadioButton("None")

        radioButtonNone.addActionListener {
            ConfigManager.isPropertiesFinal = false
        }
        val radioButtonFinal = JRadioButton("final")

        radioButtonFinal.addActionListener {
            ConfigManager.isPropertiesFinal = true
        }

        if (ConfigManager.isPropertiesFinal) {

            radioButtonFinal.isSelected = true
        } else {
            radioButtonNone.isSelected = true
        }
        val buttonGroupProperty = ButtonGroup()
        buttonGroupProperty.add(radioButtonNone)
        buttonGroupProperty.add(radioButtonFinal)

        addComponentIntoVerticalBoxAlignmentLeft(keywordLable)
        add(Box.createVerticalStrut(JBUI.scale(10)))
        addComponentIntoVerticalBoxAlignmentLeft(radioButtonNone)
        add(Box.createVerticalStrut(JBUI.scale(10)))

        addComponentIntoVerticalBoxAlignmentLeft(radioButtonFinal)

        add(Box.createVerticalStrut(JBUI.scale(10)))


//        val line = com.intellij.util.xml.ui.TextPanel()
//                .apply {
//                    maximumSize = JBDimension(480, 1)
//                    minimumSize = JBDimension(480, 1)
//                    preferredSize = JBDimension(480, 1)
//                    background = Color.GRAY
//                }
//
//        add(line)
//
//        add(Box.createVerticalStrut(JBUI.scale(10)))
//
//        val initDefaultValueAvoidNull = JCheckBox("Init With Default Value (Avoid Null)")
//                .apply {
//                    isSelected = ConfigManager.defaultValueStrategy == DefaultValueStrategy.AvoidNull
//                }
//
//        val initDefaultValueAllowNull = JCheckBox("Init With Default Value Null When Property Is Nullable")
//                .apply {
//                    isSelected = ConfigManager.defaultValueStrategy == DefaultValueStrategy.AllowNull
//                }
//
//        initDefaultValueAvoidNull.addActionListener {
//            if(initDefaultValueAvoidNull.isSelected) {
//                ConfigManager.defaultValueStrategy = DefaultValueStrategy.AvoidNull
//                initDefaultValueAllowNull.isSelected = false
//            } else {
//                ConfigManager.defaultValueStrategy = DefaultValueStrategy.None
//            }
//        }
//
//        initDefaultValueAllowNull.addActionListener {
//            if(initDefaultValueAllowNull.isSelected) {
//                ConfigManager.defaultValueStrategy = DefaultValueStrategy.AllowNull
//                initDefaultValueAvoidNull.isSelected = false
//            } else {
//                ConfigManager.defaultValueStrategy = DefaultValueStrategy.None
//            }
//        }
//
//        add(Box.createVerticalStrut(JBUI.scale(10)))
//        addComponentIntoVerticalBoxAlignmentLeft(initDefaultValueAvoidNull)
//        add(Box.createVerticalStrut(JBUI.scale(10)))
//        addComponentIntoVerticalBoxAlignmentLeft(initDefaultValueAllowNull)
    }
}
