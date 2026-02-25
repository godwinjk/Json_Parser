package com.godwin.jsonparser.generator.common.util

import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager

object CodeStyleUtils {

    fun reFormat(document: Document, project: Project) {
        val psiDocumentManager = PsiDocumentManager.getInstance(project)
        psiDocumentManager.commitDocument(document)

        val psiFile = psiDocumentManager.getPsiFile(document)
        if (psiFile != null) {
            CodeStyleManager.getInstance(project).reformat(psiFile)
            ReformatCodeProcessor(project, psiFile, null, false).run()
        }
    }

    fun reFormat(psiFile: PsiFile, project: Project) {
        CodeStyleManager.getInstance(project).reformat(psiFile)
        ReformatCodeProcessor(project, psiFile, null, false).run()
    }
}