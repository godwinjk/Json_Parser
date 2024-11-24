package com.godwin.jsonparser.generator.jsontodart.codeannotations

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.Annotation
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager

class CustomPropertyAnnotationTemplate(val rawName: String) : AnnotationTemplate {

    private val annotation = Annotation(DartConfigManager.customPropertyAnnotationFormatString, rawName)

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }

}