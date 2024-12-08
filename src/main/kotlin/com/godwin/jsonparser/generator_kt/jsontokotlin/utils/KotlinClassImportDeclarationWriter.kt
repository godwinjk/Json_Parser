package com.godwin.jsonparser.generator_kt.jsontokotlin.utils

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.InterceptorManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import kotlin.math.max

/**
 * to be a helper to insert Import class declare code
 * Created by Godwin on 2024/12/20
 */


object KotlinClassImportDeclarationWriter : IClassImportDeclarationWriter {


    override fun insertImportClassCode(project: Project?, editFile: Document) {

        val text = editFile.text

        val interceptedImportClassDeclaration = ClassImportDeclaration.applyImportClassDeclarationInterceptors(
            InterceptorManager.getEnabledImportClassDeclarationInterceptors()
        )

        interceptedImportClassDeclaration.split("\n").forEach { importClassLineString ->

            if (importClassLineString !in text) {

                val packageIndex = try {
                    "^[\\s]*package\\s.+\n$".toRegex(RegexOption.MULTILINE).find(text)!!.range.last
                } catch (e: Exception) {
                    -1
                }
                val lastImportKeywordIndex = try {
                    "^[\\s]*import\\s.+\n$".toRegex(RegexOption.MULTILINE).findAll(text).last().range.last
                } catch (e: Exception) {
                    -1
                }
                val index = max(lastImportKeywordIndex, packageIndex)
                val insertIndex =
                    if (index == -1) 0 else editFile.getLineEndOffset(editFile.getLineNumber(index))

                executeCouldRollBackAction(project) {
                    editFile.insertString(insertIndex, "\n" + importClassLineString + "\n")
                }

            }
        }
    }

}


