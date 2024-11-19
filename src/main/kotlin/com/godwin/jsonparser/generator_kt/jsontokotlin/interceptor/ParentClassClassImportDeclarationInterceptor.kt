package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.ConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.KOTLIN_KEYWORD_LIST


/**
 * insert parent class declaration code
 */
class ParentClassClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String): String {

        val importClass = ConfigManager.parenClassTemplate.substringBeforeLast("(").trim()

        val legalImportClass = importClass.split(".").map {
            if ((KOTLIN_KEYWORD_LIST.contains(it) || it.first().isDigit())) "`$it`" else it
        }.joinToString(".")

        val parentClassImportDeclaration = "import $legalImportClass"

        return "$originClassImportDeclaration\n$parentClassImportDeclaration".trim()
    }
}