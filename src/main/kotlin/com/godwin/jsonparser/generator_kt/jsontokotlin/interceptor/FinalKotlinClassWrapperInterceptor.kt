package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.KOTLIN_KEYWORD_LIST

/**
 * Interceptor to make kotlin keyword property names valid
 */
class FinalKotlinClassWrapperInterceptor : IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        return if (kotlinClass is DataClass) {
            val keywordValidProperties = kotlinClass.properties.map {
                it.copy(name = with(it.name) {
                    if (isNotEmpty() && (KOTLIN_KEYWORD_LIST.contains(this) || first().isDigit() || contains('$'))) "`$this`" else this
                })
            }
            kotlinClass.copy(properties = keywordValidProperties)
        } else {
            kotlinClass
        }
    }
}
