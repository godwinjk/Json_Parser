package com.godwin.jsonparser.action

import com.godwin.jsonparser.util.Log
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.util.Key
import javax.swing.ButtonGroup
import javax.swing.JComponent
import javax.swing.JRadioButton

class JBRadioAction(
    text: String?,
    private val actionCommand: String?,
    private val buttonGroup: ButtonGroup?,
    private val actionListener: ((java.awt.event.ActionEvent) -> Unit)?,
    private val selected: Boolean = false
) : AnAction(text), CustomComponentAction {

    constructor(text: String?, buttonGroup: ButtonGroup?) : this(text, null, buttonGroup, null, false)

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent =
        getComponent(presentation)

    private fun getComponent(presentation: Presentation): JRadioButton {
        return JRadioButton("").apply {
            buttonGroup?.add(this)
            addActionListener { e ->
                isSelected = true
                Log.i("JBRadioAction.createCustomComponent${templatePresentation.text}")
                actionListener?.invoke(e)
            }
            presentation.putClientProperty(SELECTED_KEY, selected)
            updateCustomComponent(this, presentation)
        }
    }

    override fun actionPerformed(e: AnActionEvent) {}

    private fun updateCustomComponent(radioButton: JRadioButton, presentation: Presentation) {
        radioButton.apply {
            text = presentation.text
            toolTipText = presentation.description
            mnemonic = presentation.mnemonic
            displayedMnemonicIndex = presentation.displayedMnemonicIndex
            isSelected = presentation.getClientProperty(SELECTED_KEY) == true
            isEnabled = true
            isVisible = true
            actionCommand?.let { this.actionCommand = it }
            buttonGroup?.add(this)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.apply {
            isVisible = true
            isEnabled = true
        }
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    companion object {
        private val SELECTED_KEY = Key.create<Boolean>("selected")
    }
}
