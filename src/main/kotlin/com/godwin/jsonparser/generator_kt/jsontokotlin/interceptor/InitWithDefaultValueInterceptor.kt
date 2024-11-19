package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.ConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DefaultValueStrategy
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeelements.getDefaultValue
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeelements.getDefaultValueAllowNull

class InitWithDefaultValueInterceptor : IKotlinClassInterceptor<KotlinClass> {
    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        return if (kotlinClass is DataClass) {
            val dataClass: DataClass = kotlinClass

            val initWithDefaultValueProperties = dataClass.properties.map {

                val initDefaultValue = if (ConfigManager.defaultValueStrategy == DefaultValueStrategy.AvoidNull) getDefaultValue(it.type)
                else getDefaultValueAllowNull(it.type)
                it.copy(value = initDefaultValue)
            }

            dataClass.copy(properties = initWithDefaultValueProperties)
        } else {
            kotlinClass
        }
    }
}
