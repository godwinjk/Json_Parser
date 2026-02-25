package com.godwin.jsonparser.generator.jsontodart.specs.annotations

import com.godwin.jsonparser.generator.jsontodart.specs.clazz.Annotation
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager

class CustomPropertyAnnotationTemplate(val rawName: String) : AnnotationTemplate {

    private val annotation = Annotation(DartConfigManager.customPropertyAnnotationFormatString, rawName)

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }

}