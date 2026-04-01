package com.godwin.jsonparser.listeners

import com.godwin.jsonparser.constants.TOOL_WINDOW_ID
import com.godwin.jsonparser.services.JsonPersistence
import com.godwin.jsonparser.ui.tutorial.TutorialManager
import com.godwin.jsonparser.ui.tutorial.TutorialSteps
import com.godwin.jsonparser.util.Log
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import javax.swing.JComponent
import javax.swing.Timer

class ToolWindowListener : ToolWindowManagerListener {

    override fun stateChanged(toolWindowManager: ToolWindowManager) {
        super.stateChanged(toolWindowManager)
        val toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID) ?: return
        val visible = toolWindow.isVisible
        Log.i("ToolWindow is visible: $visible")

        if (!visible) return

        val prefs = try { JsonPersistence.getInstance() } catch (_: Exception) { return }
        if (prefs.onboardingShown) return

        // First time the tool window is opened — show tutorial
        prefs.onboardingShown = true
        val root = toolWindow.component as? JComponent ?: return
        Timer(800) {
            TutorialManager.start(TutorialSteps.all())
        }.apply { isRepeats = false; start() }
    }
}
