package com.godwin.jsonparser.generator.jsontodart.specs.clazz

import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.test.TestConfig
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Edge cases and boundary condition tests for DartClass
 */
class DartClassEdgeCasesTest {

    @Before
    fun setUp() {
        resetConfig()
    }

    @After
    fun tearDown() {
        resetConfig()
    }

    private fun resetConfig() {
        TestConfig.isTestModel = true
        DartConfigManager.isPropertyNullable = false
        DartConfigManager.isFreezedAnnotation = false
        DartConfigManager.isJsonSerializationAnnotation = false
        DartConfigManager.isPropertyOptional = false
        DartConfigManager.isPropertyFinal = false
        DartConfigManager.isDartModelClassName = false
        DartConfigManager.isDartModelClassName = false
    }

    // ========== Empty/Null Edge Cases ==========

    @Test
    fun `test class with empty name`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "",
            properties = emptyList()
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("class  {"))
    }

    @Test
    fun `test class with whitespace name`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "   ",
            properties = emptyList()
        )

        val code = dartClass.getCode()
        assertTrue(code.contains("class  {"))
    }

    @Test
    fun `test property with empty origin name`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "field",
            type = "String",
            value = "",
            comment = "",
            isLast = false,
            originName = ""
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Test",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("json['']"))
        assertTrue(code.contains("data['']"))
    }

    @Test
    fun `test annotation with empty string`() {
        val annotation = Annotation("", "")
        val dartClass = DartClass(
            annotations = listOf(annotation),
            name = "Test",
            properties = emptyList()
        )

        val code = dartClass.getCode()

        // Empty annotation should not add extra lines
        assertFalse(code.startsWith("\n\nclass"))
    }

    // ========== Special Characters Edge Cases ==========

    @Test
    fun `test property name with special characters`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "user_name",
            type = "String",
            value = "",
            comment = "",
            isLast = false,
            originName = "user-name"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Test",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("String user_name;"))
        assertTrue(code.contains("json['user-name']"))
        assertTrue(code.contains("data['user-name']"))
    }

    @Test
    fun `test property name with dollar sign`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "\$price",
            type = "double",
            value = "",
            comment = "",
            isLast = false,
            originName = "price"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Product",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("double \$price;"))
    }

    @Test
    fun `test class name with numbers`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User123",
            properties = emptyList()
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("class User123"))
    }

    // ========== Complex Type Edge Cases ==========

    @Test
    fun `test nested generic types`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "data",
            type = "Map<String, List<int>>",
            value = "",
            comment = "",
            isLast = false,
            originName = "data"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Complex",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("Map<String, List<int>> data;"))
    }

    @Test
    fun `test nullable generic type`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "items",
            type = "List<String>?",
            value = "",
            comment = "",
            isLast = false,
            originName = "items"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Container",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("List<String>? items;"))
    }

    // ========== Large Scale Edge Cases ==========

    @Test
    fun `test class with many properties`() {
        val properties = (1..50).map { i ->
            Property(
                annotations = emptyList(),
                keyword = "",
                name = "field$i",
                type = "String",
                value = "",
                comment = "",
                isLast = false,
                originName = "field$i"
            )
        }
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "LargeClass",
            properties = properties
        )

        val code = dartClass.getCode()

        // Verify all properties are present
        properties.forEach { property ->
            assertTrue(code.contains("String ${property.name};"))
        }
    }

    @Test
    fun `test class with many annotations`() {
        val annotations = (1..10).map { i ->
            Annotation("@annotation$i", "")
        }
        val dartClass = DartClass(
            annotations = annotations,
            name = "AnnotatedClass",
            properties = emptyList()
        )

        val code = dartClass.getCode()

        annotations.forEach { annotation ->
            assertTrue(code.contains(annotation.annotationTemplate))
        }
    }

    // ========== Mixed Configuration Edge Cases ==========

    @Test
    fun `test all config flags enabled`() {
        DartConfigManager.isPropertyNullable = true
        DartConfigManager.isFreezedAnnotation = true
        DartConfigManager.isJsonSerializationAnnotation = true
        DartConfigManager.isPropertyOptional = true
        DartConfigManager.isPropertyFinal = true
        DartConfigManager.isDartModelClassName = true

        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "name",
            type = "String",
            value = "",
            comment = "",
            isLast = false,
            originName = "userName"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "user_data",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        // Should use Freezed + JsonSerializable
        assertTrue(code.contains("class UserData"))
        assertTrue(code.contains("const factory UserData"))
        assertTrue(code.contains("_\$UserDataFromJson"))
    }

    // ========== Indentation Edge Cases ==========

    @Test
    fun `test extreme extra indent`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "name",
            type = "String",
            value = "",
            comment = "",
            isLast = false,
            originName = "name"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Test",
            properties = listOf(property)
        )

        val code = dartClass.getCode(extraIndent = "        ")

        val lines = code.lines().filter { it.isNotBlank() }
        assertTrue(lines.all { it.startsWith("        ") })
    }

    @Test
    fun `test empty extra indent`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Test",
            properties = emptyList()
        )

        val code1 = dartClass.getCode()
        val code2 = dartClass.getCode(extraIndent = "")

        // Should be identical
        assertTrue(code1 == code2)
    }

    // ========== Property Value Edge Cases ==========

    @Test
    fun `test property with default value`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "count",
            type = "int",
            value = "0",
            comment = "",
            isLast = false,
            originName = "count"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Counter",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("int count = 0;"))
    }

    @Test
    fun `test property with complex default value`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "items",
            type = "List<String>",
            value = "const []",
            comment = "",
            isLast = false,
            originName = "items"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Container",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("List<String> items = const [];"))
    }

    // ========== Comment Edge Cases ==========

    @Test
    fun `test property with multiline comment`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "description",
            type = "String",
            value = "",
            comment = "Line 1\nLine 2\nLine 3",
            isLast = false,
            originName = "description"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Item",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        // Comment should be included
        assertTrue(code.contains("//"))
    }

    @Test
    fun `test property with special characters in comment`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "field",
            type = "String",
            value = "",
            comment = "Comment with 'quotes' and \"double quotes\"",
            isLast = false,
            originName = "field"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Test",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("//"))
    }

    // ========== Keyword Edge Cases ==========

    @Test
    fun `test property with final keyword`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "final",
            name = "id",
            type = "String",
            value = "",
            comment = "",
            isLast = false,
            originName = "id"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Entity",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("final String id;"))
    }

    @Test
    fun `test property with late keyword`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "late",
            name = "data",
            type = "String",
            value = "",
            comment = "",
            isLast = false,
            originName = "data"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Lazy",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("late String data;"))
    }

    // ========== Type Checking Edge Cases ==========

    @Test
    fun `test dynamic type`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "value",
            type = "dynamic",
            value = "",
            comment = "",
            isLast = false,
            originName = "value"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Flexible",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("dynamic value;"))
        assertTrue(code.contains("value: json['value']"))
    }

    @Test
    fun `test Object type`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "data",
            type = "Object",
            value = "",
            comment = "",
            isLast = false,
            originName = "data"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Container",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("Object data;"))
    }

    // ========== Serialization Edge Cases ==========

    @Test
    fun `test fromJson with all null values`() {
        DartConfigManager.isPropertyNullable = true
        val properties = listOf(
            createProperty("name", "String", "name"),
            createProperty("age", "int", "age"),
            createProperty("tags", "List<String>", "tags")
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = properties
        )

        val code = dartClass.getCode()

        // All should handle null
        assertTrue(code.contains("name: json['name']"))
        assertTrue(code.contains("age: json['age']"))
        assertTrue(code.contains("tags: json['tags'] != null"))
    }

    @Test
    fun `test toJson with all null values`() {
        DartConfigManager.isPropertyNullable = true
        val properties = listOf(
            createProperty("name", "String", "name"),
            createProperty("address", "Address", "address"),
            createProperty("tags", "List<String>", "tags")
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = properties
        )

        val code = dartClass.getCode()

        // Non-primitives should have null checks
        assertTrue(code.contains("if (name != null) {"))
        assertTrue(code.contains("if (address != null) {"))
        assertTrue(code.contains("if (tags != null) {"))
    }

    // ========== Helper Methods ==========

    private fun createProperty(
        name: String,
        type: String,
        originName: String
    ): Property {
        return Property(
            annotations = emptyList(),
            keyword = "",
            name = name,
            type = type,
            value = "",
            comment = "",
            isLast = false,
            originName = originName
        )
    }
}
