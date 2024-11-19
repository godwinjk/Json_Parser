package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.annotations.fastjson

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IKotlinClassInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeannotations.FastjsonPropertyAnnotationTemplate
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeelements.KPropertyName


class AddFastJsonAnnotationInterceptor : IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        return if (kotlinClass is DataClass) {

            val addFastJsonAnnotationProperties = kotlinClass.properties.map {

                val camelCaseName = KPropertyName.makeLowerCamelCaseLegalName(it.originName)

                val annotations = FastjsonPropertyAnnotationTemplate(it.originName).getAnnotations()

                it.copy(annotations = annotations, name = camelCaseName)
            }

            kotlinClass.copy(properties = addFastJsonAnnotationProperties)
        } else {
            kotlinClass
        }

    }

}
