package com.godwin.jsonparser.action

import com.godwin.jsonparser.ui.tutorial.TutorialManager
import com.godwin.jsonparser.ui.tutorial.TutorialSteps
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class StartTutorialAction : AnAction(
    "Start Tutorial",
    "Interactive tour of all JSON Parser features",
    AllIcons.Actions.Help
) {
    override fun actionPerformed(e: AnActionEvent) {
        TutorialManager.start(TutorialSteps.all())
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
