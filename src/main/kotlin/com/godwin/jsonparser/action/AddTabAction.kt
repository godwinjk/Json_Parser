package com.godwin.jsonparser.action

import com.godwin.jsonparser.ui.IParserWidget
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class AddTabAction(private val parserWidget: IParserWidget) : AnAction(
    "Add Tab",
    "Create new parser tab",
    AllIcons.General.Add
) {
    override fun actionPerformed(e: AnActionEvent) {
        parserWidget.createParserSession()
        println("AddTabAction.actionPerformed")
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isVisible = parserWidget.getTabCount() <= 10
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
