package com.godwin.jsonparser.action

import com.godwin.jsonparser.ui.onboarding.TutorialManager
import com.godwin.jsonparser.ui.onboarding.TutorialSteps
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import javax.swing.JComponent

class StartOnboardingAction : AnAction(
    "Start Onboarding",
    "Interactive tour of all JSON Parser features",
    AllIcons.Actions.Help
) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        // Find the tool window root component to attach the overlay
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Json Parser") ?: return
        val root = toolWindow.component as? JComponent ?: return
        TutorialManager.start(TutorialSteps.all(), root)
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
