package com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.custom

import com.godwin.jsonparser.generator.jsontodart.interceptor.clazz.IDartClassInterceptor
import com.godwin.jsonparser.generator.jsontodart.specs.annotations.CustomPropertyAnnotationTemplate
import com.godwin.jsonparser.generator.jsontodart.specs.clazz.Annotation
import com.godwin.jsonparser.generator.jsontodart.specs.clazz.DartClass
import com.godwin.jsonparser.generator.jsontodart.specs.elements.KPropertyName
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager

class AddCustomAnnotationInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {

        val addCustomAnnotationProperties = dartClass.properties.map {

            val camelCaseName = KPropertyName.makeLowerCamelCaseLegalName(it.originName)

            val annotations = CustomPropertyAnnotationTemplate(it.originName).getAnnotations()

            it.copy(annotations = annotations, name = camelCaseName)
        }

        val classAnnotationString = DartConfigManager.customClassAnnotationFormatString

        val classAnnotation = Annotation.fromAnnotationString(classAnnotationString)

        return dartClass.copy(properties = addCustomAnnotationProperties, annotations = listOf(classAnnotation))
    }
}
