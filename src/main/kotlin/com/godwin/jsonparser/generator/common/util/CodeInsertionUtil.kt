package com.godwin.jsonparser.generator.common.util

import com.godwin.jsonparser.generator.common.filetype.GenFileType
import com.godwin.jsonparser.generator.jsontodart.code_gen.DartClassImportDeclaration
import com.godwin.jsonparser.generator.jsontodart.code_gen.DartDataClassMaker
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.utils.*
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.util.*
import kotlin.math.max

object CodeInsertionUtil {

    fun insertDartCode(
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

    fun insertKotlinCode(
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

    fun insertJsonExample(
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

    fun calculateOffset(caret: Caret?, document: Document): Int {
        val offset = caret?.offset?.takeIf { it > 0 } ?: document.textLength

        val regex = RegexOption.MULTILINE
        val lastPackageIndex =
            "^[\\s]*package\\s.+\n$".toRegex(regex).findAll(document.text).lastOrNull()?.range?.last ?: -1
        val lastImportIndex =
            "^[\\s]*import\\s.+\n$".toRegex(regex).findAll(document.text).lastOrNull()?.range?.last ?: -1

        return maxOf(offset, lastPackageIndex + 1, lastImportIndex + 1)
    }

    fun cleanCurrentEditFile(document: Document, editorText: String = document.text) {
        val cleanText = getCleanText(editorText)
        document.setText(cleanText)
    }

    fun getCleanText(editorText: String): String {
        val tempCleanText = editorText.substringBeforeLast("class")
        return if (tempCleanText.trim().endsWith("data")) tempCleanText.trim().removeSuffix("data") else tempCleanText
    }

    fun getCurrentEditFileTemClassName(editorText: String) = editorText.substringAfterLast("class")
        .substringBefore("(").substringBefore("{").trim()

    fun couldGetAndReuseClassNameInCurrentEditFileForInsertCode(editorText: String): Boolean {
        try {
            val removeDocComment = editorText.replace(Regex("/\\*\\*(.|\\n)*\\*/", RegexOption.MULTILINE), "")
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
