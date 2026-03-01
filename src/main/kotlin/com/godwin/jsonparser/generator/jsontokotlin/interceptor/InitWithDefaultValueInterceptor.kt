package com.godwin.jsonparser.generator.jsontokotlin.interceptor

import com.godwin.jsonparser.generator.jsontokotlin.model.DefaultValueStrategy
import com.godwin.jsonparser.generator.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator.jsontokotlin.model.codeelements.getDefaultValue
import com.godwin.jsonparser.generator.jsontokotlin.model.codeelements.getDefaultValueAllowNull

class InitWithDefaultValueInterceptor : IKotlinClassInterceptor<KotlinClass> {
    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        return if (kotlinClass is DataClass) {
            val dataClass: DataClass = kotlinClass

            val initWithDefaultValueProperties = dataClass.properties.map {

                val initDefaultValue =
                    if (KotlinConfigManager.defaultValueStrategy == DefaultValueStrategy.AvoidNull) getDefaultValue(it.type)
                    else getDefaultValueAllowNull(it.type)
                it.copy(value = initDefaultValue)
            }

            dataClass.copy(properties = initWithDefaultValueProperties)
        } else {
            kotlinClass
        }
    }
}
