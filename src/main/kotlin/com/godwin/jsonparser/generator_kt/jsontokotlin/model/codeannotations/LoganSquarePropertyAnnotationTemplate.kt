package com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeannotations
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.Annotation

class LoganSquarePropertyAnnotationTemplate(val rawName: String) : AnnotationTemplate {

    companion object {

        const val propertyAnnotationFormat = "@JsonField(name = arrayOf(\"%s\"))"
    }

    private val annotation = Annotation(propertyAnnotationFormat, rawName)

    override fun getCode(): String {
        return annotation.getAnnotationString()
    }

    override fun getAnnotations(): List<Annotation> {
        return listOf(annotation)
    }
}
