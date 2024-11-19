package com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.custom

import com.godwin.jsonparser.generator.jsontodart.ConfigManager
import com.godwin.jsonparser.generator.jsontodart.classscodestruct.Annotation
import com.godwin.jsonparser.generator.jsontodart.classscodestruct.KotlinDataClass
import com.godwin.jsonparser.generator.jsontodart.codeannotations.CustomPropertyAnnotationTemplate
import com.godwin.jsonparser.generator.jsontodart.codeelements.KPropertyName
import com.godwin.jsonparser.generator.jsontodart.interceptor.IKotlinDataClassInterceptor

class AddCustomAnnotationInterceptor : IKotlinDataClassInterceptor {

    override fun intercept(kotlinDataClass: KotlinDataClass): KotlinDataClass {

        val addCustomAnnotationProperties = kotlinDataClass.properties.map {

            val camelCaseName = KPropertyName.makeLowerCamelCaseLegalName(it.originName)

            val annotations = CustomPropertyAnnotationTemplate(it.originName).getAnnotations()

            it.copy(annotations = annotations,name = camelCaseName)
        }

        val classAnnotationString = ConfigManager.customClassAnnotationFormatString

        val classAnnotation = Annotation.fromAnnotationString(classAnnotationString)

        return kotlinDataClass.copy(properties = addCustomAnnotationProperties,annotations = listOf(classAnnotation))
    }
}
