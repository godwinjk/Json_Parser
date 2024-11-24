package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass

/**
 * Use this try to recover the origin name that before property name formatting to camel case
 */
class MakePropertyOriginNameInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {

        val makeUpOriginNameProperties = dartClass.properties.map {
            val rawName = it.getRawName()
            return@map if (rawName.isEmpty()) {
                it.copy(originName = it.name)
            } else {
                it.copy(originName = rawName)
            }
        }

        return dartClass.copy(properties = makeUpOriginNameProperties)
    }
}