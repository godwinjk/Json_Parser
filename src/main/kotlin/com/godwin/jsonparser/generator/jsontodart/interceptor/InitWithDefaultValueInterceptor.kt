package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass
import com.godwin.jsonparser.generator.jsontodart.codeelements.getDefaultValue
import com.godwin.jsonparser.generator.jsontodart.codeelements.getDefaultValueAllowNull
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DefaultValueStrategy

class InitWithDefaultValueInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {

        val getValue: (arg: String) -> String =
            if (DartConfigManager.defaultValueStrategy == DefaultValueStrategy.AvoidNull) ::getDefaultValue
            else ::getDefaultValueAllowNull

        val initWithDefaultValueProperties = dartClass.properties.map {
            val initDefaultValue = getValue(it.type)
            it.copy(value = initDefaultValue)
        }
        return dartClass.copy(properties = initWithDefaultValueProperties)
    }

}
