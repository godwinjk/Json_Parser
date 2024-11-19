package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.moshi

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IImportClassDeclarationInterceptor
class AddMoshiAnnotationClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {


    override fun intercept(originClassImportDeclaration: String): String {


        val propertyAnnotationImportClassString =  "import com.squareup.moshi.Json"


        return originClassImportDeclaration.append(propertyAnnotationImportClassString)
    }
}
