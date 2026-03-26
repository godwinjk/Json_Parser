package com.godwin.jsonparser.generator.jsontodart.specs.clazz

import com.godwin.jsonparser.generator.jsontodart.interceptor.clazz.IDartClassInterceptor
import com.godwin.jsonparser.generator.jsontodart.utils.*
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ParsedDartDataClass
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager

data class DartClass(
    val id: Int = -1,
    val annotations: List<Annotation>,
    val name: String,
    val properties: List<Property>,
    val nestedClasses: List<DartClass> = listOf(),
    val parentClassTemplate: String = "",
    val mixinClass: String = ""
) {

    fun getCode(extraIndent: String = ""): String {
        val indent = getIndent()
        val className = toPascalCase(name)

        val code = buildString {
            appendAnnotations()
            appendClassDeclaration(className)
            appendProperties(indent)
            appendConstructor(indent, className)
            appendSerializationMethods(indent, className)
            append("}")
            if (DartConfigManager.isFreezedAnnotation && !DartConfigManager.isJsonSerializationAnnotation) {
                append("\n\n")
                appendFreezedExtension(indent, className)
            }
        }

        return applyExtraIndent(code, extraIndent)
    }

    private fun StringBuilder.appendAnnotations() {
        annotations
            .mapNotNull { it.getAnnotationString().takeIf { str -> str.isNotBlank() } }
            .forEach { append(it).append("\n") }
    }

    private fun StringBuilder.appendClassDeclaration(className: String) {
        append("class $className")
        if (parentClassTemplate.isNotBlank()) append(" extends $parentClassTemplate")
        if (mixinClass.isNotBlank()) append(" with $mixinClass")
        append(" {\n")
    }

    private fun StringBuilder.appendProperties(indent: String) {
        if (DartConfigManager.isFreezedAnnotation) return
        properties.forEach { property ->
            append(indent).append(property.getCode()).append(";")
            if (property.comment.isNotBlank()) append(" // ").append(getCommentCode(property.comment))
            append("\n")
        }
    }

    private fun StringBuilder.appendConstructor(indent: String, className: String) {
        if (DartConfigManager.isFreezedAnnotation) appendFreezedConstructor(indent, className)
        else appendStandardConstructor(indent, className)
    }

    private fun StringBuilder.appendStandardConstructor(indent: String, className: String) {
        append("\n$indent$className(")
        if (properties.isNotEmpty()) {
            val required = if (isRequiredParameter()) "required " else ""
            val params = properties.joinToString(", ") { "${required}this.${it.name}" }
            append("{$params}")
        }
        append(");\n")
    }

    private fun StringBuilder.appendFreezedConstructor(indent: String, className: String) {
        append("\n${indent}const factory $className(")
        if (properties.isNotEmpty()) {
            append("{")
            properties.forEachIndexed { index, property ->
                val constructorCode = property.getCodeForConstructor()
                    .split("\n")
                    .joinToString("\n") { "$indent$indent$it" }
                append("\n").append(constructorCode)
                if (index < properties.lastIndex) append(",")
            }
            append("\n$indent}")
        }
        append(") = _$className;\n")
    }

    private fun isRequiredParameter() = !DartConfigManager.isPropertyOptional || DartConfigManager.isPropertyFinal

    private fun StringBuilder.appendFreezedExtension(indent: String, className: String) {
        append("extension ${className}Json on $className {\n")
        // static fromJson helper
        append("${indent}static $className fromJson(Map<String, dynamic> json) {\n")
        append("$indent${indent}return $className(\n")
        properties.forEach { property ->
            val jsonKey = "json['${property.originName}']"
            val value = ManualSerializationStrategy.fromJsonValue(property, jsonKey, classPrefix = "Json")
            append("$indent$indent${indent}${property.name}: $value,\n")
        }
        append("$indent${indent});\n")
        append("$indent}\n")
        // toJson extension method
        append("\n${indent}Map<String, dynamic> toJson() {\n")
        append("$indent${indent}final data = <String, dynamic>{};\n")
        properties.forEach { property ->
            val key = "data['${property.originName}']"
            val serialization = when {
                property.isPrimitiveType() -> property.name
                property.isListType() && property.getGenericType().isPrimitiveType() -> property.name
                property.isListType() -> "${property.name}.map((v) => v.toJson()).toList()"
                else -> "${property.name}.toJson()"
            }
            append("$indent$indent$key = $serialization;\n")
        }
        append("$indent${indent}return data;\n")
        append("$indent}\n")
        append("}")
    }

    private fun StringBuilder.appendSerializationMethods(indent: String, className: String) {
        val strategy = getSerializationStrategy()
        if (strategy === FreezedOnlyStrategy) return
        append("\n$indent")
        strategy.appendMethods(this, indent, className, properties)
    }

    private fun getSerializationStrategy(): SerializationStrategy = when {
        DartConfigManager.isFreezedAnnotation && DartConfigManager.isJsonSerializationAnnotation ->
            DelegateSerializationStrategy(includeToJson = false)
        DartConfigManager.isFreezedAnnotation ->
            FreezedOnlyStrategy
        DartConfigManager.isJsonSerializationAnnotation ->
            DelegateSerializationStrategy(includeToJson = true)
        else ->
            ManualSerializationStrategy
    }

    private fun applyExtraIndent(code: String, extraIndent: String): String {
        if (extraIndent.isEmpty()) return code
        return code.lineSequence().joinToString("\n") { if (it.isNotBlank()) extraIndent + it else it }
    }

    fun applyInterceptors(interceptors: List<IDartClassInterceptor>): DartClass {
        return interceptors.fold(this) { dartClass, interceptor ->
            dartClass.applyInterceptorWithNestedClasses(interceptor)
        }
    }

    private fun applyInterceptorWithNestedClasses(interceptor: IDartClassInterceptor): DartClass {
        if (nestedClasses.isEmpty()) return interceptor.intercept(this)
        val updatedNestedClasses = nestedClasses.map { it.applyInterceptorWithNestedClasses(interceptor) }
        return interceptor.intercept(this).copy(nestedClasses = updatedNestedClasses)
    }

    companion object {
        fun fromParsedDartClass(parsedDartDataClass: ParsedDartDataClass): DartClass {
            return DartClass(
                annotations = parsedDartDataClass.annotations.map { Annotation.fromAnnotationString(it) },
                id = -1,
                name = parsedDartDataClass.name,
                properties = parseProperties(parsedDartDataClass.properties)
            )
        }

        private fun parseProperties(parsedProperties: List<ParsedDartDataClass.Property>): List<Property> {
            val propertyNames = parsedProperties.map { it.propertyName }
            return parsedProperties
                .map { property ->
                    if (propertyNames.contains(property.propertyName + BACKSTAGE_NULLABLE_POSTFIX))
                        property.copy(propertyType = property.propertyType + "?")
                    else property
                }
                .filterNot { it.propertyName.endsWith(BACKSTAGE_NULLABLE_POSTFIX) }
                .map { Property.fromParsedProperty(it) }
        }

        fun getDefaultValue(type: String): String = when (type) {
            "int" -> "0"
            "double" -> "0.0"
            "bool" -> "false"
            "String" -> "''"
            else -> "null"
        }
    }
}

