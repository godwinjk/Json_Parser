package com.godwin.jsonparser.generator.jsontodart.interceptor.clazz

import com.godwin.jsonparser.generator.jsontodart.specs.clazz.DartClass
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager

class ParentClassTemplateDartClassInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {

        val parentClassTemplateSimple = DartConfigManager.parenClassTemplate.substringAfterLast(".")
        return dartClass.copy(parentClassTemplate = parentClassTemplateSimple)
    }
}