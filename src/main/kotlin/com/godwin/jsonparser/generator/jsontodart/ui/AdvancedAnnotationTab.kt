package com.godwin.jsonparser.generator.jsontodart.ui

import com.godwin.jsonparser.generator.jsontodart.utils.addComponentIntoVerticalBoxAlignmentLeft
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.TargetJsonConverter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBDimension
import com.intellij.util.ui.JBEmptyBorder
import com.intellij.util.ui.JBUI
import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.LayoutManager
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.*
import javax.swing.border.EmptyBorder

/**
 *
 * Created by Seal.Wu on 2018/2/7.
 */
/**
 * JSON Converter Annotation Tab View
 */
class AdvancedAnnotationTab(layout: LayoutManager?, isDoubleBuffered: Boolean) : JPanel(layout, isDoubleBuffered) {

    constructor(isDoubleBuffered: Boolean) : this(FlowLayout(), isDoubleBuffered)

    init {

        val subBoxPanel = JPanel()
            .apply {
                minimumSize = JBDimension(480, 150)
                border = JBEmptyBorder(0, 10, 0, 0)
                this.layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
            }


        addAnnotationClassImportCodeSettingPanel(subBoxPanel)

        addClassAnnotationFormatSettingPanel(subBoxPanel)

        addPropertyAnnotationFormatSettingPanel(subBoxPanel)

        val annotationSelectPanel = TargetJsonLibConfigPanel(true) {
            subBoxPanel.isVisible = it
        }

        val boxPanel = JPanel()
            .apply {
                this.layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                val bordWidth = JBUI.scale(10)
                border = EmptyBorder(bordWidth, bordWidth, bordWidth, bordWidth)
                size = JBDimension(480, 350)
                minimumSize = JBDimension(480, 350)

                add(annotationSelectPanel)
                add(Box.createVerticalStrut(JBUI.scale(10)))
                add(subBoxPanel)
            }


        subBoxPanel.isVisible = DartConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom


        setLayout(BoxLayout(this, BoxLayout.PAGE_AXIS))

        border = JBEmptyBorder(0)

        val jbScrollPane = JBScrollPane(boxPanel)
            .apply {
                size = JBDimension(500, 320)
                preferredSize = JBDimension(500, 320)
                maximumSize = JBDimension(500, 320)
                maximumSize = JBDimension(500, 320)
                this.border = null
            }

        addComponentIntoVerticalBoxAlignmentLeft(jbScrollPane)

    }

    private fun addAnnotationClassImportCodeSettingPanel(subBoxPanel: JPanel): JPanel {

        val annotationImportClassTextArea = JTextArea(DartConfigManager.customAnnotationClassImportdeclarationString)
            .apply {
                minimumSize = JBDimension(460, 40)
                addFocusListener(object : FocusListener {
                    override fun focusLost(e: FocusEvent?) {
                        DartConfigManager.customAnnotationClassImportdeclarationString = text
                    }

                    override fun focusGained(e: FocusEvent?) {
                    }

                })
            }

        val jbScrollPaneClassFormat = JBScrollPane(annotationImportClassTextArea)
            .apply {
                preferredSize = JBDimension(460, 40)
                autoscrolls = true
                horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            }

        val annotationImportClass = JPanel(true)
            .apply {
                minimumSize = JBDimension(460, 60)
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                val importClassLable = JBLabel("Annotation Import Class : ")
                addComponentIntoVerticalBoxAlignmentLeft(importClassLable)
                add(Box.createVerticalStrut(JBUI.scale(10)))
                addComponentIntoVerticalBoxAlignmentLeft(jbScrollPaneClassFormat)
            }

        subBoxPanel.addComponentIntoVerticalBoxAlignmentLeft(annotationImportClass)
        return annotationImportClass
    }

