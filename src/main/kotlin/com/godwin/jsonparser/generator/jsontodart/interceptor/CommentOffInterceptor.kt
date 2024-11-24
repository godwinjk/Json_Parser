package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass

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