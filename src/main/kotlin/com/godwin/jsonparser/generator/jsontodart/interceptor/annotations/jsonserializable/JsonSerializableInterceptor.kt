package com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.jsonserializable

import com.godwin.jsonparser.generator.jsontodart.interceptor.clazz.IDartClassInterceptor
import com.godwin.jsonparser.generator.jsontodart.specs.annotations.JsonSerializableClassAnnotationTemplate
import com.godwin.jsonparser.generator.jsontodart.specs.annotations.JsonSerializablePropertyAnnotationTemplate
import com.godwin.jsonparser.generator.jsontodart.specs.clazz.DartClass
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager

class JsonSerializableInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {
        val addCustomAnnotationProperties = dartClass.properties.map {
            val annotations = JsonSerializablePropertyAnnotationTemplate(it.originName).getAnnotations()
            it.copy(annotations = annotations)
        }
        val classAnnotation = JsonSerializableClassAnnotationTemplate()
        return if (DartConfigManager.isFreezedAnnotation) {
            dartClass.copy(properties = addCustomAnnotationProperties)
        } else {
            dartClass.copy(properties = addCustomAnnotationProperties, annotations = classAnnotation.getAnnotations())
        }
    }

}