    private fun addPropertyAnnotationFormatSettingPanel(subBoxPanel: JPanel) {

        val annotationFormatField = JTextArea(DartConfigManager.customPropertyAnnotationFormatString)
            .apply {
                minimumSize = JBDimension(460, 40)
                addFocusListener(object : FocusListener {
                    override fun focusLost(e: FocusEvent?) {
                        DartConfigManager.customPropertyAnnotationFormatString = text
                    }

                    override fun focusGained(e: FocusEvent?) {
                    }

                })
            }

        val jbScrollPanePropertyFormat = JBScrollPane(annotationFormatField)
            .apply {
                preferredSize = JBDimension(460, 40)
                autoscrolls = true
                horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            }

        val annotationStringPanel = JPanel(true)
            .apply {
                minimumSize = JBDimension(460, 60)
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                addComponentIntoVerticalBoxAlignmentLeft(JBLabel("Property Annotation Format: "))
                add(Box.createVerticalStrut(JBUI.scale(10)))
                addComponentIntoVerticalBoxAlignmentLeft(jbScrollPanePropertyFormat)
            }


        subBoxPanel.addComponentIntoVerticalBoxAlignmentLeft(annotationStringPanel)
    }

    private fun addClassAnnotationFormatSettingPanel(subBoxPanel: JPanel) {

        val annotationClassFormatField = JTextArea(DartConfigManager.customClassAnnotationFormatString)
            .apply {
                minimumSize = JBDimension(460, 40)
                addFocusListener(object : FocusListener {
                    override fun focusLost(e: FocusEvent?) {
                        DartConfigManager.customClassAnnotationFormatString = text
                    }

                    override fun focusGained(e: FocusEvent?) {
                    }

                })
            }

        val jbScrollPaneClassAnnotationFormat = JBScrollPane(annotationClassFormatField)
            .apply {
                preferredSize = JBDimension(460, 40)
                autoscrolls = true
                horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED

            }


        val annotationClassFormatStringPanel = JPanel(true)
            .apply {
                minimumSize = JBDimension(460, 60)
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                addComponentIntoVerticalBoxAlignmentLeft(JBLabel("Class Annotation Format: "))
                add(Box.createVerticalStrut(JBUI.scale(10)))
                addComponentIntoVerticalBoxAlignmentLeft(jbScrollPaneClassAnnotationFormat)
            }

        subBoxPanel.addComponentIntoVerticalBoxAlignmentLeft(annotationClassFormatStringPanel)
    }


