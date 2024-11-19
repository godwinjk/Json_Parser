package com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeannotations

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Annotation

interface AnnotationTemplate {
    fun getCode():String
    fun getAnnotations():List<Annotation>
}