// ========== Serialization Strategies ==========

private sealed interface SerializationStrategy {
    fun appendMethods(builder: StringBuilder, indent: String, className: String, properties: List<Property>)
}

/** Freezed-only: build_runner generates all methods via .freezed.dart, nothing to emit manually. */
private object FreezedOnlyStrategy : SerializationStrategy {
    override fun appendMethods(builder: StringBuilder, indent: String, className: String, properties: List<Property>) {}
}

/**
 * Used for json_serializable-only and freezed+json_serializable.
 * [includeToJson] = false when combined with freezed (freezed generates toJson internally).
 */
private class DelegateSerializationStrategy(private val includeToJson: Boolean) : SerializationStrategy {
    override fun appendMethods(builder: StringBuilder, indent: String, className: String, properties: List<Property>) {
        builder.append("factory $className.fromJson(Map<String, dynamic> json) => _$" + "${className}FromJson(json);\n")
        if (includeToJson) {
            builder.append("${indent}Map<String, dynamic> toJson() => _$" + "${className}ToJson(this);\n")
        }
    }
}

private object ManualSerializationStrategy : SerializationStrategy {
    override fun appendMethods(builder: StringBuilder, indent: String, className: String, properties: List<Property>) {
        appendFromJson(builder, indent, className, properties)
        appendToJson(builder, indent, properties)
    }

