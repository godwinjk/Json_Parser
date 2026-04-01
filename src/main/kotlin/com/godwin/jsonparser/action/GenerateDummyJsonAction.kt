package com.godwin.jsonparser.action

import com.godwin.jsonparser.ui.IParserWidget
import com.godwin.jsonparser.ui.dialog.GenerateDummyJsonDialog
import com.godwin.jsonparser.util.DummyJsonGenerator
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class GenerateDummyJsonAction(private val parserWidget: IParserWidget) : AnAction(
    "Generate Dummy JSON",
    "Generate fake JSON data in a new parser tab",
    AllIcons.Json.Object
) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val dialog = GenerateDummyJsonDialog(project)
        if (!dialog.showAndGet()) return

        val json = if (dialog.isArray) {
            DummyJsonGenerator.generateArray(dialog.propertyCount, dialog.depth, dialog.arraySize)
        } else {
            DummyJsonGenerator.generateObject(dialog.propertyCount, dialog.depth)
        }

        parserWidget.createParserSessionWithContent(json)
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
