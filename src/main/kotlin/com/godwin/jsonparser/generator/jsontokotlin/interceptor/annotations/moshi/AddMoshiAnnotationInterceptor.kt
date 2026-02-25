package com.godwin.jsonparser.generator.jsontokotlin.interceptor.annotations.moshi

import com.godwin.jsonparser.generator.jsontokotlin.interceptor.IKotlinClassInterceptor
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator.jsontokotlin.model.codeannotations.MoshiPropertyAnnotationTemplate
import com.godwin.jsonparser.generator.jsontokotlin.model.codeelements.KPropertyName


class AddMoshiAnnotationInterceptor : IKotlinClassInterceptor<KotlinClass> {


    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {
            val addMoshiCodeGenAnnotationProperties = kotlinClass.properties.map {

                val camelCaseName = KPropertyName.makeLowerCamelCaseLegalName(it.originName)

                it.copy(
                    annotations = MoshiPropertyAnnotationTemplate(it.originName).getAnnotations(),
                    name = camelCaseName
                )
            }

            return kotlinClass.copy(properties = addMoshiCodeGenAnnotationProperties)
        } else {
            return kotlinClass
        }

    }
}
