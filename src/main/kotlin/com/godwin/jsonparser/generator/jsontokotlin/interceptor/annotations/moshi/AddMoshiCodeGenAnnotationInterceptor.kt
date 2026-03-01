package com.godwin.jsonparser.generator.jsontokotlin.interceptor.annotations.moshi

import com.godwin.jsonparser.generator.jsontokotlin.interceptor.IKotlinClassInterceptor
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.Annotation
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator.jsontokotlin.model.codeannotations.MoshiPropertyAnnotationTemplate
import com.godwin.jsonparser.generator.jsontokotlin.model.codeelements.KPropertyName

/**
 * This interceptor try to add Moshi(code gen) annotation
 */
class AddMoshiCodeGenAnnotationInterceptor : IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        if (kotlinClass is DataClass) {
            val addMoshiCodeGenAnnotationProperties = kotlinClass.properties.map {

                val camelCaseName = KPropertyName.makePropertyName(it.originName, true)
                it.copy(
                    annotations = MoshiPropertyAnnotationTemplate(it.originName).getAnnotations(),
                    name = camelCaseName
                )
            }
            val classAnnotationString = "@JsonClass(generateAdapter = true)"

            val classAnnotation = Annotation.fromAnnotationString(classAnnotationString)

            return kotlinClass.copy(
                properties = addMoshiCodeGenAnnotationProperties,
                annotations = listOf(classAnnotation)
            )
        } else {
            return kotlinClass
        }
    }
}