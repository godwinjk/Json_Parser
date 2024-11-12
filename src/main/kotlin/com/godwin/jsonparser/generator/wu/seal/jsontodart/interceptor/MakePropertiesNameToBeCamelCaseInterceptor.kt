package wu.seal.jsontodart.interceptor

import wu.seal.jsontodart.classscodestruct.KotlinDataClass
import wu.seal.jsontodart.codeelements.KPropertyName

class MakePropertiesNameToBeCamelCaseInterceptor : IKotlinDataClassInterceptor {

    override fun intercept(kotlinDataClass: KotlinDataClass): KotlinDataClass {

        val camelCaseNameProperties = kotlinDataClass.properties.map {

            val camelCaseName = KPropertyName.makeLowerCamelCaseLegalNameOrEmptyName(it.originName)

            it.copy(name = camelCaseName)
        }

        return kotlinDataClass.copy(properties = camelCaseNameProperties)
    }

}
