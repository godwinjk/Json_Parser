package com.godwin.jsonparser.generator.jsontokotlin.interceptor.annotations.custom

import com.godwin.jsonparser.generator.jsontokotlin.interceptor.IImportClassDeclarationInterceptor
import com.godwin.jsonparser.generator.jsontokotlin.model.KotlinConfigManager

class AddCustomAnnotationClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String): String {


        val propertyAnnotationImportClassString = KotlinConfigManager.customAnnotationClassImportdeclarationString


        return originClassImportDeclaration.append(propertyAnnotationImportClassString)
    }
}
