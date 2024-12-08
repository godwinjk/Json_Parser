package com.godwin.jsonparser.generator.jsontodart.utils

import com.godwin.jsonparser.generator.jsontodart.interceptor.IImportClassDeclarationInterceptor
import com.godwin.jsonparser.generator.jsontodart.interceptor.InterceptorManager

/**
 *  class import declaration
 * Created by Godwin on 2024/12/20.
 */
object ClassImportDeclaration {

    /**
     * import class declaration getter
     */
    fun getImportClassDeclaration(fileName: String): String {

        return applyImportClassDeclarationInterceptors(
            InterceptorManager.getEnabledImportClassDeclarationInterceptors(), fileName
        )
    }

    fun applyImportClassDeclarationInterceptors(
        interceptors: List<IImportClassDeclarationInterceptor>, fileName: String
    ): String {

        var classImportDeclaration = ""

        interceptors.forEach {
            classImportDeclaration = it.intercept(classImportDeclaration, fileName)
        }
        return classImportDeclaration
    }
}