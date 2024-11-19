package com.godwin.jsonparser.generator.jsontodart.codeannotations

import com.godwin.jsonparser.generator.jsontodart.ConfigManager
import com.godwin.jsonparser.generator.jsontodart.classscodestruct.Annotation

class CustomPropertyAnnotationTemplate(val rawName: String) : AnnotationTemplate {

    private val annotation = Annotation(ConfigManager.customPropertyAnnotationFormatString, rawName)

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }

}