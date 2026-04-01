package com.godwin.jsonparser.action

import com.godwin.jsonparser.util.EditorHintsNotifier
import com.godwin.jsonparser.util.Log
import com.godwin.jsonparser.util.analytics.AnalyticsConstant
import com.godwin.jsonparser.util.analytics.AnalyticsService
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.Icon

class CopyToClipBoardAction(
    text: String?,
    description: String?,
    icon: Icon?
) : AnAction(text, description, icon) {

    override fun actionPerformed(e: AnActionEvent) {
        AnalyticsService.track(AnalyticsConstant.ACTION_COPY_TO_CLIPBOARD)
        try {
            val project = e.project ?: return

            // 1. Try the standard way
            var editor = e.getData(CommonDataKeys.EDITOR)

            // 2. Try the "Even if Inactive" way
            if (editor == null) {
                editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE)
            }

            // 3. FORCE IT from the FileEditorManager (The ultimate fallback)
            if (editor == null) {
                editor = FileEditorManager.getInstance(project).selectedTextEditor
            }
            if (editor == null) {
                Log.i("Not in editor window")
                Notifications.Bus.notifyAndHide(
                    Notification(
                        "Json Parser",
                        "Json Parser",
                        "Text not copied. Please select text and press copy to clipboard button.",
                        NotificationType.ERROR
                    ), e.project
                )
                return
            }

            val selectText = editor.selectionModel.selectedText
            if (selectText.isNullOrEmpty()) {
                Log.i("selectText == null")
                EditorHintsNotifier.notifyError(editor, "Nothing selected (ctrl+A to select all)", 0)
                return
            }

            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(selectText), null)
            EditorHintsNotifier.notifyInfo(editor, "Text copied")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR) ?: FileEditorManager.getInstance(project!!).selectedTextEditor

        // Set visibility/enabled state based on whether ANY editor exists
        e.presentation.isEnabledAndVisible = (editor != null)
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
