package com.godwin.jsonparser.generator_kt.jsontokotlin.ui

import com.godwin.jsonparser.generator_kt.extensions.ExtensionsCollector
import com.intellij.util.ui.JBDimension
import java.awt.BorderLayout
import javax.swing.JPanel

class ExtensionsTab(isDoubleBuffered: Boolean) :
    JPanel(BorderLayout(), isDoubleBuffered) {
    init {
        jScrollPanel(JBDimension(500, 300)) {
            jVerticalLinearLayout {
                ExtensionsCollector.extensions.forEach {
                    add(it.createUI())
                }
                fixedSpace(30)
            }
        }
    }
}
