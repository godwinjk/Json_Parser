package com.godwin.jsonparser.generator.jsontokotlin.interceptor

import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass


class OrderPropertyByAlphabeticalInterceptor : IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        if (kotlinClass is DataClass) {

            val orderByAlphabeticalProperties = kotlinClass.properties.sortedBy { it.name }

            return kotlinClass.copy(properties = orderByAlphabeticalProperties)
        } else {
            return kotlinClass
        }

    }
}

