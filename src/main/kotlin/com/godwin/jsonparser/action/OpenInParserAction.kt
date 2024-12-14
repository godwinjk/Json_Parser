package com.godwin.jsonparser.action

import com.godwin.jsonparser.rx.Subscriber.publishMessage
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import java.nio.charset.Charset

class OpenInParserAction : AnAction("Open In Json Parser") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        try {
            if (virtualFile != null && project != null) {
                // Your custom logic for handling the file (e.g., opening in a custom viewer)
                // For example, open a Lottie viewer, open in a browser, etc.
                val window =
                    ToolWindowManager.getInstance(project).getToolWindow("Json Parser")
                val string = readFileContent(virtualFile)
                window?.activate({
                    publishMessage(string)
                }, true, true)
            }
        } catch (e: Exception) {
            Messages.showErrorDialog(project, "Error reading file content", "Error")
        }
    }

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isVisible = file != null && file.extension == "json" // Example condition (only for JSON files)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    private fun readFileContent(file: VirtualFile): String {
        // Open the file in a read-only mode and read its content
        return file.inputStream.use {
            it.bufferedReader(Charset.defaultCharset()).readText()
        }
    }
}