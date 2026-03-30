package com.godwin.jsonparser.action

import com.godwin.jsonparser.ui.IParserWidget
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.vfs.LocalFileSystem

class LoadFromFileAction(private val parserWidget: IParserWidget) : AnAction(
    "Load from File",
    "Open a JSON file in a new parser tab",
    AllIcons.Actions.MenuOpen
) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        FileChooser.chooseFile(
            FileChooserDescriptor(true, false, false, false, false, false),
            project,
            LocalFileSystem.getInstance().findFileByPath(project.basePath ?: "")
        ) { file ->
            try {
                val content = String(file.contentsToByteArray()).replace("\r\n", "\n")
                parserWidget.createParserSessionWithContent(content)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
