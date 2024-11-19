package com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeannotations

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Annotation

class SerializablePropertyAnnotationTemplate(val rawName: String) : AnnotationTemplate {

    companion object{

        const val annotationFormat = "@SerialName(\"%s\")"
    }

    private val annotation = Annotation(annotationFormat, rawName)

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }
}