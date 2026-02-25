package com.godwin.jsonparser.generator.jsontodart.specs.annotations

import com.godwin.jsonparser.generator.jsontodart.specs.clazz.Annotation

class JsonSerializableClassAnnotationTemplate : AnnotationTemplate {

    private val annotation = Annotation("@JsonSerializable()", "")

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }

}