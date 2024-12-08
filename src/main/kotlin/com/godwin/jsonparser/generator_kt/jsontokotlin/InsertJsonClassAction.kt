package com.godwin.jsonparser.generator_kt.jsontokotlin

import com.godwin.jsonparser.generator.jsontodart.DartClassImportDeclarationWriter
import com.godwin.jsonparser.generator.jsontodart.DartDataClassCodeMaker
import com.godwin.jsonparser.generator.jsontodart.filetype.GenFileType
import com.godwin.jsonparser.generator_kt.jsontokotlin.feedback.dealWithException
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.UnSupportJsonException
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.JsonInputDialog
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.*
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
            val project = event.getData(PlatformDataKeys.PROJECT)
            val caret = event.getData(PlatformDataKeys.CARET)
            val editor = event.getData(PlatformDataKeys.EDITOR_EVEN_IF_INACTIVE)
            if (couldNotInsertCode(editor)) return
            val document = editor?.document ?: return
            val editorText = document.text

            /**
             *  temp class name for insert
             */
            var tempClassName = ""
            val couldGetAndReuseClassNameInCurrentEditFileForInsertCode =
                couldGetAndReuseClassNameInCurrentEditFileForInsertCode(editorText)

            if (couldGetAndReuseClassNameInCurrentEditFileForInsertCode) {
                /**
                 * auto obtain the current class name
                 */
                tempClassName = getCurrentEditFileTemClassName(editorText)
            }
            val inputDialog = JsonInputDialog(tempClassName, project!!)
            inputDialog.show()
            val className = inputDialog.getClassName()
            val inputString = inputDialog.inputString
            val json = if (inputString.startsWith("http")) {
                URL(inputString).readText()
            } else inputString
            if (json.isEmpty()) {
                return
            }
            jsonString = json
            val fileType = inputDialog.getFileType()
            if (reuseClassName(couldGetAndReuseClassNameInCurrentEditFileForInsertCode, className, tempClassName)) {
                executeCouldRollBackAction(project) {
                    /**
                     * if you don't clean then we will trick a conflict with two same class name error
                     */
                    cleanCurrentEditFile(document)
                }
            }
            val offset = calculateOffset(caret, document)

            if (fileType == GenFileType.Dart) {
                if (insertDartCode(project, document, className, jsonString, offset)) {
                    if (DartConfigManager.isAppendOriginalJson) {
                        insertJsonExample(project, document, jsonString, offset, fileType)
                    }
                }
            } else {
                if (insertKotlinCode(project, document, className, jsonString, offset)) {
                    if (KotlinConfigManager.isAppendOriginalJson) {
                        insertJsonExample(project, document, jsonString, offset, fileType)
                    }
                }
            }

        } catch (e: UnSupportJsonException) {
            val advice = e.advice
            Messages.showInfoMessage(dealWithHtmlConvert(advice), "Tip")
        } catch (e: Throwable) {
            dealWithException(jsonString, e)
            throw e
        }
    }

    private fun dealWithHtmlConvert(advice: String) = advice.replace("<", "&lt;").replace(">", "&gt;")

    private fun reuseClassName(
        couldGetAndReuseClassNameInCurrentEditFileForInserCode: Boolean,
        className: String,
        tempClassName: String
    ) = couldGetAndReuseClassNameInCurrentEditFileForInserCode && className == tempClassName

    private fun couldNotInsertCode(editor: Editor?): Boolean {
        if (editor == null || editor.document.isWritable.not()) {
            Messages.showWarningDialog(
                "Please open a file in edited state for inserting Kotlin code!",
                "No Edited File"
            )
            return true
        }
        return false
    }

    private fun insertDartCode(
        project: Project?,
        document: Document,
        className: String,
        jsonString: String,
        offset: Int
    ): Boolean {
        val virtualFile = FileDocumentManager.getInstance().getFile(document)
        DartClassImportDeclarationWriter.insertImportClassCode(
            project,
            document,
            virtualFile?.name ?: className
        )
        var currentOffset = offset
        val codeMaker: DartDataClassCodeMaker
        try {
            //passing current file directory along with className and json
            codeMaker = DartDataClassCodeMaker(className, jsonString)
        } catch (e: IllegalFormatFlagsException) {
            e.printStackTrace()
            Messages.showErrorDialog(e.message, "UnSupport Json")
            return false
        }

        val generateClassesString = codeMaker.makeDartDataClassCode()

        executeCouldRollBackAction(project) {
            if (offset <= 0) {
                if (offset == 0) {
                    currentOffset = document.textLength
                }
                val normalizedText = document.text.replace("\r\n", "\n")

                val packageIndex = "^[\\s]*package\\s.+$".toRegex(RegexOption.MULTILINE)
                    .find(normalizedText)
                    ?.range
                    ?.endInclusive ?: -1

                val lastImportKeywordIndex = "^[\\s]*import\\s.+$".toRegex(RegexOption.MULTILINE)
                    .findAll(normalizedText)
                    .lastOrNull()
                    ?.range
                    ?.endInclusive ?: -1

                val lastPartKeyWord = "^[\\s]*part\\s.+$".toRegex(RegexOption.MULTILINE)
                    .findAll(normalizedText)
                    .lastOrNull()
                    ?.range
                    ?.endInclusive ?: -1


                if (offset < packageIndex) {
                    currentOffset = packageIndex + 1
                }
                if (offset < lastImportKeywordIndex) {
                    currentOffset = lastImportKeywordIndex + 1
                }
                if (offset < lastPartKeyWord) {
                    currentOffset = lastPartKeyWord + 1
                }
            } else {
                currentOffset = document.textLength
            }
            document.insertString(
                currentOffset.coerceAtLeast(0),
                "\n\n$generateClassesString"
            )
        }
        return true
    }

    private fun insertKotlinCode(
        project: Project?,
        document: Document,
        className: String,
        jsonString: String,
        offset: Int
    ): Boolean {
        KotlinClassImportDeclarationWriter.insertImportClassCode(project, document)

        val codeMaker: KotlinClassCodeMaker
        try {
            //passing current file directory along with className and json
            val kotlinClass = KotlinClassMaker(className, jsonString).makeKotlinClass()
            codeMaker = KotlinClassCodeMaker(kotlinClass, jsonString.isJSONSchema())
        } catch (e: IllegalFormatFlagsException) {
            e.printStackTrace()
            Messages.showErrorDialog(e.message, "UnSupport Json")
            return false
        }

        val generateClassesString = codeMaker.makeKotlinClassCode()

        executeCouldRollBackAction(project) {
            document.insertString(
                max(offset, 0),
                ClassCodeFilter.removeDuplicateClassCode(generateClassesString)
            )
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
        var offset: Int
        if (caret != null) {

            offset = caret.offset
            if (offset == 0) {
                offset = document.textLength
            }
            val lastPackageKeywordLineEndIndex = try {
                "^[\\s]*package\\s.+\n$".toRegex(RegexOption.MULTILINE).findAll(document.text).last().range.last
            } catch (e: Exception) {
                -1
            }
            val lastImportKeywordLineEndIndex = try {
                "^[\\s]*import\\s.+\n$".toRegex(RegexOption.MULTILINE).findAll(document.text).last().range.last
            } catch (e: Exception) {
                -1
            }
            if (offset < lastPackageKeywordLineEndIndex) {
                offset = lastPackageKeywordLineEndIndex + 1
            }
            if (offset < lastImportKeywordLineEndIndex) {
                offset = lastImportKeywordLineEndIndex + 1
            }

        } else {
            offset = document.textLength
        }

        return offset
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
