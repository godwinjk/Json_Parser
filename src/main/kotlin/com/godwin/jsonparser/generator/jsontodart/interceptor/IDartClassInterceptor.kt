package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass

/**
 * Interceptor for code transform
 */
interface IDartClassInterceptor {
    fun intercept(dartClass: DartClass): DartClass
}