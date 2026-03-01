package com.godwin.jsonparser.generator.jsontokotlin.interceptor

import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass


/**
 * Interceptor that apply the `isCommentOff` config enable condition
 */
object CommentOffInterceptor : IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        return if (kotlinClass is DataClass) {
            val newProperty = kotlinClass.properties.map {
                it.copy(comment = "")
            }

            kotlinClass.copy(properties = newProperty)
        } else {
            kotlinClass
        }

    }
}