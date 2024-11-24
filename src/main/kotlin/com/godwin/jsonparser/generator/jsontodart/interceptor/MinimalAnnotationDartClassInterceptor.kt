package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass

/**
 * interceptor to make the code to be like the minimal annotation
 * which means that if the property name is the same as raw name then remove the
 * annotations contains %s
 */
class MinimalAnnotationDartClassInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {
        val newProperties = dartClass.properties.map { p ->
            if (p.originName == p.name) {
                p.copy(annotations = p.annotations.filter { it.rawName.isBlank() })
            } else {
                p
            }
        }

        return dartClass.copy(properties = newProperties)

    }


}
