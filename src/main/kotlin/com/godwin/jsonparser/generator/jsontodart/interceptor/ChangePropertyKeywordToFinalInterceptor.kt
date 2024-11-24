package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass


class ChangePropertyKeywordToFinalInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {

        val finalProperties = dartClass.properties.map {
            it.copy(keyword = "final")
        }

        return dartClass.copy(properties = finalProperties)
    }

}
