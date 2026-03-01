package com.godwin.jsonparser.generator.jsontodart.interceptor.clazz

import com.godwin.jsonparser.generator.jsontodart.specs.clazz.DartClass
import com.godwin.jsonparser.generator.jsontodart.specs.clazz.Property


class OrderPropertyByAlphabeticalInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {
        val orderByAlphabeticalProperties = dartClass.properties.sortedBy { it.name }.resetIsLastFieldValueOfProperty()
        return dartClass.copy(properties = orderByAlphabeticalProperties)
    }


    private fun List<Property>.resetIsLastFieldValueOfProperty(): List<Property> {
        return mapIndexed { index, property ->
            property.copy(isLast = index == lastIndex)
        }
    }

}

