package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.ConfigManager
import com.godwin.jsonparser.generator.jsontodart.DefaultValueStrategy
import com.godwin.jsonparser.generator.jsontodart.classscodestruct.KotlinDataClass
import com.godwin.jsonparser.generator.jsontodart.codeelements.getDefaultValue
import com.godwin.jsonparser.generator.jsontodart.codeelements.getDefaultValueAllowNull

class InitWithDefaultValueInterceptor : IKotlinDataClassInterceptor {

    override fun intercept(kotlinDataClass: KotlinDataClass): KotlinDataClass {

        val getValue: (arg: String) -> String =
                if (ConfigManager.defaultValueStrategy == DefaultValueStrategy.AvoidNull) ::getDefaultValue
                else ::getDefaultValueAllowNull

        val initWithDefaultValueProperties = kotlinDataClass.properties.map {

            val initDefaultValue = getValue(it.type)

            it.copy(value = initDefaultValue)
        }

        return kotlinDataClass.copy(properties = initWithDefaultValueProperties)
    }

}
