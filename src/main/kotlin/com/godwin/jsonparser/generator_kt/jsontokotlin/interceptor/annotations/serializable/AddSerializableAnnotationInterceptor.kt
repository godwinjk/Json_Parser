package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.serializable

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IKotlinClassInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeannotations.SerializablePropertyAnnotationTemplate
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeelements.KPropertyName
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Annotation

class AddSerializableAnnotationInterceptor: IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {
            val addCustomAnnotationProperties = kotlinClass.properties.map {

                val camelCaseName = KPropertyName.makeLowerCamelCaseLegalName(it.originName)

                val annotations = SerializablePropertyAnnotationTemplate(it.originName).getAnnotations()

                it.copy(annotations = annotations,name = camelCaseName)
            }

            val classAnnotationString = "@Serializable"

            val classAnnotation = Annotation.fromAnnotationString(classAnnotationString)

            return kotlinClass.copy(properties = addCustomAnnotationProperties,annotations = listOf(classAnnotation))
        } else {
            return kotlinClass
        }
    }

}