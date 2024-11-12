package wu.seal.jsontodart.codeannotations

import wu.seal.jsontodart.classscodestruct.Annotation

interface AnnotationTemplate {
    fun getCode():String
    fun getAnnotations():List<Annotation>
}