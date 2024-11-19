package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.extensions.ExtensionsCollector
import com.godwin.jsonparser.generator.jsontodart.ConfigManager
import com.godwin.jsonparser.generator.jsontodart.DefaultValueStrategy
import com.godwin.jsonparser.generator.jsontodart.TargetJsonConverter
import com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.custom.AddCustomAnnotationClassImportDeclarationInterceptor
import com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.custom.AddCustomAnnotationInterceptor

object InterceptorManager {

    fun getEnabledKotlinDataClassInterceptors(): List<IKotlinDataClassInterceptor> {

        return mutableListOf<IKotlinDataClassInterceptor>().apply {

            if (ConfigManager.isPropertiesFinal) {
                add(ChangePropertyKeywordToFinalInterceptor())
            }

            if (ConfigManager.defaultValueStrategy != DefaultValueStrategy.None) {
                add(InitWithDefaultValueInterceptor())
            }

            when (ConfigManager.targetJsonConverterLib) {
                TargetJsonConverter.None -> {
                }
                TargetJsonConverter.NoneWithCamelCase -> add(MakePropertiesNameToBeCamelCaseInterceptor())
                TargetJsonConverter.Custom -> add(AddCustomAnnotationInterceptor())
                else -> {}
            }
            if (ConfigManager.enableMinimalAnnotation) {
                add(MinimalAnnotationKotlinDataClassInterceptor())
            }

            if (ConfigManager.parenClassTemplate.isNotBlank()) {
                add(ParentClassTemplateKotlinDataClassInterceptor())
            }

            if (ConfigManager.keywordPropertyValid) {
                add(MakeKeywordNamedPropertyValidInterceptor())
            }

            if (ConfigManager.isCommentOff) {
                add(CommentOffInterceptor)
            }

            if (ConfigManager.isOrderByAlphabetical) {
                add(OrderPropertyByAlphabeticalInterceptor())
            }

        }.apply {
            //add extensions's interceptor
            addAll(ExtensionsCollector.extensions)
        }
    }


    fun getEnabledImportClassDeclarationInterceptors(): List<IImportClassDeclarationInterceptor> {

        return mutableListOf<IImportClassDeclarationInterceptor>().apply {


            when (ConfigManager.targetJsonConverterLib) {
                TargetJsonConverter.Custom->add(AddCustomAnnotationClassImportDeclarationInterceptor())
                else->{}
            }

            if (ConfigManager.parenClassTemplate.isNotBlank()) {

                add(ParentClassClassImportDeclarationInterceptor())
            }
        }.apply {
            //add extensions's interceptor
            addAll(ExtensionsCollector.extensions)
        }
    }

}
