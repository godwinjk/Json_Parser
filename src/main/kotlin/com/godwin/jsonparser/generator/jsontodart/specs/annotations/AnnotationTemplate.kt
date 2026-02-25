package com.godwin.jsonparser.generator.jsontodart.specs.annotations

import com.godwin.jsonparser.generator.jsontodart.specs.clazz.Annotation

interface AnnotationTemplate {
    fun getCode(): String
    fun getAnnotations(): List<Annotation>
}