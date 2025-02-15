package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor

import com.godwin.jsonparser.generator_kt.extensions.ExtensionsCollector
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.custom.AddCustomAnnotationClassImportDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.custom.AddCustomAnnotationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.fastjson.AddFastJsonAnnotationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.fastjson.AddFastjsonAnnotationClassImportDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.gson.AddGsonAnnotationClassImportDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.gson.AddGsonAnnotationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.jackson.AddJacksonAnnotationClassImportDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.jackson.AddJacksonAnnotationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.logansquare.AddLoganSquareAnnotationClassImportDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.logansquare.AddLoganSquareAnnotationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.moshi.AddMoshiAnnotationClassImportDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.moshi.AddMoshiAnnotationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.moshi.AddMoshiCodeGenAnnotationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.moshi.AddMoshiCodeGenClassImportDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.serializable.AddSerializableAnnotationClassImportDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.serializable.AddSerializableAnnotationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DefaultValueStrategy
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.TargetJsonConverter
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass

object InterceptorManager {

    fun getEnabledKotlinDataClassInterceptors(): List<IKotlinClassInterceptor<KotlinClass>> {

        return mutableListOf<IKotlinClassInterceptor<KotlinClass>>().apply {

            if (KotlinConfigManager.isPropertiesVar) {
                add(ChangePropertyKeywordToVarInterceptor())
            }

            add(PropertyTypeNullableStrategyInterceptor())

            if (KotlinConfigManager.defaultValueStrategy != DefaultValueStrategy.None) {
                add(InitWithDefaultValueInterceptor())
            }

            when (KotlinConfigManager.targetJsonConverterLib) {
                TargetJsonConverter.None -> {
                }

                TargetJsonConverter.NoneWithCamelCase -> add(MakePropertiesNameToBeCamelCaseInterceptor())
                TargetJsonConverter.Gson -> add(AddGsonAnnotationInterceptor())
                TargetJsonConverter.FastJson -> add(AddFastJsonAnnotationInterceptor())
                TargetJsonConverter.Jackson -> add(AddJacksonAnnotationInterceptor())
                TargetJsonConverter.MoShi -> add(AddMoshiAnnotationInterceptor())
                TargetJsonConverter.MoshiCodeGen -> add(AddMoshiCodeGenAnnotationInterceptor())
                TargetJsonConverter.LoganSquare -> add(AddLoganSquareAnnotationInterceptor())
                TargetJsonConverter.Custom -> add(AddCustomAnnotationInterceptor())
                TargetJsonConverter.Serializable -> add(AddSerializableAnnotationInterceptor())
                else -> {}
            }

            if (KotlinConfigManager.parenClassTemplate.isNotBlank()) {
                add(ParentClassTemplateKotlinClassInterceptor())
            }

            if (KotlinConfigManager.isCommentOff) {
                add(CommentOffInterceptor)
            }

            if (KotlinConfigManager.isOrderByAlphabetical) {
                add(OrderPropertyByAlphabeticalInterceptor())
            }

        }.apply {
            //add extensions's interceptor
            addAll(ExtensionsCollector.extensions)
        }.apply {
            if (KotlinConfigManager.enableMinimalAnnotation) {
                add(MinimalAnnotationKotlinClassInterceptor())
            }
            add(FinalKotlinClassWrapperInterceptor())
        }
    }


    fun getEnabledImportClassDeclarationInterceptors(): List<IImportClassDeclarationInterceptor> {

        return mutableListOf<IImportClassDeclarationInterceptor>().apply {
            when (KotlinConfigManager.targetJsonConverterLib) {
                TargetJsonConverter.Gson -> add(AddGsonAnnotationClassImportDeclarationInterceptor())
                TargetJsonConverter.FastJson -> add(AddFastjsonAnnotationClassImportDeclarationInterceptor())
                TargetJsonConverter.Jackson -> add(AddJacksonAnnotationClassImportDeclarationInterceptor())
                TargetJsonConverter.MoShi -> add(AddMoshiAnnotationClassImportDeclarationInterceptor())
                TargetJsonConverter.MoshiCodeGen -> add(AddMoshiCodeGenClassImportDeclarationInterceptor())
                TargetJsonConverter.LoganSquare -> add(AddLoganSquareAnnotationClassImportDeclarationInterceptor())
                TargetJsonConverter.Custom -> add(AddCustomAnnotationClassImportDeclarationInterceptor())
                TargetJsonConverter.Serializable -> add(AddSerializableAnnotationClassImportDeclarationInterceptor())
                else -> {}
            }

            if (KotlinConfigManager.parenClassTemplate.isNotBlank()) {

                add(ParentClassClassImportDeclarationInterceptor())
            }
        }.apply {
            //add extensions's interceptor
            addAll(ExtensionsCollector.extensions)
        }
    }

}
