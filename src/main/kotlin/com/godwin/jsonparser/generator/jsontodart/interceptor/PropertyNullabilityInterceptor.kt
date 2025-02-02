package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager


class PropertyNullabilityInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {
        val finalProperties = dartClass.properties.map {
            it.copy(nullability = DartConfigManager.isPropertyNullable)
        }

        return dartClass.copy(properties = finalProperties)
    }

}
