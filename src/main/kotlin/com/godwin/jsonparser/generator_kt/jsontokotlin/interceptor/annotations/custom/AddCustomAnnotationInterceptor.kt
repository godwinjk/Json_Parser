package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.custom

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IKotlinClassInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.ConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeannotations.CustomPropertyAnnotationTemplate
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeelements.KPropertyName
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Annotation

class AddCustomAnnotationInterceptor : IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {
            val addCustomAnnotationProperties = kotlinClass.properties.map {

                val camelCaseName = KPropertyName.makeLowerCamelCaseLegalName(it.originName)

                val annotations = CustomPropertyAnnotationTemplate(it.originName).getAnnotations()

                it.copy(annotations = annotations,name = camelCaseName)
            }

            val classAnnotationString = ConfigManager.customClassAnnotationFormatString

            val classAnnotation = Annotation.fromAnnotationString(classAnnotationString)

            return kotlinClass.copy(properties = addCustomAnnotationProperties,annotations = listOf(classAnnotation))
        } else {
            return kotlinClass
        }
    }
}
