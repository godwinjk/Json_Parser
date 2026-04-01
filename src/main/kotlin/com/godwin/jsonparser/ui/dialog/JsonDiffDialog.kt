package com.godwin.jsonparser.ui.dialog

import com.godwin.jsonparser.util.JsonUtils
import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project

object JsonDiffDialog {

    fun show(project: Project, leftJson: String) {
        val leftFormatted = try {
            if (leftJson.isBlank()) "" else JsonUtils.formatJson(leftJson)
        } catch (_: Exception) {
            leftJson
        }

        val factory = DiffContentFactory.getInstance()

        // Create writable documents via EditorFactory
        val leftDoc = EditorFactory.getInstance().createDocument(leftFormatted)
        val rightDoc = EditorFactory.getInstance().createDocument("")

        // Both documents are writable by default when created this way
        leftDoc.setReadOnly(false)
        rightDoc.setReadOnly(false)

        // Auto-format right side when valid JSON is pasted
        rightDoc.addDocumentListener(object : com.intellij.openapi.editor.event.DocumentListener {
            override fun documentChanged(event: com.intellij.openapi.editor.event.DocumentEvent) {
                val text = rightDoc.text
                if (text.isBlank() || !JsonUtils.isValidJson(text)) return
                val formatted = try {
                    JsonUtils.formatJson(text)
                } catch (_: Exception) {
                    return
                }
                if (formatted == text) return
                ApplicationManager.getApplication().invokeLater {
                    com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(project) {
                        rightDoc.setText(formatted)
                    }
                }
            }
        })

        val leftContent = factory.create(project, leftDoc)
        val rightContent = factory.create(project, rightDoc)

        val request = SimpleDiffRequest(
            "JSON Diff",
            leftContent,
            rightContent,
            "Original (current tab)",
            "Compare (paste JSON here)"
        )

        DiffManager.getInstance().showDiff(project, request)
    }
}
