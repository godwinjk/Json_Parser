package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass

class ChangePropertyKeywordToVarInterceptor : IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {

            val varProperties = kotlinClass.properties.map {

                it.copy(keyword = "var")
            }

            return kotlinClass.copy(properties = varProperties)
        } else {
            return  kotlinClass
        }
    }

}
