package com.godwin.jsonparser.generator.jsontodart.classscodestruct

import com.godwin.jsonparser.generator.jsontodart.interceptor.IDartClassInterceptor
import com.godwin.jsonparser.generator.jsontodart.utils.BACKSTAGE_NULLABLE_POSTFIX
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ParsedDartDataClass
import com.godwin.jsonparser.generator.jsontodart.utils.getCommentCode
import com.godwin.jsonparser.generator.jsontodart.utils.getIndent
import com.godwin.jsonparser.generator.jsontodart.utils.isPrimitiveType
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager

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

        val code = buildString {
            if (annotations.isNotEmpty()) {
                val annotationsCode = annotations.joinToString("\n") { it.getAnnotationString() }
                if (annotationsCode.isNotBlank()) {
                    append(annotationsCode).append("\n")
                }
            }
            append("class ").append(name)
            if (parentClassTemplate.isNotBlank()) {
                append(" extends ")
                append(parentClassTemplate)
            }
            if (mixinClass.isNotBlank()) {
                append(" with ")
                append(mixinClass)
            }
            append(" {").append("\n")
            //This block is for class properties.
            //Here For freezed no properties are defining, all things are handled by freezed after build runner
            if (!DartConfigManager.isFreezedAnnotation) {
                properties.forEach { p ->
                    val code = p.getCode()
                    val addIndentCode = code.split("\n").joinToString("\n") { indent + it }
                    append(addIndentCode)
                    append(";")
                    if (p.comment.isNotBlank()) append(" // ").append(getCommentCode(p.comment))
                    append("\n")
                }
            }
            // This bloc is for constructor
            //Again for freezed we are not creating this constructor
            if (!DartConfigManager.isFreezedAnnotation) {
                append("\n").append(indent).append(name).append("({")
                properties.forEach { p ->
                    if (!DartConfigManager.isPropertyOptional || DartConfigManager.isPropertyFinal) {
                        append("required ")
                    }
                    append("this.").append(p.name)
                    if (p.isLast.not()) append(", ")
                }
                append("});").append("\n")
            } else if (DartConfigManager.isFreezedAnnotation) {
                //For freezed only
                append("\n").append(indent).append("factory ").append(name).append("({")
                properties.forEach { p ->
                    append("\n")
                    val code = p.getCodeForConstructor().split("\n").joinToString("\n") { indent + indent + it }
                    append(code)
                    if (p.isLast.not()) {
                        append(", ")
                    }
                }
                append("}) = ").append("_$name;").append("\n")
            }
            //from json
            if (DartConfigManager.isFreezedAnnotation) {
                append("\n")
                append(indent).append("factory ").append(name)
                    .append(".fromJson(Map<String, dynamic> json) => _$${name}FromJson(json);\n")
            } else if (DartConfigManager.isJsonSerializationAnnotation) {
                append(indent).append("factory ").append(name)
                    .append(".fromJson(Map<String, dynamic> json) => _$$name(json);\n")
                append(indent).append("Map<String, dynamic> toJson() =>  ").append("_$$name(json);\n")
            } else if (DartConfigManager.isJsonSerializationAnnotation && !DartConfigManager.isFreezedAnnotation) {
                append(indent).append("factory ").append(name)
                    .append(".fromJson(Map<String, dynamic> json) => _$$name(json);\n")

                append(indent).append("Map<String, dynamic> toJson() =>  ").append("_$$name(json);\n")

            } else {
                append(indent).append("factory ").append(name).append(".fromJson(Map<String, dynamic> json) {\n")
                append(indent).append(indent).append("return ").append(name).append("(").append("\n")

                properties.forEach { p ->
                    append(indent).append(indent).append(indent)
                    append(p.name).append(": ")

                    val valueGetter = "json['${p.name}']"

                    when {
                        p.isListType() && p.getGenericType().isPrimitiveType() -> {
                            append("$valueGetter != null ? new List<${p.getGenericType()}>.from($valueGetter) : null")
                        }

                        p.isListType() && !p.getGenericType().isPrimitiveType() -> {
                            append("$valueGetter != null ? ($valueGetter as List).map((i) => ${p.getGenericType()}.fromJson(i)).toList() : null")
                        }

                        p.isPrimitiveType() -> {
                            append(valueGetter)
                        }

                        else -> {
                            append("$valueGetter != null ? ${p.type}.fromJson($valueGetter) : null")
                        }
                    }
                    append(", ")
                    append("\n")
                }

                append(indent).append(indent).append(");").append("\n")
                append(indent).append("}").append("\n\n")
            }
            //toJson
            if (DartConfigManager.isJsonSerializationAnnotation && !DartConfigManager.isFreezedAnnotation) {
                append(indent).append("Map<String, dynamic> toJson() => _$${name}(this);")
                append("\n")
            } else if (!DartConfigManager.isFreezedAnnotation) {
                append(indent).append("Map<String, dynamic> toJson() {\n")
                append(indent).append(indent).append("final Map<String, dynamic> data = new Map<String, dynamic>();\n")

                properties.filter { it.isPrimitiveType() }.forEach { p ->
                    append(indent).append(indent)
                    val valueSetter = "data['${p.name}']"
                    append(valueSetter).append(" = ").append("this.${p.name}")
                    append(";")
                    append("\n")
                }

                properties.filter { !it.isPrimitiveType() }.forEach { p ->
                    append(indent).append(indent)
                    val valueSetter = "data['${p.name}']"

                    when {
                        p.isListType() && p.getGenericType().isPrimitiveType() -> {
                            append("if (${p.name} != null) {\n")
                            append(indent).append(indent).append(indent).append(valueSetter).append(" = ")
                                .append("${p.name};\n")
                            append(indent).append(indent).append("}\n")
                        }

                        p.isListType() && !p.getGenericType().isPrimitiveType() -> {
                            append("if (${p.name} != null) {\n")
                            append(indent).append(indent).append(indent).append(valueSetter).append(" = ")
                                .append("${p.name}.map((v) => v.toJson()).toList();\n")
                            append(indent).append(indent).append("}\n")
                        }

                        else -> {
                            append("if (${p.name} != null) {\n")
                            append(indent).append(indent).append(indent).append(valueSetter).append(" = ")
                                .append("${p.name}.toJson();\n")
                            append(indent).append(indent).append("}\n")
                        }
                    }
                }
                append(indent).append(indent).append("return data;\n")
                append(indent).append("}").append("\n")
            }
            append("}")
        }
        return if (extraIndent.isNotEmpty()) {
            code.split("\n").joinToString("\n") {
                if (it.isNotBlank()) {
                    extraIndent + it
                } else {
                    it
                }
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

    fun applyInterceptorWithNestedClasses(interceptor: IDartClassInterceptor): DartClass {
        if (nestedClasses.isNotEmpty()) {
            val newNestedClasses = nestedClasses.map { it.applyInterceptorWithNestedClasses(interceptor) }
            return interceptor.intercept(this).copy(nestedClasses = newNestedClasses)
        }
        return interceptor.intercept(this)
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
