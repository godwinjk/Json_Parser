package com.godwin.jsonparser.generator.jsontodart.interceptor.clazz

import com.godwin.jsonparser.generator.jsontodart.specs.clazz.DartClass

/**
 * Interceptor that apply the `isCommentOff` config enable condition
 */
object CommentOffInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {
        val newProperty = dartClass.properties.map {
            it.copy(comment = "")
        }

        return dartClass.copy(properties = newProperty)
    }
}