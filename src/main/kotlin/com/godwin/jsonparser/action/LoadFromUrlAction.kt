package com.godwin.jsonparser.action

import com.godwin.jsonparser.ui.IParserWidget
import com.godwin.jsonparser.ui.dialog.HttpClientDialog
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class LoadFromUrlAction(private val parserWidget: IParserWidget) : AnAction(
    "Load from URL",
    "Retrieve JSON from a URL in a new parser tab",
    AllIcons.Actions.Download
) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val dialog = HttpClientDialog(project) { json ->
            parserWidget.createParserSessionWithContent(json)
        }
        dialog.isVisible = true
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
