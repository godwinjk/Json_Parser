package wu.seal.jsontodart.codeannotations

import wu.seal.jsontodart.ConfigManager
import wu.seal.jsontodart.classscodestruct.Annotation

class CustomPropertyAnnotationTemplate(val rawName: String) : AnnotationTemplate {

    private val annotation = Annotation(ConfigManager.customPropertyAnnotationFormatString, rawName)

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }

}