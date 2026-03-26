package com.godwin.jsonparser.generator.jsontodart.interceptor.annotations.dart

import com.godwin.jsonparser.generator.jsontodart.interceptor.clazz.IDartClassInterceptor
import com.godwin.jsonparser.generator.jsontodart.specs.annotations.FreezedClassAnnotationTemplate
import com.godwin.jsonparser.generator.jsontodart.specs.annotations.JsonSerializableClassAnnotationTemplate
import com.godwin.jsonparser.generator.jsontodart.specs.annotations.JsonSerializablePropertyAnnotationTemplate
import com.godwin.jsonparser.generator.jsontodart.specs.clazz.DartClass
import com.godwin.jsonparser.generator.jsontodart.utils.toPascalCase
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager

/**
 * Handles both @freezed and @JsonSerializable class/property annotation injection.
 * Replaces the former FreezedInterceptor and JsonSerializableInterceptor.
 */
class DartAnnotationInterceptor(
    private val isFreezed: Boolean = DartConfigManager.isFreezedAnnotation,
    private val isJsonSerializable: Boolean = DartConfigManager.isJsonSerializationAnnotation
) : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {
        var result = dartClass

        if (isJsonSerializable) {
            result = applyJsonSerializableAnnotations(result)
        }

        if (isFreezed) {
            result = applyFreezedAnnotations(result)
        }

        return result
    }

    private fun applyJsonSerializableAnnotations(dartClass: DartClass): DartClass {
        val annotatedProperties = dartClass.properties.map { property ->
            val annotations = JsonSerializablePropertyAnnotationTemplate(property.originName).getAnnotations()
            property.copy(annotations = annotations)
        }
        // Class-level @JsonSerializable only when not combined with freezed
        // (freezed handles the class annotation itself)
        return if (isFreezed) {
            dartClass
        } else {
            dartClass.copy(
                properties = annotatedProperties,
                annotations = JsonSerializableClassAnnotationTemplate().getAnnotations()
            )
        }
    }

    private fun applyFreezedAnnotations(dartClass: DartClass): DartClass {
        val mixin = "_$" + toPascalCase(dartClass.name)
        return dartClass.copy(
            annotations = FreezedClassAnnotationTemplate().getAnnotations(),
            mixinClass = mixin
        )
    }
}
