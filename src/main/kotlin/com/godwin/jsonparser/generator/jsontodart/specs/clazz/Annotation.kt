package com.godwin.jsonparser.generator.jsontodart.specs.clazz

import com.godwin.jsonparser.generator.jsontodart.utils.numberOf
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager

data class Annotation(val annotationTemplate: String, val rawName: String) {

    fun getAnnotationString(): String {
        if (!annotationTemplate.contains("%s")) return annotationTemplate
        val countS = annotationTemplate.numberOf("%s")
        return annotationTemplate.format(*Array(countS) { rawName })
    }

    companion object {

        private fun fromOneLineAnnotationString(annotationString: String): Annotation {
            require(!annotationString.contains("\n")) {
                "Only support one line annotation!! current is $annotationString"
            }

            if (!annotationString.contains("\"")) {
                return Annotation(annotationString, "")
            }

            require(annotationString.numberOf("\"") == 2) {
                "Only support one line annotation with one couple Double quotes!! current is $annotationString"
            }

            val rawName = annotationString.substringAfter("\"").substringBefore("\"")
            val annotationTemplate =
                annotationString.substringBefore("\"") + "\"%s\"" + annotationString.substringAfterLast("\"")
            return Annotation(annotationTemplate, rawName)
        }

        private fun fromMultipleLineAnnotationString(multipleLineString: String): Annotation {
            val annotations = multipleLineString.trim().split("\n").map { fromOneLineAnnotationString(it) }
            val annotationTemplate = annotations.joinToString("\n") { it.annotationTemplate }
            val rawName = annotations.firstOrNull { it.rawName.isNotBlank() }?.rawName ?: ""
            return Annotation(annotationTemplate, rawName)
        }

        fun fromAnnotationString(annotationString: String): Annotation {
            return try {
                if (!annotationString.trim().contains("\n")) {
                    fromOneLineAnnotationString(annotationString)
                } else {
                    fromMultipleLineAnnotationString(annotationString)
                }
            } catch (e: IllegalArgumentException) {
                val annotationTemplate = DartConfigManager.customPropertyAnnotationFormatString
                if (!annotationTemplate.contains("%s")) {
                    Annotation(annotationTemplate, "")
                } else {
                    val pre = annotationTemplate.substringBefore("%s")
                    val rawName = annotationString.substringAfter(pre).substringBefore("\"")
                    Annotation(annotationTemplate, rawName)
                }
            }
        }
    }
}
