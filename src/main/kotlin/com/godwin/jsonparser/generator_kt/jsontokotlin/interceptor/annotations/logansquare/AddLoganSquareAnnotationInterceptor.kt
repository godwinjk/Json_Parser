package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.logansquare

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IKotlinClassInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeannotations.LoganSquarePropertyAnnotationTemplate
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeelements.KPropertyName
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Annotation

class AddLoganSquareAnnotationInterceptor : IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {
            val addLoganSquareAnnotationProperties = kotlinClass.properties.map {

                val camelCaseName = KPropertyName.makeLowerCamelCaseLegalName(it.originName)

                it.copy(annotations =  LoganSquarePropertyAnnotationTemplate(it.originName).getAnnotations(),name = camelCaseName)
            }

            val classAnnotationString = "@JsonObject"

            val classAnnotation = Annotation.fromAnnotationString(classAnnotationString)


            return kotlinClass.copy(properties = addLoganSquareAnnotationProperties,annotations = listOf(classAnnotation))
        } else {
            return kotlinClass
        }
    }
}
