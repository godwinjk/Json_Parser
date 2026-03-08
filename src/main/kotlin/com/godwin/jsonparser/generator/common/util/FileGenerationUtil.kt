package com.godwin.jsonparser.generator.common.util

import com.godwin.jsonparser.generator.common.filetype.GenFileType
import com.godwin.jsonparser.generator.jsontodart.code_gen.DartClassFileGenerator
import com.godwin.jsonparser.generator.jsontodart.code_gen.DartCodeMaker
import com.godwin.jsonparser.generator.jsontodart.utils.ClassCodeFilter
import com.godwin.jsonparser.generator.jsontokotlin.interceptor.InterceptorManager
import com.godwin.jsonparser.generator.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.utils.KotlinClassFileGenerator
import com.godwin.jsonparser.generator.jsontokotlin.utils.KotlinClassMaker
import com.godwin.jsonparser.generator.jsontokotlin.utils.isJSONSchema
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory

object FileGenerationUtil {

    fun generate(
        className: String,
        json: String,
        packageDeclare: String,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory,
        fileType: GenFileType
    ) {
        if (fileType == GenFileType.Kotlin) {
            generateKotlinFiles(
                className, json, packageDeclare, project, psiFileFactory, directory
            )
        } else {
            generateDartFiles(
                className, json, packageDeclare, project, psiFileFactory, directory
            )
        }
    }

    private fun generateDartFiles(
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

    private fun generateKotlinFiles(
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
