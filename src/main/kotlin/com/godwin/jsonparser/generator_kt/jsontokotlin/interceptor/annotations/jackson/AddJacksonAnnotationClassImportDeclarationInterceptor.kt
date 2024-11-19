package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.jackson

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IImportClassDeclarationInterceptor
class AddJacksonAnnotationClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {


    override fun intercept(originClassImportDeclaration: String): String {


        val propertyAnnotationImportClassString = "import com.fasterxml.jackson.annotation.JsonProperty"


        return originClassImportDeclaration.append(propertyAnnotationImportClassString)
    }

}
