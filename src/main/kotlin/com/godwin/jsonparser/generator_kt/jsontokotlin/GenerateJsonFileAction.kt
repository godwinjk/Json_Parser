package com.godwin.jsonparser.generator_kt.jsontokotlin

import com.godwin.jsonparser.generator.jsontodart.DartCodeMaker
import com.godwin.jsonparser.generator.jsontodart.filetype.GenFileType
import com.godwin.jsonparser.generator.jsontodart.utils.ClassCodeFilter
import com.godwin.jsonparser.generator.jsontodart.utils.DartClassFileGenerator
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.InterceptorManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.UnSupportJsonException
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.JsonInputDialog
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.KotlinClassFileGenerator
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.KotlinClassMaker
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.isJSONSchema
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiDirectoryFactory


/**
 * Created by Seal.Wu on 2018/4/18.
 */
class GenerateJsonFileAction : AnAction("Dart/Kotlin from JSON") {

    override fun actionPerformed(event: AnActionEvent) {
        try {
            val project = event.getData(PlatformDataKeys.PROJECT) ?: return

            val dataContext = event.dataContext
            val module = LangDataKeys.MODULE.getData(dataContext) ?: return

            val directory = when (val navigatable = LangDataKeys.NAVIGATABLE.getData(dataContext)) {
                is PsiDirectory -> navigatable
                is PsiFile -> navigatable.containingDirectory
                else -> {
                    val root = ModuleRootManager.getInstance(module)
                    root.sourceRoots.asSequence().mapNotNull {
                        PsiManager.getInstance(project).findDirectory(it)
                    }.firstOrNull()
                }
            } ?: return

            val directoryFactory = PsiDirectoryFactory.getInstance(directory.project)
            val packageName = directoryFactory.getQualifiedName(directory, false)
            val psiFileFactory = PsiFileFactory.getInstance(project)
            val packageDeclare = if (packageName.isNotEmpty()) "package $packageName" else ""
            val inputDialog = JsonInputDialog("", project)
            inputDialog.show()
            val className = inputDialog.getClassName()
            val inputString = inputDialog.inputString.takeIf { it.isNotEmpty() } ?: return

            val fileType = inputDialog.getFileType()
            generate(
                className, inputString, packageDeclare, project, psiFileFactory, directory, fileType
            )
        } catch (e: UnSupportJsonException) {
            val advice = e.advice
            Messages.showInfoMessage(dealWithHtmlConvert(advice), "Tip")
        } catch (e: Throwable) {
//            dealWithException(jsonString, e)
            throw e
        }
    }

    private fun dealWithHtmlConvert(advice: String) = advice.replace("<", "&lt;").replace(">", "&gt;")

    private fun generate(
        className: String,
        json: String,
        packageDeclare: String,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory,
        fileType: GenFileType
    ) {
        if (fileType == GenFileType.Kotlin) {
            doGenerateKotlinDataClassFileAction(
                className, json, packageDeclare, project, psiFileFactory, directory
            )
        } else {
            doGenerateDartClass(
                className, json, packageDeclare, project, psiFileFactory, directory
            )
        }
    }

    private fun doGenerateDartClass(
        className: String,
        json: String,
        packageDeclare: String,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory,
    ) {
        val generatedClassesString = DartCodeMaker(className, json).makeDartClassData()

        val removeDuplicateClassCode = ClassCodeFilter.removeDuplicateClassCode(generatedClassesString)

        DartClassFileGenerator().generateMultipleDataClassFiles(
            removeDuplicateClassCode, packageDeclare, project, psiFileFactory, directory, json
        )
    }

    private fun doGenerateKotlinDataClassFileAction(
        className: String,
        json: String,
        packageDeclare: String,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory,
    ) {
        val kotlinClass = KotlinClassMaker(className, json).makeKotlinClass()
        val dataClassAfterApplyInterceptor =
            kotlinClass.applyInterceptors(InterceptorManager.getEnabledKotlinDataClassInterceptors())
        if (KotlinConfigManager.isInnerClassModel) {
            KotlinClassFileGenerator().generateSingleKotlinClassFile(
                packageDeclare, dataClassAfterApplyInterceptor, project, psiFileFactory, directory, json
            )
        } else {
            KotlinClassFileGenerator().generateMultipleKotlinClassFiles(
                dataClassAfterApplyInterceptor,
                packageDeclare,
                project,
                psiFileFactory,
                directory,
                json.isJSONSchema(),
                json
            )
        }
    }
}
