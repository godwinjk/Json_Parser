package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.ConfigManager
import com.godwin.jsonparser.generator.jsontodart.classscodestruct.KotlinDataClass

class ParentClassTemplateKotlinDataClassInterceptor : IKotlinDataClassInterceptor {

    override fun intercept(kotlinDataClass: KotlinDataClass): KotlinDataClass {

        val parentClassTemplateSimple = ConfigManager.parenClassTemplate.substringAfterLast(".")
        return kotlinDataClass.copy(parentClassTemplate = parentClassTemplateSimple)
    }


}