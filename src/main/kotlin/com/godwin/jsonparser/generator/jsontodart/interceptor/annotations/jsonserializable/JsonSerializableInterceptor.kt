package com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.jsonserializable

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass
import com.godwin.jsonparser.generator.jsontodart.codeannotations.JsonSerializableClassAnnotationTemplate
import com.godwin.jsonparser.generator.jsontodart.codeannotations.JsonSerializablePropertyAnnotationTemplate
import com.godwin.jsonparser.generator.jsontodart.interceptor.IDartClassInterceptor

class JsonSerializableInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {

        val addCustomAnnotationProperties = dartClass.properties.map {
            val annotations = JsonSerializablePropertyAnnotationTemplate(it.originName).getAnnotations()

            it.copy(annotations = annotations)
        }
        val classAnnotation = JsonSerializableClassAnnotationTemplate()
        return dartClass.copy(
            properties = addCustomAnnotationProperties, annotations = classAnnotation.getAnnotations()
        )
    }

}
