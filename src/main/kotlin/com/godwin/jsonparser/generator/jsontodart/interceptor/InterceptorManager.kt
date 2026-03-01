package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.custom.AddCustomAnnotationInterceptor
import com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.freezed.FreezedInterceptor
import com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.jsonserializable.JsonSerializableInterceptor
import com.godwin.jsonparser.generator.jsontodart.interceptor.clazz.*
import com.godwin.jsonparser.generator.jsontodart.interceptor.import_interceptor.AddCustomAnnotationClassImportDeclarationInterceptor
import com.godwin.jsonparser.generator.jsontodart.interceptor.import_interceptor.FreezedAndJsonSerializableImportInterceptor
import com.godwin.jsonparser.generator.jsontodart.interceptor.import_interceptor.IImportClassDeclarationInterceptor
import com.godwin.jsonparser.generator.jsontodart.interceptor.import_interceptor.ParentClassClassImportDeclarationInterceptor
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.model.DefaultValueStrategy
import com.godwin.jsonparser.generator.jsontokotlin.model.TargetJsonConverter

object InterceptorManager {
    fun getEnabledDartDataClassInterceptors(): List<IDartClassInterceptor> {
        return mutableListOf<IDartClassInterceptor>().apply {
            //default enabled to remove the usage of dart keywords
            add(MakeKeywordNamedPropertyValidInterceptor())
            add(MakePropertiesNameToBeCamelCaseInterceptor())
            if (DartConfigManager.isPropertyFinal) {
                add(ChangePropertyKeywordToFinalInterceptor())
            }

            if (DartConfigManager.defaultValueStrategy != DefaultValueStrategy.None) {
                add(InitWithDefaultValueInterceptor())
            }

            when (DartConfigManager.targetJsonConverterLib) {
                TargetJsonConverter.None -> {
                }

                TargetJsonConverter.DartPackage -> {
                    if (DartConfigManager.isJsonSerializationAnnotation) {
                        add(JsonSerializableInterceptor())
                    }
                    if (DartConfigManager.isFreezedAnnotation) {
                        add(FreezedInterceptor())
                    }
                }

                TargetJsonConverter.Custom -> add(AddCustomAnnotationInterceptor())
                else -> {}
            }
            if (DartConfigManager.enableMinimalAnnotation) {
                add(MinimalAnnotationDartClassInterceptor())
            }

            if (DartConfigManager.parenClassTemplate.isNotBlank()) {
                add(ParentClassTemplateDartClassInterceptor())
            }

            if (DartConfigManager.isCommentOff) {
                add(CommentOffInterceptor)
            }

            if (DartConfigManager.isOrderByAlphabetical) {
                add(OrderPropertyByAlphabeticalInterceptor())
            }
        }
    }

    fun getEnabledImportClassDeclarationInterceptors(): List<IImportClassDeclarationInterceptor> {

        return mutableListOf<IImportClassDeclarationInterceptor>().apply {
            when (DartConfigManager.targetJsonConverterLib) {
                TargetJsonConverter.Custom -> add(AddCustomAnnotationClassImportDeclarationInterceptor())
                TargetJsonConverter.DartPackage -> {
                    if (DartConfigManager.isJsonSerializationAnnotation || DartConfigManager.isFreezedAnnotation) {
                        add(
                            FreezedAndJsonSerializableImportInterceptor(
                                DartConfigManager.isJsonSerializationAnnotation,
                                DartConfigManager.isFreezedAnnotation,
                            )
                        )
                    }
                }

                else -> {}
            }

            if (DartConfigManager.parenClassTemplate.isNotBlank()) {
                add(ParentClassClassImportDeclarationInterceptor())
            }
        }
    }
}
