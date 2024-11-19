package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.KotlinDataClass


class ChangePropertyKeywordToFinalInterceptor : IKotlinDataClassInterceptor {

    override fun intercept(kotlinDataClass: KotlinDataClass): KotlinDataClass {

        val finalProperties = kotlinDataClass.properties.map {

            it.copy(keyword = "final")
        }

        return kotlinDataClass.copy(properties = finalProperties)
    }

}
