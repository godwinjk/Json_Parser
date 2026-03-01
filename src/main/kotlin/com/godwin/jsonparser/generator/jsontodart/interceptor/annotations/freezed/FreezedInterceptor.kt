package com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.freezed

import com.godwin.jsonparser.generator.jsontodart.interceptor.clazz.IDartClassInterceptor
import com.godwin.jsonparser.generator.jsontodart.specs.annotations.FreezedClassAnnotationTemplate
import com.godwin.jsonparser.generator.jsontodart.specs.clazz.DartClass

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
