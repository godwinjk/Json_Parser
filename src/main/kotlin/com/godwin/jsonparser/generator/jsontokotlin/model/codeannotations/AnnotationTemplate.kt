package com.godwin.jsonparser.generator.jsontokotlin.model.codeannotations

import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.Annotation

interface AnnotationTemplate {
    fun getCode(): String
    fun getAnnotations(): List<Annotation>
}