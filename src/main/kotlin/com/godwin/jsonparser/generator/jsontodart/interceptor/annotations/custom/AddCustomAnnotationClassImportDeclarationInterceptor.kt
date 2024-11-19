package com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.custom

import com.godwin.jsonparser.generator.jsontodart.ConfigManager
import com.godwin.jsonparser.generator.jsontodart.interceptor.IImportClassDeclarationInterceptor

class AddCustomAnnotationClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String): String {


        val propertyAnnotationImportClassString = ConfigManager.customAnnotationClassImportdeclarationString


        return originClassImportDeclaration.append(propertyAnnotationImportClassString)
    }
}
