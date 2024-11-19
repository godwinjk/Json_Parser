package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.ConfigManager


/**
 * insert parent class declaration code
 */
class ParentClassClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String): String {

        val parentClassImportDeclaration = "import ${ConfigManager.parenClassTemplate.substringBeforeLast("(").trim()}"

        return "$originClassImportDeclaration\n$parentClassImportDeclaration".trim()
    }
}