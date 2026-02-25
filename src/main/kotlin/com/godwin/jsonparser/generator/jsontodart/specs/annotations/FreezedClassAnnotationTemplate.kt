package com.godwin.jsonparser.generator.jsontodart.specs.annotations

import com.godwin.jsonparser.generator.jsontodart.specs.clazz.Annotation

class FreezedClassAnnotationTemplate : AnnotationTemplate {

    private val annotation = Annotation("@freezed", "")

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }

}