package com.godwin.jsonparser.action

import com.godwin.jsonparser.ui.IParserWidget
import com.godwin.jsonparser.ui.dialog.JsonDiffDialog
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class DiffCheckerAction(private val parserWidget: IParserWidget) : AnAction(
    "JSON Diff",
    "Compare two JSON documents",
    AllIcons.Actions.Diff
) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val currentJson = findEditorText(parserWidget.getTabs()?.getCurrentComponent()) ?: ""
        JsonDiffDialog.show(project, currentJson)
    }

    private fun findEditorText(component: java.awt.Component?): String? {
        component ?: return null
        if (component is com.intellij.openapi.editor.impl.EditorComponentImpl) {
            return component.editor.document.text
        }
        if (component is java.awt.Container) {
            for (child in component.components) {
                val result = findEditorText(child)
                if (result != null) return result
            }
        }
        return null
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
