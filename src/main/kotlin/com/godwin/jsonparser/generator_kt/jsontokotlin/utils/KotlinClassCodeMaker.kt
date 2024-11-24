package com.godwin.jsonparser.generator_kt.jsontokotlin.utils

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IKotlinClassInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.InterceptorManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass


class KotlinClassCodeMaker(private val kotlinClass: KotlinClass, private val generatedFromJSONSchema: Boolean = false) {

    fun makeKotlinClassCode(): String {
        val interceptors = InterceptorManager.getEnabledKotlinDataClassInterceptors()
        return makeKotlinClassCode(interceptors)
    }

    private fun makeKotlinClassCode(interceptors: List<IKotlinClassInterceptor<KotlinClass>>): String {
        var kotlinClassForCodeGenerate = kotlinClass
        kotlinClassForCodeGenerate = kotlinClassForCodeGenerate.applyInterceptors(interceptors)
        return if (KotlinConfigManager.isInnerClassModel) {
            kotlinClassForCodeGenerate.getCode()
        } else {
            val resolveNameConflicts = kotlinClassForCodeGenerate.resolveNameConflicts()
            val allModifiableClassesRecursivelyIncludeSelf = resolveNameConflicts
                .getAllModifiableClassesRecursivelyIncludeSelf()
            if (generatedFromJSONSchema) { //don't remove class when their properties are same
                allModifiableClassesRecursivelyIncludeSelf
                    .joinToString("\n\n") { it.getOnlyCurrentCode() }
            } else {
                allModifiableClassesRecursivelyIncludeSelf.distinctByPropertiesAndSimilarClassName()
                    .joinToString("\n\n") { it.getOnlyCurrentCode() }
            }
        }
    }

}
