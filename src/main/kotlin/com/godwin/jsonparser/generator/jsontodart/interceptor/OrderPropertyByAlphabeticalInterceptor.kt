package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.KotlinDataClass
import com.godwin.jsonparser.generator.jsontodart.classscodestruct.Property


class OrderPropertyByAlphabeticalInterceptor : IKotlinDataClassInterceptor {

    override fun intercept(kotlinDataClass: KotlinDataClass): KotlinDataClass {


        val orderByAlphabeticalProperties = kotlinDataClass.properties.sortedBy { it.name }.resetIsLastFieldValueOfProperty()


        return kotlinDataClass.copy(properties = orderByAlphabeticalProperties)
    }


    private fun List<Property>.resetIsLastFieldValueOfProperty(): List<Property> {

        return mapIndexed { index, property ->

            property.copy(isLast = index == lastIndex)

        }

    }


}

