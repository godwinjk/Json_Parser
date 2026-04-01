package com.godwin.jsonparser.action

import com.godwin.jsonparser.ui.dialog.ParserSettingsDialog
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ParserSettingsAction : AnAction(
    "Parser Settings",
    "Configure indentation and key sorting",
    AllIcons.General.Settings
) {
    override fun actionPerformed(e: AnActionEvent) {
        ParserSettingsDialog().show()
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
