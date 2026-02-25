package com.godwin.jsonparser.generator.jsontodart.specs.annotations

import com.godwin.jsonparser.generator.jsontodart.specs.clazz.Annotation

class JsonSerializablePropertyAnnotationTemplate(val rawName: String) : AnnotationTemplate {

    private val annotation = Annotation("@JsonKey(name:\"%s\")", rawName)

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }

}