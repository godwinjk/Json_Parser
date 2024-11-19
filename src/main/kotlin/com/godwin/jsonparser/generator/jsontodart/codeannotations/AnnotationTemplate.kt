package com.godwin.jsonparser.generator.jsontodart.codeannotations

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.Annotation

interface AnnotationTemplate {
    fun getCode():String
    fun getAnnotations():List<Annotation>
}