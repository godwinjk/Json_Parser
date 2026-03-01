package com.godwin.jsonparser.generator.jsontodart.code_gen

import com.godwin.jsonparser.generator.jsontodart.interceptor.InterceptorManager
import com.godwin.jsonparser.generator.jsontodart.utils.ClassImportDeclaration
import com.godwin.jsonparser.generator.jsontodart.utils.executeCouldRollBackAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project

object DartClassImportDeclaration : IClassImportDeclaration {

    override fun insertImportClassCode(project: Project?, editFile: Document, className: String) {
        val text = editFile.text
        val interceptedImportClassDeclaration = ClassImportDeclaration.applyImportClassDeclarationInterceptors(
            InterceptorManager.getEnabledImportClassDeclarationInterceptors(), className
        )

        val importText = buildImportText(interceptedImportClassDeclaration, text)
        if (importText.isEmpty()) return

        val insertIndex = calculateInsertIndex(editFile, text)
        val prefix = if (text.isBlank()) "" else "\n"

        executeCouldRollBackAction(project) {
            editFile.insertString(insertIndex, prefix + importText)
        }
    }

    private fun buildImportText(importDeclaration: String, existingText: String): String {
        val lines = importDeclaration.split("\n")
        val result = StringBuilder()
        var previousFirstWord = ""

        lines.forEach { line ->
            if (line !in existingText && line !in result) {
                val currentFirstWord = line.substringBefore(" ")
                val needsExtraNewline = previousFirstWord.isNotEmpty() && previousFirstWord != currentFirstWord

                if (result.isNotEmpty()) {
                    result.append("\n")
                    if (needsExtraNewline) result.append("\n")
                }
                result.append(line)
                previousFirstWord = currentFirstWord
            }
        }

        return result.toString()
    }

    private fun calculateInsertIndex(editFile: Document, text: String): Int {
        val normalizedText = text.replace("\r\n", "\n")
        val regex = RegexOption.MULTILINE

        val packageIndex = "^[\\s]*package\\s.+$".toRegex(regex).find(normalizedText)?.range?.endInclusive ?: -1
        val lastImportIndex =
            "^[\\s]*import\\s.+$".toRegex(regex).findAll(normalizedText).lastOrNull()?.range?.endInclusive ?: -1
        val lastPartIndex =
            "^[\\s]*part\\s.+$".toRegex(regex).findAll(normalizedText).lastOrNull()?.range?.endInclusive ?: -1

        val index = maxOf(packageIndex, lastImportIndex, lastPartIndex)
        return if (index == -1) 0 else editFile.getLineEndOffset(editFile.getLineNumber(index))
    }
}


