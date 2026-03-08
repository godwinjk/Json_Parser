package com.godwin.jsonparser.generator.common

import com.godwin.jsonparser.common.exception.UnSupportJsonException
import com.godwin.jsonparser.generator.common.filetype.GenFileType
import com.godwin.jsonparser.generator.common.ui.JsonInputDialog
import com.godwin.jsonparser.generator.common.util.CodeInsertionUtil
import com.godwin.jsonparser.generator.jsontokotlin.feedback.dealWithException
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.utils.executeCouldRollBackAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Messages
import java.net.URL

class InsertJsonClassAction : AnAction("Dart/Kotlin Class from JSON") {
    override fun actionPerformed(event: AnActionEvent) {
        var jsonString = ""
        try {
            val project = event.getData(PlatformDataKeys.PROJECT) ?: return
            val caret = event.getData(PlatformDataKeys.CARET)
            val editor = event.getData(PlatformDataKeys.EDITOR_EVEN_IF_INACTIVE)
            if (couldNotInsertCode(editor)) return
            val document = editor?.document ?: return
            val editorText = document.text

            val tempClassName = if (CodeInsertionUtil.couldGetAndReuseClassNameInCurrentEditFileForInsertCode(editorText)) {
                CodeInsertionUtil.getCurrentEditFileTemClassName(editorText)
            } else ""

            val inputDialog = JsonInputDialog(tempClassName, project)
            inputDialog.show()
            val className = inputDialog.getClassName()
            val inputString = inputDialog.inputString
            val json = if (inputString.startsWith("http")) {
                URL(inputString).readText()
            } else inputString
            if (json.isEmpty()) return

            jsonString = json
            val fileType = inputDialog.getFileType()

            if (tempClassName.isNotEmpty() && className == tempClassName) {
                executeCouldRollBackAction(project) {
                    CodeInsertionUtil.cleanCurrentEditFile(document)
                }
            }

            val offset = CodeInsertionUtil.calculateOffset(caret, document)
            val success = if (fileType == GenFileType.Dart) {
                CodeInsertionUtil.insertDartCode(project, document, className, jsonString, offset)
            } else {
                CodeInsertionUtil.insertKotlinCode(project, document, className, jsonString, offset)
            }

            if (success) {
                val shouldAppendJson = if (fileType == GenFileType.Dart) {
                    DartConfigManager.isAppendOriginalJson
                } else {
                    KotlinConfigManager.isAppendOriginalJson
                }
                if (shouldAppendJson) {
                    CodeInsertionUtil.insertJsonExample(project, document, jsonString, offset, fileType)
                }
            }
        } catch (e: UnSupportJsonException) {
            Messages.showInfoMessage(dealWithHtmlConvert(e.advice), "Tip")
        } catch (e: Throwable) {
            dealWithException(jsonString, e)
            throw e
        }
    }

    private fun dealWithHtmlConvert(advice: String) = advice.replace("<", "&lt;").replace(">", "&gt;")

    private fun couldNotInsertCode(editor: Editor?): Boolean {
        if (editor?.document?.isWritable != true) {
            Messages.showWarningDialog(
                "Please open a file in edited state for inserting Kotlin code!",
                "No Edited File"
            )
            return true
        }
        return false
    }
}
