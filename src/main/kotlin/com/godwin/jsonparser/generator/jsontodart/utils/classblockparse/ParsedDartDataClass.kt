package com.godwin.jsonparser.generator.jsontodart.utils.classblockparse

import com.godwin.jsonparser.generator.jsontodart.utils.getCommentCode
import com.godwin.jsonparser.generator.jsontodart.utils.getIndent

data class ParsedDartDataClass(
    val annotations: List<String>,
    val name: String,
    val fileName: String,
    val properties: List<Property>,
    var importStatement: String = ""
) {

    companion object {
        /**
         * Represent this is Not a ParsedKotlinDataClass Type
         */
        val NONE = ParsedDartDataClass(listOf(), "", "", listOf())
    }

    override fun toString(): String {
        return buildString {
            annotations.forEach {
                if (it.isNotBlank()) {
                    append(it)
                    append("\n")
                }
            }
            append("class ").append(name).append("{")
            append("\n")
            properties.forEach {
                append(it.toString())
                append("\n")
            }
            append("}")
        }
    }

    data class Property(
        val annotations: List<String>,
        val keyword: String,
        val propertyName: String,
        var propertyType: String,
        val propertyValue: String,
        val propertyComment: String,
        val isLastProperty: Boolean,
        var classPropertyTypeRef: ParsedDartDataClass = NONE
    ) {
        override fun toString(): String {
            val indent = getIndent()
            return buildString {
                if (annotations.size > 1) {
                    annotations.forEach {
                        if (it.isNotBlank()) {
                            append(indent).append(it).append("\n")
                        }
                    }

                    append(indent).append(keyword).append(" ").append(propertyType).append(" ").append(propertyName)

                    if (propertyValue.isNotBlank()) {
                        append(" = ").append(propertyValue)
                    }
                    append(";")
                    if (propertyComment.isNotBlank()) {
                        append(" // ").append(getCommentCode(propertyComment))
                    }
                } else {
                    append(indent)
                    if (annotations.size == 1 && annotations[0].isNotBlank()) {
                        append(annotations[0])
                        append(" ")
                    }
                    append(indent)
                    if (keyword.isNotEmpty()) {
                        append(keyword).append(" ")
                    }
                    append(propertyType).append(" ").append(propertyName)
                    if (propertyValue.isNotBlank()) {
                        append(" = ").append(propertyValue)
                    }
                    append(";")
                    if (propertyComment.isNotBlank()) {
                        append(" // ").append(propertyComment)
                    }
                }
            }
        }
    }
}