    private fun appendFromJson(builder: StringBuilder, indent: String, className: String, properties: List<Property>) {
        builder.append("factory $className.fromJson(Map<String, dynamic> json) {\n")
        builder.append("$indent${indent}return $className(\n")
        properties.forEach { property ->
            val jsonKey = "json['${property.originName}']"
            builder.append("$indent$indent$indent${property.name}: ${fromJsonValue(property, jsonKey)},\n")
        }
        builder.append("$indent$indent);\n")
        builder.append("$indent}\n")
    }

    fun fromJsonValue(property: Property, jsonKey: String, classPrefix: String= ""): String {
        val isNullable = DartConfigManager.isPropertyNullable
        return when {
            property.isListType() && property.getGenericType().isPrimitiveType() -> {
                val conv = "List<${property.getGenericType()}>.from($jsonKey)"
                if (isNullable) "$jsonKey != null ? $conv : null" else "$jsonKey != null ? $conv : []"
            }
            property.isListType() -> {
                val conv = "($jsonKey as List).map((i) => ${property.getGenericType()}.fromJson(i)).toList()"
                if (isNullable) "$jsonKey != null ? $conv : null" else "$jsonKey != null ? $conv : []"
            }
            property.isPrimitiveType() -> {
                if (isNullable) jsonKey else "$jsonKey ?? ${DartClass.getDefaultValue(property.type)}"
            }
            else -> {
                val conv = "${property.type}$classPrefix.fromJson($jsonKey)"
                if (isNullable) "$jsonKey != null ? $conv : null" else "${property.type}$classPrefix.fromJson($jsonKey ?? {})"
            }
        }
    }

    private fun appendToJson(builder: StringBuilder, indent: String, properties: List<Property>) {
        builder.append("\n${indent}Map<String, dynamic> toJson() {\n")
        builder.append("$indent${indent}final data = <String, dynamic>{};\n")
        properties.forEach { appendPropertySerialization(builder, indent, it) }
        builder.append("$indent${indent}return data;\n")
        builder.append("$indent}\n")
    }

    private fun appendPropertySerialization(builder: StringBuilder, indent: String, property: Property) {
        val key = "data['${property.originName}']"
        val isNullable = DartConfigManager.isPropertyNullable
        val serialization = when {
            property.isPrimitiveType() -> property.name
            property.isListType() && property.getGenericType().isPrimitiveType() -> property.name
            property.isListType() -> "${property.name}.map((v) => v.toJson()).toList()"
            else -> "${property.name}.toJson()"
        }
        if (isNullable) {
            builder.appendNullableAssignment(indent, key, serialization)
        } else {
            builder.append("$indent$indent$key = $serialization;\n")
        }
    }

    private fun StringBuilder.appendNullableAssignment(indent: String, key: String, value: String) {
        val propertyName = value.substringBefore(".").substringBefore("(")
        append("$indent$indent${indent}if ($propertyName != null) {\n")
        append("$indent$indent$indent$key = $value;\n")
        append("$indent$indent}\n")
    }
}
