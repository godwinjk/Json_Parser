package com.godwin.jsonparser.util.repair

import com.intellij.json.JsonFileType
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.LightVirtualFile

class PsiRepairStrategy : RepairStrategy() {

    private companion object {
        const val MAX_ITERATIONS = 5
    }

    override fun repair(project: Project, input: String): String {
        var text = input.trim()
        text = preProcessJson(text)

        val virtualFile = LightVirtualFile("repair.json", JsonFileType.INSTANCE, text)
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return text
        val document = PsiDocumentManager.getInstance(project).getDocument(psiFile) ?: return text

        var iteration = 0
        var previousErrorCount = Int.MAX_VALUE

        while (iteration < MAX_ITERATIONS) {
            PsiDocumentManager.getInstance(project).commitDocument(document)

            val errors = collectErrors(psiFile)
            if (errors.isEmpty()) break
            if (errors.size >= previousErrorCount) break

            previousErrorCount = errors.size

            WriteCommandAction.runWriteCommandAction(project) {
                errors.sortedByDescending { it.textRange.startOffset }.forEach { error ->
                    fixError(error, document)
                }
            }

            iteration++
        }

        PsiDocumentManager.getInstance(project).commitDocument(document)

        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).reformat(psiFile)
        }

        return document.text
    }

    private fun preProcessJson(text: String): String {
        var result = text
        result = result.replace("'", "\"")
        result = result.replace("True", "true")
        result = result.replace("False", "false")
        result = result.replace("NULL", "null")
        result = result.replace("None", "null")
        result = Regex("//.*?$", RegexOption.MULTILINE).replace(result, "")
        result = Regex("/\\*.*?\\*/", RegexOption.DOT_MATCHES_ALL).replace(result, "")
        result = Regex("([{,])\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*:").replace(result, "$1\"$2\":")
        return result
    }

    private fun collectErrors(file: PsiFile): List<PsiErrorElement> {
        val list = mutableListOf<PsiErrorElement>()
        file.accept(object : PsiRecursiveElementVisitor() {
            override fun visitErrorElement(element: PsiErrorElement) {
                list.add(element)
            }
        })
        return list
    }

    private fun fixError(error: PsiErrorElement, document: Document) {
        val msg = error.errorDescription.lowercase()
        val offset = error.textRange.startOffset

        try {
            when {
                msg.contains("',' expected") || msg.contains("comma expected") -> {
                    if (offset <= document.textLength) {
                        document.insertString(offset, ",")
                    }
                }

                msg.contains("':' expected") || msg.contains("colon expected") -> {
                    if (offset <= document.textLength) {
                        document.insertString(offset, ":")
                    }
                }

                msg.contains("trailing comma") -> {
                    removeTrailingComma(error, document)
                }

                msg.contains("double-quoted") || msg.contains("property key") -> {
                    fixUnquotedKey(error, document)
                }

                msg.contains("value expected") -> {
                    if (offset <= document.textLength) {
                        document.insertString(offset, "null")
                    }
                }

                msg.contains("'}' expected") -> {
                    document.insertString(document.textLength, "}")
                }

                msg.contains("']' expected") -> {
                    document.insertString(document.textLength, "]")
                }

                msg.contains("invalid") || msg.contains("unexpected") -> {
                    if (error.textRange.endOffset <= document.textLength) {
                        document.deleteString(offset, error.textRange.endOffset)
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun removeTrailingComma(error: PsiErrorElement, document: Document) {
        try {
            var prev = error.prevSibling
            while (prev != null && prev.text.isBlank()) {
                prev = prev.prevSibling
            }
            if (prev?.text?.trim() == ",") {
                val start = prev.textRange.startOffset
                val end = prev.textRange.endOffset
                if (end <= document.textLength) {
                    document.deleteString(start, end)
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun fixUnquotedKey(error: PsiErrorElement, document: Document) {
        try {
            val parent = error.parent ?: return
            val text = parent.text
            val colon = text.indexOf(":")
            if (colon > 0) {
                val key = text.substring(0, colon).trim()
                if (key.isNotEmpty() && !key.startsWith("\"") && key.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*"))) {
                    val fixed = "\"$key\"${text.substring(colon)}"
                    val start = parent.textRange.startOffset
                    val end = parent.textRange.endOffset
                    if (end <= document.textLength) {
                        document.replaceString(start, end, fixed)
                    }
                }
            }
        } catch (e: Exception) {
        }
    }
}
