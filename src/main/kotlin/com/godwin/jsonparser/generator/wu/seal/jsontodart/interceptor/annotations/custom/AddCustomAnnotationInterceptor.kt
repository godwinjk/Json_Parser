package wu.seal.jsontodart.interceptor.annotations.custom

import wu.seal.jsontodart.ConfigManager
import wu.seal.jsontodart.classscodestruct.Annotation
import wu.seal.jsontodart.classscodestruct.KotlinDataClass
import wu.seal.jsontodart.codeannotations.CustomPropertyAnnotationTemplate
import wu.seal.jsontodart.codeelements.KPropertyName
import wu.seal.jsontodart.interceptor.IKotlinDataClassInterceptor

class AddCustomAnnotationInterceptor : IKotlinDataClassInterceptor {

    override fun intercept(kotlinDataClass: KotlinDataClass): KotlinDataClass {

        val addCustomAnnotationProperties = kotlinDataClass.properties.map {

            val camelCaseName = KPropertyName.makeLowerCamelCaseLegalName(it.originName)

            val annotations = CustomPropertyAnnotationTemplate(it.originName).getAnnotations()

            it.copy(annotations = annotations,name = camelCaseName)
        }

        val classAnnotationString = ConfigManager.customClassAnnotationFormatString

        val classAnnotation = Annotation.fromAnnotationString(classAnnotationString)

        return kotlinDataClass.copy(properties = addCustomAnnotationProperties,annotations = listOf(classAnnotation))
    }
}
