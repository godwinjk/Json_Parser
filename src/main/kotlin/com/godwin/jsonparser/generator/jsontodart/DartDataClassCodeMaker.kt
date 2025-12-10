package com.godwin.jsonparser.generator.jsontodart

import com.godwin.jsonparser.generator.jsontodart.interceptor.IDartClassInterceptor
import com.godwin.jsonparser.generator.jsontodart.interceptor.InterceptorManager
import com.godwin.jsonparser.generator.jsontodart.utils.LogUtil

class DartDataClassCodeMaker(
    private val rootClassName: String,
    private val json: String
) {

    fun makeDartDataClassCode(): String {
        val interceptors = InterceptorManager.getEnabledDartDataClassInterceptors()
        return makeDartDataClassCode(interceptors)
    }

    private fun makeDartDataClassCode(interceptors: List<IDartClassInterceptor>): String {
        val dartDataClasses = DartDataClassMaker(rootClassName = rootClassName, json = json).makeDartDataClasses()

        val interceptedDataClasses = dartDataClasses.map { it.applyInterceptors(interceptors) }
        return interceptedDataClasses.joinToString("\n\n") {
            LogUtil.i("DartDataClassCodeMaker makeDartDataClassCode joinToString: $it")
            it.getCode()
        }
    }
}
