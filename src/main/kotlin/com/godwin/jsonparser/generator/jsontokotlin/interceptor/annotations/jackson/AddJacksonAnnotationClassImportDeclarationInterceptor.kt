package com.godwin.jsonparser.generator.jsontokotlin.interceptor.annotations.jackson

import com.godwin.jsonparser.generator.jsontokotlin.interceptor.IImportClassDeclarationInterceptor

class AddJacksonAnnotationClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {


    override fun intercept(originClassImportDeclaration: String): String {


        val propertyAnnotationImportClassString = "import com.fasterxml.jackson.annotation.JsonProperty"


        return originClassImportDeclaration.append(propertyAnnotationImportClassString)
    }

}
