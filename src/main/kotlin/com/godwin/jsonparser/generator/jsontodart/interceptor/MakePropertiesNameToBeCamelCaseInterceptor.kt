package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass
import com.godwin.jsonparser.generator.jsontodart.codeelements.KPropertyName

class MakePropertiesNameToBeCamelCaseInterceptor : IDartClassInterceptor {

    override fun intercept(dartClass: DartClass): DartClass {

        val camelCaseNameProperties = dartClass.properties.map {

            val camelCaseName = KPropertyName.makeLowerCamelCaseLegalNameOrEmptyName(it.originName)

            it.copy(name = camelCaseName)
        }

        return dartClass.copy(properties = camelCaseNameProperties)
    }

}
