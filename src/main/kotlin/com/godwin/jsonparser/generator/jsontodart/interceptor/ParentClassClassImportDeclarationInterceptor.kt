package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager


/**
 * insert parent class declaration code
 */
class ParentClassClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String, fileName: String): String {
        val parentClassImportDeclaration =
            "import ${DartConfigManager.parenClassTemplate.substringBeforeLast("(").trim()}"
        return "$originClassImportDeclaration\n$parentClassImportDeclaration".trim()
    }
}