package com.godwin.jsonparser

import com.godwin.jsonparser.services.ProjectService
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow

/**
 * Created by Godwin on 4/21/2018 12:32 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
class ParserToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val parser = ProjectService.getInstance()
        parser.initParser(toolWindow)
    }

    companion object {
        const val TOOL_WINDOW_ID = "Json Parser"
    }
}