package com.godwin.jsonparser.generator.jsontodart.interceptor.import_interceptor

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
            append("\n")
            if (isJsonSerializableSupport) {
                append("\npart '$fileName.g.dart';")
                append("\n")
            }
            if (isFreezedSupport) {
                append("part '$fileName.freezed.dart';")
            }
        }

        return if (originClassImportDeclaration.isBlank()) string
        else originClassImportDeclaration.append(string)
    }
}