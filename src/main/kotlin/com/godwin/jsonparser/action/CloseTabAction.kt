package com.godwin.jsonparser.action

import com.godwin.jsonparser.ui.IParserWidget
import com.godwin.jsonparser.util.Log
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class CloseTabAction(private val parserWidget: IParserWidget) : AnAction(
    "Close Tab",
    "Close parser session",
    AllIcons.Actions.Cancel
) {
    override fun actionPerformed(e: AnActionEvent) {
        parserWidget.closeCurrentParserSession()
        Log.i("CloseTabAction.actionPerformed")
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isVisible = parserWidget.getTabCount() > 1
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
