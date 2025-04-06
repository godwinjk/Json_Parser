package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass
import com.godwin.jsonparser.generator.jsontodart.utils.DART_KEYWORD_LIST
import com.godwin.jsonparser.generator_kt.extensions.wu.seal.KeepAnnotationSupportForAndroidX.append

/**
 * Interceptor to make kotlin keyword property names valid
 */
class MakeKeywordNamedPropertyValidInterceptor : IDartClassInterceptor {
    override fun intercept(dartClass: DartClass): DartClass {

        val keywordValidProperties = dartClass.properties.map {
            if (DART_KEYWORD_LIST.contains(it.name)) {
                it.copy(
                    name = "${dartClass.name[0].lowercaseChar()}_${it.name}",
                    comment = it.comment.append("Property name renamed to ${it.name}->${dartClass.name[0].lowercaseChar()}_${it.name}")
                )
            } else {
                it
            }
        }

        return dartClass.copy(properties = keywordValidProperties)
    }
}
