package com.godwin.jsonparser.generator.jsontodart.interceptor.annotations

import com.godwin.jsonparser.generator.jsontodart.interceptor.IImportClassDeclarationInterceptor

class FreezedAndJsonSerializableImportInterceptor(
    private val isJsonSerializableSupport: Boolean,
    private val isFreezedSupport: Boolean
) : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String, fileName: String): String {
        val string = buildString {
            if (isJsonSerializableSupport) {
                append("import 'package:json_annotation/json_annotation.dart';")
                append("\n")
            }
            if (isFreezedSupport) {
                append("import 'package:freezed_annotation/freezed_annotation.dart';")
                append("\n")
            }
            append("import 'dart:convert';").append("\npart '$fileName.g.dart';").append("\n")
            if (isFreezedSupport) {
                append("part '$fileName.freezed.dart';")
            }
        }


        return originClassImportDeclaration.append(string)
    }
}