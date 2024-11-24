package com.godwin.jsonparser.generator.jsontodart.classscodestruct

import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ParsedDartDataClass
import com.godwin.jsonparser.generator.jsontodart.utils.isListType
import com.godwin.jsonparser.generator.jsontodart.utils.isPrimitiveType

data class Property(
    val annotations: List<Annotation>,
    val keyword: String,
    val name: String,
    val type: String,
    val value: String,
    val comment: String,
    val isLast: Boolean,
    val refTypeId: Int = -1, // the id of property type,if can't reference in current generate classes ,use the default value -1
    val originName: String,
    val originJsonValue: String? = ""
) {

    //val genericType: String

    private var separatorBetweenAnnotationAndProperty = "\n"

    fun letLastAnnotationStayInSameLine() {
        separatorBetweenAnnotationAndProperty = " "
    }

    fun getCode(): String {

        return buildString {
            if (annotations.isNotEmpty()) {
                val annotationsCode = annotations.joinToString("\n") { it.getAnnotationString() }
                if (annotationsCode.isNotBlank()) {
                    append(annotationsCode).append(separatorBetweenAnnotationAndProperty)
                }
            }
            if (keyword.isNotEmpty()) {
                append(keyword).append(" ")
            }
            append(type).append(" ").append(name)
            if (value.isNotBlank()) {
                append(" = ").append(value)
            }
        }
    }

    fun toParsedProperty(): ParsedDartDataClass.Property {

        val propertyAnnotationCodeList = annotations.map { annotation -> annotation.getAnnotationString() }

        return ParsedDartDataClass.Property(
            propertyAnnotationCodeList,
            keyword,
            name,
            type,
            value,
            comment,
            isLast
        )

    }

    fun getRawName(): String {

        return if (annotations.isNotEmpty()) {
            val notBlankRawNames = annotations.map { it.rawName }.filter { it.isNotBlank() }
            if (notBlankRawNames.isNotEmpty()) {
                notBlankRawNames[0]
            } else {
                ""
            }
        } else {
            ""
        }
    }

    companion object {

        fun fromParsedProperty(parsedProperty: ParsedDartDataClass.Property): Property {
            val annotations = parsedProperty.annotations.map { Annotation.fromAnnotationString(it) }
            return Property(
                annotations = annotations,
                keyword = parsedProperty.keyword,
                name = parsedProperty.propertyName,
                type = parsedProperty.propertyType,
                value = parsedProperty.propertyValue,
                comment = parsedProperty.propertyComment,
                isLast = parsedProperty.isLastProperty,
                refTypeId = -1,
                originName = parsedProperty.propertyName,
                originJsonValue = if (parsedProperty.propertyComment.trim()
                        .equals("null")
                ) null else parsedProperty.propertyComment.trim()
            )
        }
    }
}

fun Property.isListType(): Boolean {
    return type.isListType()
}

fun Property.isPrimitiveType(): Boolean {
    return type.isPrimitiveType()
}

fun Property.getGenericType(): String {
    if (!isListType()) return ""
    return type.substringAfter("<").substringBefore(">")
}