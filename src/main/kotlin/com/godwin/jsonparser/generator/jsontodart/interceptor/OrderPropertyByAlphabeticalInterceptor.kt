package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass
import com.godwin.jsonparser.generator.jsontodart.classscodestruct.Property


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

