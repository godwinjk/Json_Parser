package com.godwin.jsonparser.listeners

import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener

class ToolWindowListener: ToolWindowManagerListener {


    override fun toolWindowShown(toolWindow: ToolWindow) {
        super.toolWindowShown(toolWindow)
    }

    override fun toolWindowsRegistered(ids: MutableList<String>, toolWindowManager: ToolWindowManager) {
        super.toolWindowsRegistered(ids, toolWindowManager)
    }

    override fun toolWindowUnregistered(id: String, toolWindow: ToolWindow) {
        super.toolWindowUnregistered(id, toolWindow)
    }

    override fun stateChanged(toolWindowManager: ToolWindowManager) {
        super.stateChanged(toolWindowManager)
    }




}