package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.custom

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IImportClassDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.ConfigManager

class AddCustomAnnotationClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String): String {


        val propertyAnnotationImportClassString = ConfigManager.customAnnotationClassImportdeclarationString


        return originClassImportDeclaration.append(propertyAnnotationImportClassString)
    }
}