    /**
     * Target JsonLib ConfigPanel
     */
    class TargetJsonLibConfigPanel(
        layout: LayoutManager?,
        isDoubleBuffered: Boolean,
        callBack: (selected: Boolean) -> Unit
    ) : JPanel(layout, isDoubleBuffered) {

        constructor(layout: LayoutManager?, callBack: (selected: Boolean) -> Unit) : this(layout, false, callBack)

        constructor(isDoubleBuffered: Boolean, callBack: (selected: Boolean) -> Unit) : this(
            FlowLayout(),
            isDoubleBuffered,
            callBack
        )

        init {
            val gridLayout = JPanel()
            gridLayout.layout = GridLayout(5, 2, 10, 10)

            setLayout(BoxLayout(this, BoxLayout.PAGE_AXIS))


            val radioButtonNone = JRadioButton("None")
            val radioButtonNoneWithCamelCase = JRadioButton("None (Camel Case)")
            val radioButtonGson = JRadioButton("Gson")
            val radioButtonJackson = JRadioButton("Jackson")
            val radioButtonFastjson = JRadioButton("Fastjson")
            val radioButtonMoShi = JRadioButton("MoShi")
            val radioButtonMoShiCodeGen = JRadioButton("MoShi (Codegen)")
            val radioButtonLoganSquare = JRadioButton("LoganSquare")
            val radioButtonCustom = JRadioButton("Others by customize")
            var radioButtonSerilizable = JRadioButton("kotlinx.serialization")

            radioButtonNone.addActionListener {
                DartConfigManager.targetJsonConverterLib = TargetJsonConverter.None
                callBack(DartConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom)

            }
            radioButtonNoneWithCamelCase.addActionListener {
                DartConfigManager.targetJsonConverterLib = TargetJsonConverter.NoneWithCamelCase
                callBack(DartConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom)

            }
            radioButtonGson.addActionListener {
                DartConfigManager.targetJsonConverterLib = TargetJsonConverter.Gson
                callBack(DartConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom)

            }
            radioButtonJackson.addActionListener {
                DartConfigManager.targetJsonConverterLib = TargetJsonConverter.Jackson
                callBack(DartConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom)
            }
            radioButtonFastjson.addActionListener {
                DartConfigManager.targetJsonConverterLib = TargetJsonConverter.FastJson
                callBack(DartConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom)
            }

            radioButtonMoShi.addActionListener {
                DartConfigManager.targetJsonConverterLib = TargetJsonConverter.MoShi
                callBack(DartConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom)
            }

            radioButtonMoShiCodeGen.addActionListener {
                DartConfigManager.targetJsonConverterLib = TargetJsonConverter.MoshiCodeGen
                callBack(DartConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom)
            }

            radioButtonLoganSquare.addActionListener {
                DartConfigManager.targetJsonConverterLib = TargetJsonConverter.LoganSquare
                callBack(DartConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom)
            }
            radioButtonCustom.addActionListener {
                DartConfigManager.targetJsonConverterLib = TargetJsonConverter.Custom
                callBack(DartConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom)
            }
            radioButtonSerilizable.addActionListener {
                DartConfigManager.targetJsonConverterLib = TargetJsonConverter.DartPackage
                callBack(DartConfigManager.targetJsonConverterLib == TargetJsonConverter.Custom)
            }

            when (DartConfigManager.targetJsonConverterLib) {
                TargetJsonConverter.None -> radioButtonNone.isSelected = true
                TargetJsonConverter.NoneWithCamelCase -> radioButtonNoneWithCamelCase.isSelected = true
                TargetJsonConverter.Gson -> radioButtonGson.isSelected = true
                TargetJsonConverter.Jackson -> radioButtonJackson.isSelected = true
                TargetJsonConverter.FastJson -> radioButtonFastjson.isSelected = true
                TargetJsonConverter.LoganSquare -> radioButtonLoganSquare.isSelected = true
                TargetJsonConverter.MoShi -> radioButtonMoShi.isSelected = true
                TargetJsonConverter.MoshiCodeGen -> radioButtonMoShiCodeGen.isSelected = true
                TargetJsonConverter.Custom -> radioButtonCustom.isSelected = true
                TargetJsonConverter.DartPackage -> radioButtonSerilizable.isSelected = true
                else -> {}
            }

            val buttonGroupProperty = ButtonGroup()
            buttonGroupProperty.add(radioButtonNone)
            buttonGroupProperty.add(radioButtonNoneWithCamelCase)
            buttonGroupProperty.add(radioButtonGson)
            buttonGroupProperty.add(radioButtonJackson)
            buttonGroupProperty.add(radioButtonFastjson)
            buttonGroupProperty.add(radioButtonMoShi)
            buttonGroupProperty.add(radioButtonMoShiCodeGen)
            buttonGroupProperty.add(radioButtonLoganSquare)
            buttonGroupProperty.add(radioButtonCustom)
            buttonGroupProperty.add(radioButtonSerilizable)

            gridLayout.add(radioButtonNone)
            gridLayout.add(radioButtonNoneWithCamelCase)
            gridLayout.add(radioButtonGson)
            gridLayout.add(radioButtonJackson)
            gridLayout.add(radioButtonFastjson)
            gridLayout.add(radioButtonMoShi)
            gridLayout.add(radioButtonMoShiCodeGen)
            gridLayout.add(radioButtonLoganSquare)
            gridLayout.add(radioButtonCustom)
            gridLayout.add(radioButtonSerilizable)

            gridLayout.size = JBDimension(480, 230)
            gridLayout.preferredSize = JBDimension(480, 230)
            gridLayout.maximumSize = JBDimension(480, 230)
            gridLayout.maximumSize = JBDimension(480, 230)


            size = JBDimension(480, 230)
            maximumSize = JBDimension(480, 230)
            maximumSize = JBDimension(480, 230)

            addComponentIntoVerticalBoxAlignmentLeft(gridLayout)
        }

    }
}
