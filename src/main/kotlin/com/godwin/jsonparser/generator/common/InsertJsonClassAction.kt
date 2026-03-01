package com.godwin.jsonparser.generator.common

import com.godwin.jsonparser.common.exception.UnSupportJsonException
import com.godwin.jsonparser.generator.common.filetype.GenFileType
import com.godwin.jsonparser.generator.common.ui.JsonInputDialog
import com.godwin.jsonparser.generator.common.util.CodeStyleUtils
import com.godwin.jsonparser.generator.jsontodart.code_gen.DartClassImportDeclaration
import com.godwin.jsonparser.generator.jsontodart.code_gen.DartDataClassMaker
import com.godwin.jsonparser.generator.jsontokotlin.feedback.dealWithException
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.utils.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.net.URL
import java.util.*
import kotlin.math.max

/**
 * Plugin action
 * Created by Godwin on 2024/12/20
 */
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

            val tempClassName = if (couldGetAndReuseClassNameInCurrentEditFileForInsertCode(editorText)) {
                getCurrentEditFileTemClassName(editorText)
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
                    cleanCurrentEditFile(document)
                }
            }

            val offset = calculateOffset(caret, document)
            val success = if (fileType == GenFileType.Dart) {
                insertDartCode(project, document, className, jsonString, offset)
            } else {
                insertKotlinCode(project, document, className, jsonString, offset)
            }

            if (success) {
                val shouldAppendJson = if (fileType == GenFileType.Dart) {
                    DartConfigManager.isAppendOriginalJson
                } else {
                    KotlinConfigManager.isAppendOriginalJson
                }
                if (shouldAppendJson) {
                    insertJsonExample(project, document, jsonString, offset, fileType)
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

    private fun insertDartCode(
        project: Project,
        document: Document,
        className: String,
        jsonString: String,
        offset: Int
    ): Boolean {
        val virtualFile = FileDocumentManager.getInstance().getFile(document)
        DartClassImportDeclaration.insertImportClassCode(
            project,
            document,
            virtualFile?.name ?: className
        )

        val codeMaker = try {
            DartDataClassMaker(className, jsonString)
        } catch (e: IllegalFormatFlagsException) {
            Messages.showErrorDialog(e.message, "UnSupport Json")
            return false
        }

        val generateClassesString = codeMaker.makeDartDataClassCode()

        executeCouldRollBackAction(project) {
            val currentOffset = if (offset <= 0) {
                val normalizedText = document.text.replace("\r\n", "\n")
                val regex = RegexOption.MULTILINE

                val packageIndex = "^[\\s]*package\\s.+$".toRegex(regex).find(normalizedText)?.range?.endInclusive ?: -1
                val lastImportIndex =
                    "^[\\s]*import\\s.+$".toRegex(regex).findAll(normalizedText).lastOrNull()?.range?.endInclusive ?: -1
                val lastPartIndex =
                    "^[\\s]*part\\s.+$".toRegex(regex).findAll(normalizedText).lastOrNull()?.range?.endInclusive ?: -1

                maxOf(packageIndex, lastImportIndex, lastPartIndex, 0) + 1
            } else {
                document.textLength
            }

            document.insertString(currentOffset, "\n\n$generateClassesString")
            CodeStyleUtils.reFormat(document, project)
        }
        return true
    }

    private fun insertKotlinCode(
        project: Project,
        document: Document,
        className: String,
        jsonString: String,
        offset: Int
    ): Boolean {
        KotlinClassImportDeclarationWriter.insertImportClassCode(project, document)

        val codeMaker = try {
            val kotlinClass = KotlinClassMaker(className, jsonString).makeKotlinClass()
            KotlinClassCodeMaker(kotlinClass, jsonString.isJSONSchema())
        } catch (e: IllegalFormatFlagsException) {
            Messages.showErrorDialog(e.message, "UnSupport Json")
            return false
        }

        val generateClassesString = codeMaker.makeKotlinClassCode()

        executeCouldRollBackAction(project) {
            document.insertString(
                max(offset, 0),
                ClassCodeFilter.removeDuplicateClassCode(generateClassesString)
            )
            CodeStyleUtils.reFormat(document, project)
        }
        return true
    }

    private fun insertJsonExample(
        project: Project?,
        document: Document,
        jsonString: String,
        offset: Int,
        fileType: GenFileType
    ) {
        val jsonExample = if (fileType == GenFileType.Dart) {
            jsonString.toDartDocMultilineComment()
        } else {
            jsonString.toJavaDocMultilineComment()
        }
        executeCouldRollBackAction(project) {
            document.insertString(
                max(offset, 0),
                jsonExample
            )
        }
    }

    private fun calculateOffset(caret: Caret?, document: Document): Int {
        val offset = caret?.offset?.takeIf { it > 0 } ?: document.textLength

        val regex = RegexOption.MULTILINE
        val lastPackageIndex =
            "^[\\s]*package\\s.+\n$".toRegex(regex).findAll(document.text).lastOrNull()?.range?.last ?: -1
        val lastImportIndex =
            "^[\\s]*import\\s.+\n$".toRegex(regex).findAll(document.text).lastOrNull()?.range?.last ?: -1

        return maxOf(offset, lastPackageIndex + 1, lastImportIndex + 1)
    }

    private fun cleanCurrentEditFile(document: Document, editorText: String = document.text) {
        val cleanText = getCleanText(editorText)
        document.setText(cleanText)
    }

    fun getCleanText(editorText: String): String {
        val tempCleanText = editorText.substringBeforeLast("class")
        return if (tempCleanText.trim().endsWith("data")) tempCleanText.trim().removeSuffix("data") else tempCleanText
    }

    fun getCurrentEditFileTemClassName(editorText: String) = editorText.substringAfterLast("class")
        .substringBefore("(").substringBefore("{").trim()

    /**
     * whether we could reuse current class name declared in the edit file for inserting data class code
     * if we could use it,then we would clean the kotlin file as it was new file without any class code .
     */
    fun couldGetAndReuseClassNameInCurrentEditFileForInsertCode(editorText: String): Boolean {
        try {
            val removeDocComment = editorText.replace(Regex("/\\*\\*(.|\n)*\\*/", RegexOption.MULTILINE), "")
            val removeDocCommentAndPackageDeclareText = removeDocComment
                .replace(Regex("^(?:\\s*package |\\s*import ).*$", RegexOption.MULTILINE), "")

            removeDocCommentAndPackageDeclareText.run {
                if (numberOf("class") == 1 &&
                    (substringAfter("class").containsAnyOf(listOf("(", ":", "=")).not()
                            || substringAfter("class").substringAfter("(").replace(
                        Regex("\\s"),
                        ""
                    ).let { it == ")" || it == "){}" })
                ) {
                    return true
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return false
    }
}
