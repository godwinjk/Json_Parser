package com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.freezed

import com.godwin.jsonparser.generator.jsontodart.interceptor.IImportClassDeclarationInterceptor

class FreezedAnnotationClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String, fileName: String): String {
        val propertyAnnotationImportClassString = """
            import 'package:freezed_annotation/freezed_annotation.dart';
            import 'dart:convert';
            
            part '$fileName.freezed.dart';
            part '$fileName.g.dart';
        """.trimIndent()
        return originClassImportDeclaration.append(propertyAnnotationImportClassString)
    }
}
