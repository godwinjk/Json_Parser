package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass


class ParentClassTemplateKotlinClassInterceptor : IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {

            val parentClassTemplateSimple = KotlinConfigManager.parenClassTemplate.substringAfterLast(".")
            return kotlinClass.copy(parentClassTemplate = parentClassTemplateSimple)
        } else {
            return kotlinClass
        }
    }


}