package com.godwin.jsonparser.generator.jsontokotlin.interceptor.annotations.custom

import com.godwin.jsonparser.generator.jsontokotlin.interceptor.IKotlinClassInterceptor
import com.godwin.jsonparser.generator.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator.jsontokotlin.model.codeannotations.CustomPropertyAnnotationTemplate
import com.godwin.jsonparser.generator.jsontokotlin.model.codeelements.KPropertyName
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.Annotation

class AddCustomAnnotationInterceptor : IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {
            val addCustomAnnotationProperties = kotlinClass.properties.map {

                val camelCaseName = KPropertyName.makeLowerCamelCaseLegalName(it.originName)

                val annotations = CustomPropertyAnnotationTemplate(it.originName).getAnnotations()

                it.copy(annotations = annotations, name = camelCaseName)
            }

            val classAnnotationString = KotlinConfigManager.customClassAnnotationFormatString

            val classAnnotation = Annotation.fromAnnotationString(classAnnotationString)

            return kotlinClass.copy(properties = addCustomAnnotationProperties, annotations = listOf(classAnnotation))
        } else {
            return kotlinClass
        }
    }
}
