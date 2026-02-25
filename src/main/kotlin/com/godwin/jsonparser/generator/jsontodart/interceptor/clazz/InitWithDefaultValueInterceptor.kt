package com.godwin.jsonparser.generator.jsontodart.interceptor.clazz

import com.godwin.jsonparser.generator.jsontodart.specs.clazz.DartClass
import com.godwin.jsonparser.generator.jsontodart.specs.elements.getDefaultValue
import com.godwin.jsonparser.generator.jsontodart.specs.elements.getDefaultValueAllowNull
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.model.DefaultValueStrategy

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
