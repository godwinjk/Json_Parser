package com.godwin.jsonparser.generator.jsontodart.specs.clazz

import com.godwin.jsonparser.generator.jsontodart.interceptor.clazz.IDartClassInterceptor
import com.godwin.jsonparser.generator.jsontodart.utils.*
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ParsedDartDataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager

data class DartClass(
    val id: Int = -1, // -1 represent the default unknown id
    val annotations: List<Annotation>,
    val name: String,
    val properties: List<Property>,
    val nestedClasses: List<DartClass> = listOf(),
    val parentClassTemplate: String = "",
    val mixinClass: String = ""
) {

    fun getCode(extraIndent: String = ""): String {
        val indent = getIndent()
        val className = if (DartConfigManager.isDartModelClassName) toPascalCase(name) else name

        val code = buildString {
            annotations.mapNotNull { it.getAnnotationString().takeIf { str -> str.isNotBlank() } }
                .forEach { append(it).append("\n") }

            append("class $className")
            if (parentClassTemplate.isNotBlank()) append(" extends $parentClassTemplate")
            if (mixinClass.isNotBlank()) append(" with $mixinClass")
            append(" {\n")

            if (!DartConfigManager.isFreezedAnnotation) {
                properties.forEach { p ->
                    append(indent).append(p.getCode()).append(";")
                    if (p.comment.isNotBlank()) append(" // ").append(getCommentCode(p.comment))
                    append("\n")
                }
            }

            if (!DartConfigManager.isFreezedAnnotation) {
                append("\n$indent$className({")
                properties.joinToString(", ") { p ->
                    val required =
                        if (!DartConfigManager.isPropertyOptional || DartConfigManager.isPropertyFinal) "required" else ""
                    "$required this.${p.name}"
                }.also { append(it) }
                append("});\n")
            } else {
                append("\n${indent}factory $className({")
                properties.forEachIndexed { index, p ->
                    val constructorCode =
                        p.getCodeForConstructor().split("\n").joinToString("\n") { indent + indent + it }
                    append("\n").append(constructorCode)
                    if (index < properties.size - 1) append(",")
                }
                append("\n$indent}) = _$className;\n")
            }

            append("\n$indent")
            when {
                DartConfigManager.isFreezedAnnotation -> {
                    append("factory $className.fromJson(Map<String, dynamic> json) => _\$${className}FromJson(json);\n")
                }

                DartConfigManager.isJsonSerializationAnnotation -> {
                    append("factory $className.fromJson(Map<String, dynamic> json) => _\$${className}FromJson(json);\n")
                    append("${indent}Map<String, dynamic> toJson() => _\$${className}ToJson(this);\n")
                }

                else -> {
                    append("factory $className.fromJson(Map<String, dynamic> json) {\n")
                    append("$indent${indent}return $className(\n")
                    properties.forEach { p ->
                        val jsonKey = "json['${p.originName}']"
                        val value = when {
                            p.isListType() && p.getGenericType().isPrimitiveType() ->
                                if (DartConfigManager.isPropertyNullable) "$jsonKey != null ? List<${p.getGenericType()}>.from($jsonKey) : null"
                                else "List<${p.getGenericType()}>.from($jsonKey)"

                            p.isListType() ->
                                if (DartConfigManager.isPropertyNullable) "$jsonKey != null ? ($jsonKey as List).map((i) => ${p.getGenericType()}.fromJson(i)).toList() : null"
                                else "($jsonKey as List).map((i) => ${p.getGenericType()}.fromJson(i)).toList()"

                            p.isPrimitiveType() -> jsonKey

                            else ->
                                if (DartConfigManager.isPropertyNullable) "$jsonKey != null ? ${p.type}.fromJson($jsonKey) : null"
                                else "${p.type}.fromJson($jsonKey)"
                        }
                        append("$indent$indent$indent${p.name}: $value,\n")
                    }
                    append("$indent$indent);\n")
                    append("$indent}\n")
                }
            }

            if (!DartConfigManager.isFreezedAnnotation && !DartConfigManager.isJsonSerializationAnnotation) {
                append("\n${indent}Map<String, dynamic> toJson() {\n")
                append("$indent${indent}final data = <String, dynamic>{};\n")
                properties.forEach { p ->
                    val key = "data['${p.originName}']"
                    val value = p.name
                    when {
                        p.isPrimitiveType() -> append("$indent$indent$key = $value;\n")

                        p.isListType() && p.getGenericType().isPrimitiveType() -> {
                            if (DartConfigManager.isPropertyNullable) {
                                append("$indent${indent}if ($value != null) {\n")
                                append("$indent$indent$indent$key = $value;\n")
                                append("$indent$indent}\n")
                            } else {
                                append("$indent$indent$key = $value;\n")
                            }
                        }

                        p.isListType() -> {
                            if (DartConfigManager.isPropertyNullable) {
                                append("$indent${indent}if ($value != null) {\n")
                                append("$indent$indent$indent$key = $value.map((v) => v.toJson()).toList();\n")
                                append("$indent$indent}\n")
                            } else {
                                append("$indent$indent$key = $value.map((v) => v.toJson()).toList();\n")
                            }
                        }

                        else -> {
                            if (DartConfigManager.isPropertyNullable) {
                                append("$indent${indent}if ($value != null) {\n")
                                append("$indent$indent$indent$key = $value.toJson();\n")
                                append("$indent${indent}}\n")
                            } else {
                                append("$indent$indent$key = $value.toJson();\n")
                            }
                        }
                    }
                }
                append("$indent${indent}return data;\n")
                append("$indent}\n")
            }

            append("}")
        }

        return if (extraIndent.isNotEmpty()) {
            code.lineSequence().joinToString("\n") {
                if (it.isNotBlank()) extraIndent + it else it
            }
        } else {
            code
        }
    }


    fun applyInterceptors(interceptors: List<IDartClassInterceptor>): DartClass {
        var dataClass = this
        interceptors.forEach {
            dataClass = dataClass.applyInterceptorWithNestedClasses(it)
        }
        return dataClass
    }

    private fun applyInterceptorWithNestedClasses(interceptor: IDartClassInterceptor): DartClass {
        if (nestedClasses.isNotEmpty()) {
            val newNestedClasses = nestedClasses.map { it.applyInterceptorWithNestedClasses(interceptor) }
            return interceptor.intercept(this).copy(nestedClasses = newNestedClasses)
        }
        return interceptor.intercept(this)
    }

    fun toParsedDartClass(): ParsedDartDataClass {
        val annotationCodeList = annotations.map { it.getAnnotationString() }

        val parsedProperties = properties.map { it.toParsedProperty() }
        val className = if (DartConfigManager.isDartModelClassName) toPascalCase(name) else name
        val fileName = if (DartConfigManager.isDartModelClassName) camelCaseToSnakeCase(name) else name
        return ParsedDartDataClass(annotationCodeList, className, fileName, parsedProperties)
    }

    companion object {
        fun fromParsedDartClass(parsedDartDataClass: ParsedDartDataClass): DartClass {
            val annotations = parsedDartDataClass.annotations.map { Annotation.fromAnnotationString(it) }
            val propertyNames = parsedDartDataClass.properties.map { it.propertyName }
            val properties = parsedDartDataClass.properties.map {
                if (propertyNames.contains(it.propertyName + BACKSTAGE_NULLABLE_POSTFIX)) it.copy(propertyType = it.propertyType + "?") else it
            }.filter { it.propertyName.endsWith(BACKSTAGE_NULLABLE_POSTFIX).not() }
                .map { Property.fromParsedProperty(it) }
            return DartClass(
                annotations = annotations, id = -1, name = parsedDartDataClass.name, properties = properties
            )
        }
    }
}
