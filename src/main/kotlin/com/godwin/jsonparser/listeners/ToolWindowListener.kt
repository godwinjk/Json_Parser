package com.godwin.jsonparser.listeners

import com.godwin.jsonparser.constants.TOOL_WINDOW_ID
import com.godwin.jsonparser.util.Log
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener

class ToolWindowListener : ToolWindowManagerListener {

    override fun stateChanged(toolWindowManager: ToolWindowManager) {
        super.stateChanged(toolWindowManager)
        val visible = toolWindowManager.getToolWindow(TOOL_WINDOW_ID)?.isVisible
        Log.i("ToolWindow is visible: $visible")

    }

}