package com.godwin.jsonparser.generator.jsontodart.codeannotations

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.Annotation

class JsonSerializablePropertyAnnotationTemplate(val rawName: String) : AnnotationTemplate {

    private val annotation = Annotation("@JsonKey(name:\"%s\")", rawName)

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }

}