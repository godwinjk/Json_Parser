package com.godwin.jsonparser.generator.jsontodart.interceptor.clazz

import com.godwin.jsonparser.generator.jsontodart.specs.clazz.DartClass

/**
 * Interceptor for code transform
 */
interface IDartClassInterceptor {
    fun intercept(dartClass: DartClass): DartClass
}