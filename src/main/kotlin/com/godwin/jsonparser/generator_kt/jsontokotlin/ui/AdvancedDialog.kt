package com.godwin.jsonparser.generator_kt.jsontokotlin.ui

import com.godwin.jsonparser.generator.jsontodart.filetype.GenFileType
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.dart.AdvancedDartAnnotationTab
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.dart.AdvancedDartOtherTab
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.dart.AdvancedDartPropertyTab
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.ui.JBDimension
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JPanel

/**
 *
 * Created by Seal.Wu on 2017/9/13.
 */

class AdvancedDialog(canBeParent: Boolean) : DialogWrapper(canBeParent) {

    init {
        init()
        title = "Advanced"
    }


    override fun createCenterPanel(): JComponent? {

        return JBTabbedPane().apply {
            add("Dart", createDartTab())
            add("Kotlin", createKotlinTab())
            minimumSize = JBDimension(500, 300)
        }
    }

    private fun createDartTab(): JComponent {
        return JBTabbedPane().apply {
            add("Property", createPropertyTab(GenFileType.dart))
            add("Annotation", createAnnotationTab(GenFileType.dart))
            add("Other", createOtherSettingTab(GenFileType.dart))
//            add("Extensions", createExtensionTab())
            minimumSize = JBDimension(500, 300)
        }
    }

    private fun createKotlinTab(): JComponent {
        return JBTabbedPane().apply {
            add("Property", createPropertyTab(GenFileType.kotlin))
            add("Annotation", createAnnotationTab(GenFileType.kotlin))
            add("Other", createOtherSettingTab(GenFileType.kotlin))
            add("Extensions", createExtensionTab(GenFileType.kotlin))
            minimumSize = JBDimension(500, 300)
        }
    }

    private fun createOtherSettingTab(genFileType: GenFileType) =
        if (genFileType == GenFileType.kotlin) AdvancedOtherTab(true) else AdvancedDartOtherTab(true)

    private fun createAnnotationTab(genFileType: GenFileType) =
        if (genFileType == GenFileType.kotlin) AdvancedAnnotationTab(true) else AdvancedDartAnnotationTab(true)

    private fun createExtensionTab(genFileType: GenFileType) = ExtensionsTab(genFileType, true)

    private fun createPropertyTab(genFileType: GenFileType) =
        if (genFileType == GenFileType.kotlin) AdvancedPropertyTab(true) else AdvancedDartPropertyTab(true)

    override fun createActions(): Array<Action> {
        return arrayOf(okAction)
    }

}
