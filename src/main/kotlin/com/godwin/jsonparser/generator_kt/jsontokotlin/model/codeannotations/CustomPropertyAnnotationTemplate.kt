package com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeannotations

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.ConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Annotation

class CustomPropertyAnnotationTemplate(val rawName: String) : AnnotationTemplate {

    private val annotation = Annotation(ConfigManager.customPropertyAnnotationFormatString, rawName)

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }

}