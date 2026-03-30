package com.godwin.jsonparser.action

import com.godwin.jsonparser.ui.IParserWidget
import com.godwin.jsonparser.util.JsonDownloader
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.util.DispatchThreadProgressWindow
import com.intellij.openapi.ui.Messages

class LoadFromUrlAction(private val parserWidget: IParserWidget) : AnAction(
    "Load from URL",
    "Retrieve JSON from a URL in a new parser tab",
    AllIcons.Toolwindows.WebToolWindow
) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val inputData = Messages.showMultilineInputDialog(
            project,
            "Retrieve Content from Http URL\n\nTip: Paste your header in NEXT LINE with a colon(:)",
            "URL",
            null, null, null
        ) ?: return

        if (inputData.isBlank()) return

        val progressWindow = DispatchThreadProgressWindow(false, project)
        progressWindow.isIndeterminate = true
        progressWindow.setRunnable {
            try {
                val data = JsonDownloader.getData(inputData)
                parserWidget.createParserSessionWithContent(data)
            } finally {
                progressWindow.stop()
            }
        }
        progressWindow.start()
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
