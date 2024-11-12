package wu.seal.jsontodart.interceptor

import wu.seal.jsontodart.ConfigManager
import wu.seal.jsontodart.DefaultValueStrategy
import wu.seal.jsontodart.classscodestruct.KotlinDataClass
import wu.seal.jsontodart.codeelements.getDefaultValue
import wu.seal.jsontodart.codeelements.getDefaultValueAllowNull

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
