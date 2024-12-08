package com.godwin.jsonparser.generator.jsontodart

import com.godwin.jsonparser.generator.jsontodart.bean.jsonschema.*
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.util.*

class JsonSchemaDataClassGenerator(private val jsonObjectDef: ObjectPropertyDef) {
    val classes = mutableListOf<TypeSpec>()

    fun generate(className: String?) {
        val clazz = className
            ?: throw IllegalArgumentException("className cannot be null when jsonObjectDef.title is null")

        val requiredFields = jsonObjectDef.required // don't remove `?: emptyArray()`
        val properties = jsonObjectDef.properties
        val s = TypeSpec.classBuilder(clazz).apply {
            if (!DartConfigManager.isCommentOff && (jsonObjectDef.description?.isNotBlank() == true)) {
                addKdoc(jsonObjectDef.description)
            }
            addModifiers(KModifier.DATA)
            primaryConstructor(FunSpec.constructorBuilder().apply {
                properties.forEach { property, propertyDefinition ->
                    val typeName = resolveType(property, requiredFields, propertyDefinition)
                    addParameter(property, typeName)
                    if (propertyDefinition::class === ObjectPropertyDef::class) {
                        val jsonSchemaDataClassGenerator =
                            JsonSchemaDataClassGenerator(propertyDefinition as ObjectPropertyDef)
                        jsonSchemaDataClassGenerator.generate((typeName as? ClassName)?.simpleName)
                        classes.addAll(jsonSchemaDataClassGenerator.classes)
                    }
                }
            }.build())
            properties.forEach { property, propertyDefinition ->
                val typeName = resolveType(property, requiredFields, propertyDefinition)
                val description = (propertyDefinition as? ObjectPropertyDef)?.description
                addProperty(
                    PropertySpec.builder(property, typeName).apply {
                        if (!DartConfigManager.isCommentOff && (description?.isNotBlank() == true)) {
                            addKdoc(description)
                        }
                        initializer(property)
                    }.build()
                )
            }
        }.build()
        classes.add(s)
    }

    private fun resolveType(
        property: String,
        requiredFields: Array<String>,
        propertyDefinition: PropertyDef
    ): TypeName {
        val nullable = property !in requiredFields
        return when (propertyDefinition) {
            is IntPropertyDef -> ClassName.bestGuess("Int").copy(nullable = nullable)
            is NumberPropertyDef -> ClassName.bestGuess("Double").copy(nullable = nullable)
            is BoolPropertyDef -> ClassName.bestGuess("Boolean").copy(nullable = nullable)
            is StringPropertyDef -> ClassName.bestGuess("String").copy(nullable = nullable)
            is EnumPropertyDef -> ClassName.bestGuess("String").copy(nullable = nullable)
            is ArrayPropertyDef -> {
                val arrayParameterType = resolveType("", arrayOf(""), propertyDefinition.items)
                ClassName.bestGuess("Array").parameterizedBy(arrayParameterType).copy(nullable = nullable)
            }

            is ObjectPropertyDef -> ClassName("",
                property.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }).copy(
                nullable = nullable
            )

            else -> String::class.asTypeName().copy(nullable = nullable)
        }
    }
}
