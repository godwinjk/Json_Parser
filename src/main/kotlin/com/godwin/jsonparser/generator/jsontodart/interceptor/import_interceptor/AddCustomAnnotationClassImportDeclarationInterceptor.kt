package com.godwin.jsonparser.generator.jsontodart.interceptor.import_interceptor

import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager

class AddCustomAnnotationClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String, fileName: String): String {

        val propertyAnnotationImportClassString = DartConfigManager.customAnnotationClassImportdeclarationString
        return if (originClassImportDeclaration.isBlank()) propertyAnnotationImportClassString
        else originClassImportDeclaration.append(propertyAnnotationImportClassString)
    }
}