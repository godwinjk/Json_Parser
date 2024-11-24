package com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.jsonserializable

import com.godwin.jsonparser.generator.jsontodart.interceptor.IImportClassDeclarationInterceptor

class JsonSerializableAnnotationClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String, fileName: String): String {

        val propertyAnnotationImportClassString = """
            import 'package:json_annotation/json_annotation.dart';
            import 'dart:convert';
            
            part '$fileName.g.dart';
        """.trimIndent()
        return originClassImportDeclaration.append(propertyAnnotationImportClassString)
    }
}
