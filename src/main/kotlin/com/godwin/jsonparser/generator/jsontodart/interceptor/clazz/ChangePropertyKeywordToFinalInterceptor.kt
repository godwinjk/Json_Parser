package com.godwin.jsonparser.generator.jsontodart.interceptor.clazz

import com.godwin.jsonparser.generator.jsontodart.specs.clazz.DartClass


class ChangePropertyKeywordToFinalInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {

        val finalProperties = dartClass.properties.map {
            it.copy(keyword = "final")
        }

        return dartClass.copy(properties = finalProperties)
    }

}
