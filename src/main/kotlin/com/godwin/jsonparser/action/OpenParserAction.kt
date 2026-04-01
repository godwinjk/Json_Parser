package com.godwin.jsonparser.action

import com.godwin.jsonparser.constants.TOOL_WINDOW_ID
import com.godwin.jsonparser.rx.Subscriber
import com.godwin.jsonparser.util.Log
import com.godwin.jsonparser.util.analytics.AnalyticsConstant
import com.godwin.jsonparser.util.analytics.AnalyticsService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.wm.ToolWindowManager

class OpenParserAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        AnalyticsService.track(AnalyticsConstant.ACTION_OPEN_PARSER)
        try {
            val project = e.project ?: return
            val editor = FileEditorManager.getInstance(project).selectedTextEditor
            if (editor == null) {
                Log.i("Not in editor window")
                return
            }

            val selectText = editor.selectionModel.selectedText
            if (selectText.isNullOrEmpty()) {
                Log.i("selectText == null : ${selectText == null}")
                return
            }

            ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID)?.activate({
                Subscriber.publishMessage(selectText)
            }, true, true)
        } catch (e: Exception) {
            Log.e(e.message)
        }
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
