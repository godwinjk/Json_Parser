package com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.custom

import com.godwin.jsonparser.generator.jsontodart.interceptor.IImportClassDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager

class AddCustomAnnotationClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String, fileName: String): String {

        val propertyAnnotationImportClassString = DartConfigManager.customAnnotationClassImportdeclarationString
        return if (originClassImportDeclaration.isBlank()) propertyAnnotationImportClassString
        else originClassImportDeclaration.append(propertyAnnotationImportClassString)
    }
}
