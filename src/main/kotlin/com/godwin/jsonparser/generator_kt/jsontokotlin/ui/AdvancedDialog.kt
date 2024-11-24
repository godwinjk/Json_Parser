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
            add("Property", createPropertyTab(GenFileType.Dart))
            add("Annotation", createAnnotationTab(GenFileType.Dart))
            add("Other", createOtherSettingTab(GenFileType.Dart))
//            add("Extensions", createExtensionTab())
            minimumSize = JBDimension(500, 300)
        }
    }

    private fun createKotlinTab(): JComponent {
        return JBTabbedPane().apply {
            add("Property", createPropertyTab(GenFileType.Kotlin))
            add("Annotation", createAnnotationTab(GenFileType.Kotlin))
            add("Other", createOtherSettingTab(GenFileType.Kotlin))
            add("Extensions", createExtensionTab())
            minimumSize = JBDimension(500, 300)
        }
    }

    private fun createOtherSettingTab(genFileType: GenFileType) =
        if (genFileType == GenFileType.Kotlin) AdvancedOtherTab(true) else AdvancedDartOtherTab(true)

    private fun createAnnotationTab(genFileType: GenFileType) =
        if (genFileType == GenFileType.Kotlin) AdvancedAnnotationTab(true) else AdvancedDartAnnotationTab(true)

    private fun createExtensionTab() = ExtensionsTab( true)

    private fun createPropertyTab(genFileType: GenFileType) =
        if (genFileType == GenFileType.Kotlin) AdvancedPropertyTab(true) else AdvancedDartPropertyTab(true)

    override fun createActions(): Array<Action> {
        return arrayOf(okAction)
    }

}
