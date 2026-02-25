package com.godwin.jsonparser.generator.jsontokotlin.interceptor

import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator.jsontokotlin.utils.getOutType


class PropertyTypeNullableStrategyInterceptor : IKotlinClassInterceptor<KotlinClass> {

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {

        return if (kotlinClass is DataClass) {

            val propertyTypeAppliedWithNullableStrategyProperties = kotlinClass.properties.map {

                val propertyTypeAppliedWithNullableStrategy = getOutType(it.type, it.originJsonValue)

                it.copy(type = propertyTypeAppliedWithNullableStrategy)
            }

            kotlinClass.copy(properties = propertyTypeAppliedWithNullableStrategyProperties)
        } else {
            kotlinClass
        }

    }

}
