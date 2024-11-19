package com.godwin.jsonparser.generator.jsontodart.interceptor

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.KotlinDataClass
import com.godwin.jsonparser.generator.jsontodart.codeelements.KPropertyName

class MakePropertiesNameToBeCamelCaseInterceptor : IKotlinDataClassInterceptor {

    override fun intercept(kotlinDataClass: KotlinDataClass): KotlinDataClass {

        val camelCaseNameProperties = kotlinDataClass.properties.map {

            val camelCaseName = KPropertyName.makeLowerCamelCaseLegalNameOrEmptyName(it.originName)

            it.copy(name = camelCaseName)
        }

        return kotlinDataClass.copy(properties = camelCaseNameProperties)
    }

}
