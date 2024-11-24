package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager

class ParentClassTemplateDartClassInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {

        val parentClassTemplateSimple = DartConfigManager.parenClassTemplate.substringAfterLast(".")
        return dartClass.copy(parentClassTemplate = parentClassTemplateSimple)
    }
}