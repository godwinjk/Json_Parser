package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.gson

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IImportClassDeclarationInterceptor


class AddGsonAnnotationClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    companion object{

        const val propertyAnnotationImportClassString ="import com.google.gson.annotations.SerializedName"
    }

    override fun intercept(originClassImportDeclaration: String): String {

        return originClassImportDeclaration.append(propertyAnnotationImportClassString)
    }
}
