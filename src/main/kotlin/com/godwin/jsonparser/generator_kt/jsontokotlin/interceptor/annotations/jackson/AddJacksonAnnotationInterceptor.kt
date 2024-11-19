package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.jackson

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IKotlinClassInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeannotations.JacksonPropertyAnnotationTemplate
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeelements.KPropertyName


class AddJacksonAnnotationInterceptor : IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {
            val addMoshiCodeGenAnnotationProperties = kotlinClass.properties.map {

                val camelCaseName = KPropertyName.makeLowerCamelCaseLegalName(it.originName)

                it.copy(annotations =  JacksonPropertyAnnotationTemplate(it.originName).getAnnotations(),name = camelCaseName)
            }

            return kotlinClass.copy(properties = addMoshiCodeGenAnnotationProperties)
        } else {
            return kotlinClass
        }
    }
}
