package com.godwin.jsonparser.generator.jsontodart.codeannotations

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.Annotation

class JsonSerializableClassAnnotationTemplate : AnnotationTemplate {

    private val annotation = Annotation("@JsonSerializable()", "")

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }

}