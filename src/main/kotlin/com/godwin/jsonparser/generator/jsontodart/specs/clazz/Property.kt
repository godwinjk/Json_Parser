package com.godwin.jsonparser.generator.jsontodart.specs.clazz

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
    val refTypeId: Int = -1,
    val originName: String,
    val originJsonValue: String? = ""
) {

    private var separatorBetweenAnnotationAndProperty = "\n"

    fun getCode(): String = buildString {
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

    fun getCodeForConstructor(): String = buildString {
        if (annotations.isNotEmpty()) {
            val annotationsCode = annotations.joinToString("\n") { it.getAnnotationString() }
            if (annotationsCode.isNotBlank()) {
                append(annotationsCode).append(" ")
            }
        }
        append("required ").append(type).append(" ").append(name)
    }

    fun toParsedProperty() = ParsedDartDataClass.Property(
        annotations.map { it.getAnnotationString() },
        keyword,
        name,
        type,
        value,
        comment,
        isLast
    )

    fun getRawName(): String = annotations.firstOrNull { it.rawName.isNotBlank() }?.rawName ?: ""

    companion object {
        fun fromParsedProperty(parsedProperty: ParsedDartDataClass.Property) = Property(
            annotations = parsedProperty.annotations.map { Annotation.fromAnnotationString(it) },
            keyword = parsedProperty.keyword,
            name = parsedProperty.propertyName,
            type = parsedProperty.propertyType,
            value = parsedProperty.propertyValue,
            comment = parsedProperty.propertyComment,
            isLast = parsedProperty.isLastProperty,
            refTypeId = -1,
            originName = parsedProperty.propertyName,
            originJsonValue = parsedProperty.propertyComment.trim().takeIf { it != "null" }
        )
    }
}

fun Property.isListType(): Boolean = type.isListType()

fun Property.isPrimitiveType(): Boolean = type.isPrimitiveType()

fun Property.getGenericType(): String {
    if (!isListType()) return ""
    return type.substringAfter("<").substringBefore(">")
}