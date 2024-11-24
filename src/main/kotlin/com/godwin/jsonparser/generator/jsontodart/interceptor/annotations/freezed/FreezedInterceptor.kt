package com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.freezed

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass
import com.godwin.jsonparser.generator.jsontodart.codeannotations.FreezedClassAnnotationTemplate
import com.godwin.jsonparser.generator.jsontodart.interceptor.IDartClassInterceptor

class FreezedInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {

        val classAnnotation = FreezedClassAnnotationTemplate()
        val mixinClassTemplate = "_$${dartClass.name}"
        return dartClass.copy(
            annotations = classAnnotation.getAnnotations(),
            mixinClass = mixinClassTemplate
        )
    }
}
