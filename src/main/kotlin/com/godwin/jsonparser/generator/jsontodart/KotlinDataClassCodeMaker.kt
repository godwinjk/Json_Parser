package com.godwin.jsonparser.generator.jsontodart

import com.godwin.jsonparser.generator.jsontodart.interceptor.IKotlinDataClassInterceptor
import com.godwin.jsonparser.generator.jsontodart.interceptor.InterceptorManager
import com.godwin.jsonparser.generator.jsontodart.utils.LogUtil

class KotlinDataClassCodeMaker(private val rootClassName: String, private val json: String) {

    fun makeKotlinDataClassCode(): String {
        val interceptors = InterceptorManager.getEnabledKotlinDataClassInterceptors()

        return if (needMakeKotlinCodeByKotlinDataClass()) {
            makeKotlinDataClassCode(interceptors)
        } else {
            makeKotlinDataClassCode(interceptors)
        }
    }

    private fun needMakeKotlinCodeByKotlinDataClass(): Boolean {
        return InterceptorManager.getEnabledKotlinDataClassInterceptors().isNotEmpty()
    }

    private fun makeKotlinDataClassCode(interceptors: List<IKotlinDataClassInterceptor>): String {

        val kotlinDataClasses = KotlinDataClassMaker(rootClassName = rootClassName, json = json).makeKotlinDataClasses()
        LogUtil.i("KotlinDataClassCodeMaker makeKotlinDataClassCode kotlinDataClasses: $kotlinDataClasses")

        val interceptedDataClasses = kotlinDataClasses.map { it.applyInterceptors(interceptors) }
        return interceptedDataClasses.joinToString("\n\n\n\n") {

            LogUtil.i("KotlinDataClassCodeMaker makeKotlinDataClassCode joinToString: $it")

            it.getCode()
        }
    }
}
