package com.godwin.jsonparser.generator.jsontokotlin.interceptor.annotations.gson

import com.godwin.jsonparser.generator.jsontokotlin.interceptor.IKotlinClassInterceptor
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator.jsontokotlin.model.codeannotations.GsonPropertyAnnotationTemplate
import com.godwin.jsonparser.generator.jsontokotlin.model.codeelements.KPropertyName


class AddGsonAnnotationInterceptor : IKotlinClassInterceptor<KotlinClass> {


    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        return if (kotlinClass is DataClass) {
            val addGsonAnnotationProperties = kotlinClass.properties.map {

                val camelCaseName = KPropertyName.makeLowerCamelCaseLegalName(it.originName)

                it.copy(
                    annotations = GsonPropertyAnnotationTemplate(it.originName).getAnnotations(),
                    name = camelCaseName
                )
            }
            kotlinClass.copy(properties = addGsonAnnotationProperties)
        } else {
            kotlinClass
        }

    }

}
