package com.godwin.jsonparser.generator_kt.jsontokotlin.utils

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IImportClassDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.InterceptorManager


/**
 *  class import declaration
 * Created by Seal.Wu on 2018/4/18.
 */
object ClassImportDeclaration {

    /**
     * import class declaration getter
     */
    fun getImportClassDeclaration(): String {

        return applyImportClassDeclarationInterceptors(
                InterceptorManager.getEnabledImportClassDeclarationInterceptors())

    }


    fun applyImportClassDeclarationInterceptors(
            interceptors: List<IImportClassDeclarationInterceptor>): String {

        var classImportDeclaration = ""

        interceptors.forEach {
            classImportDeclaration = it.intercept(classImportDeclaration)
        }
        return classImportDeclaration
    }
}