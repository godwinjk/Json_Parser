package com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeannotations

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Annotation
class MoshiPropertyAnnotationTemplate(val rawName: String) : AnnotationTemplate {

    companion object {

        const val propertyAnnotationFormat = "@Json(name = \"%s\")"

    }

    private val annotation = Annotation(propertyAnnotationFormat, rawName)

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }
}