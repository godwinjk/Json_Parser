package com.godwin.jsonparser.generator.jsontodart

import com.godwin.jsonparser.generator.jsontodart.interceptor.InterceptorManager
import com.godwin.jsonparser.generator.jsontodart.utils.ClassImportDeclaration
import com.godwin.jsonparser.generator.jsontodart.utils.executeCouldRollBackAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project

/**
 * to be a helper to insert Import class declare code
 * Created by Godwin on 2024/12/20.
 */

object DartClassImportDeclarationWriter : IClassImportDeclarationWriter {

    override fun insertImportClassCode(project: Project?, editFile: Document, className: String) {

        var text = editFile.text
        var importText = ""
        val interceptedImportClassDeclaration = ClassImportDeclaration.applyImportClassDeclarationInterceptors(
            InterceptorManager.getEnabledImportClassDeclarationInterceptors(), className
        )
        val listOfLines = interceptedImportClassDeclaration.split("\n")
        listOfLines.forEachIndexed { listIndex, importClassLineString ->
            if (importClassLineString !in text && importClassLineString !in importText) {
                //top section contains several parts like import package and part , then for formating adding a another \n
                val isLinesSimilar = if (listIndex != 0) {
                    val words = listOfLines[listIndex - 1].split(" ")
                    val currentWords = importClassLineString.split(" ")
                    //comparing the first words
                    if (words.isNotEmpty() && currentWords.isNotEmpty()) {
                        words[0] == currentWords[0]
                    } else {
                        false
                    }
                } else {
                    false
                }
                if (importText.isBlank()) {
                    importText += importClassLineString
                } else {
                    val separator = if (!isLinesSimilar) "\n" else ""

                    importText += "\n" + separator + importClassLineString
                }
            }
        }

        val normalizedText = text.replace("\r\n", "\n")

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

        val index = lastImportKeywordIndex.coerceAtLeast(packageIndex).coerceAtLeast(lastPartKeyWord)
        val insertIndex =
            if (index == -1) 0 else editFile.getLineEndOffset(editFile.getLineNumber(index))

        executeCouldRollBackAction(project) {
            if (text.isBlank()) {
                editFile.insertString(
                    insertIndex,
                    importText
                )
            } else {
                editFile.insertString(
                    insertIndex,
                    "\n" + importText
                )
            }
        }
    }
}


