package com.godwin.jsonparser.generator.jsontodart.specs.clazz

import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.test.TestConfig
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DartClassTest {

    @Before
    fun setUp() {
        // Reset config to defaults before each test
        TestConfig.isTestModel = true
        DartConfigManager.isPropertyNullable = false
        DartConfigManager.isFreezedAnnotation = false
        DartConfigManager.isJsonSerializationAnnotation = false
        DartConfigManager.isPropertyOptional = false
        DartConfigManager.isPropertyFinal = false
        DartConfigManager.isDartModelClassName = false
    }

    @After
    fun tearDown() {
        // Reset config after each test
        DartConfigManager.isPropertyNullable = false
        DartConfigManager.isFreezedAnnotation = false
        DartConfigManager.isJsonSerializationAnnotation = false
        DartConfigManager.isPropertyOptional = false
        DartConfigManager.isPropertyFinal = false
        DartConfigManager.isDartModelClassName = false
    }

    // ========== Basic Class Generation Tests ==========

    @Test
    fun `test simple class with no properties`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "EmptyClass",
            properties = emptyList()
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("class EmptyClass {"))
        assertTrue(code.contains("EmptyClass();"))
        assertTrue(code.contains("factory EmptyClass.fromJson"))
        assertTrue(code.contains("Map<String, dynamic> toJson()"))
    }

    @Test
    fun `test class with single primitive property`() {
        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("String name;"))
        assertTrue(code.contains("User({required this.name});"))
        assertTrue(code.contains("name: json['userName']"))
        assertTrue(code.contains("data['userName'] = name;"))
    }

    @Test
    fun `test class with multiple properties`() {
        val properties = listOf(
            createProperty("name", "String", "userName"),
            createProperty("age", "int", "userAge"),
            createProperty("email", "String", "userEmail")
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = properties
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("String name;"))
        assertTrue(code.contains("int age;"))
        assertTrue(code.contains("String email;"))
        assertTrue(code.contains("required this.name, required this.age, required this.email"))
    }

    // ========== Annotation Tests ==========

    @Test
    fun `test class with annotations`() {
        val annotation = Annotation("@freezed", "")
        val dartClass = DartClass(
            annotations = listOf(annotation),
            name = "User",
            properties = emptyList()
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("@freezed"))
        assertTrue(code.contains("class User"))
    }

    @Test
    fun `test class with multiple annotations`() {
        val annotations = listOf(
            Annotation("@freezed", ""),
            Annotation("@JsonSerializable()", "")
        )
        val dartClass = DartClass(
            annotations = annotations,
            name = "User",
            properties = emptyList()
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("@freezed"))
        assertTrue(code.contains("@JsonSerializable()"))
    }

    // ========== Inheritance Tests ==========

    @Test
    fun `test class with parent class`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Admin",
            properties = emptyList(),
            parentClassTemplate = "User"
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("class Admin extends User"))
    }

    @Test
    fun `test class with mixin`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = emptyList(),
            mixinClass = "Serializable"
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("class User with Serializable"))
    }

    @Test
    fun `test class with both parent and mixin`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Admin",
            properties = emptyList(),
            parentClassTemplate = "User",
            mixinClass = "Serializable"
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("class Admin extends User with Serializable"))
    }

    // ========== Constructor Tests ==========

    @Test
    fun `test constructor with optional parameters`() {
        DartConfigManager.isPropertyOptional = true
        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("User({this.name});"))
        assertFalse(code.contains("required this.name"))
    }

    @Test
    fun `test constructor with required parameters`() {
        DartConfigManager.isPropertyFinal = true
        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("required this.name"))
    }

    @Test
    fun `test empty constructor when no properties`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Empty",
            properties = emptyList()
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("Empty();"))
        assertFalse(code.contains("Empty({});"))
    }

    // ========== Freezed Tests ==========

    @Test
    fun `test freezed class generation`() {
        DartConfigManager.isFreezedAnnotation = true
        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("const factory User"))
        assertTrue(code.contains("= _User;"))
        assertFalse(code.contains("String name;")) // No property declarations in Freezed
    }

    @Test
    fun `test freezed with empty properties`() {
        DartConfigManager.isFreezedAnnotation = true
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Empty",
            properties = emptyList()
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("const factory Empty() = _Empty;"))
    }

    @Test
    fun `test freezed with json_serializable`() {
        DartConfigManager.isFreezedAnnotation = true
        DartConfigManager.isJsonSerializationAnnotation = true
        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("factory User.fromJson(Map<String, dynamic> json) => _\$UserFromJson(json);"))
        assertFalse(code.contains("Map<String, dynamic> toJson()")) // toJson handled by Freezed
    }

    @Test
    fun `test freezed only without json_serializable`() {
        DartConfigManager.isFreezedAnnotation = true
        DartConfigManager.isJsonSerializationAnnotation = false
        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("fromJson"))
        assertTrue(code.contains("toJson"))
    }

    // ========== JsonSerializable Tests ==========

    @Test
    fun `test json_serializable only`() {
        DartConfigManager.isJsonSerializationAnnotation = true
        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("factory User.fromJson(Map<String, dynamic> json) => _\$UserFromJson(json);"))
        assertTrue(code.contains("Map<String, dynamic> toJson() => _\$UserToJson(this);"))
    }

    // ========== Manual Serialization Tests ==========

    @Test
    fun `test manual fromJson with primitive types`() {
        val properties = listOf(
            createProperty("name", "String", "userName"),
            createProperty("age", "int", "userAge"),
            createProperty("active", "bool", "isActive")
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = properties
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("name: json['userName'] ?? '',"))
        assertTrue(code.contains("age: json['userAge'] ?? 0,"))
        assertTrue(code.contains("active: json['isActive'] ?? false,"))
    }

    @Test
    fun `test manual fromJson with nullable primitives`() {
        DartConfigManager.isPropertyNullable = true
        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("name: json['userName'],"))
        assertFalse(code.contains("??"))
    }

    @Test
    fun `test manual fromJson with list of primitives`() {
        val property = createProperty("tags", "List<String>", "userTags")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("tags: json['userTags'] != null ? List<String>.from(json['userTags']) : [],"))
    }

    @Test
    fun `test manual fromJson with nullable list of primitives`() {
        DartConfigManager.isPropertyNullable = true
        val property = createProperty("tags", "List<String>", "userTags")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("tags: json['userTags'] != null ? List<String>.from(json['userTags']) : null,"))
    }

    @Test
    fun `test manual fromJson with list of objects`() {
        val property = createProperty("addresses", "List<Address>", "userAddresses")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("addresses: json['userAddresses'] != null ? (json['userAddresses'] as List).map((i) => Address.fromJson(i)).toList() : [],"))
    }

    @Test
    fun `test manual fromJson with nullable list of objects`() {
        DartConfigManager.isPropertyNullable = true
        val property = createProperty("addresses", "List<Address>", "userAddresses")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("addresses: json['userAddresses'] != null ? (json['userAddresses'] as List).map((i) => Address.fromJson(i)).toList() : null,"))
    }

    @Test
    fun `test manual fromJson with nested object`() {
        val property = createProperty("address", "Address", "userAddress")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("address: Address.fromJson(json['userAddress'] ?? {}),"))
    }

    @Test
    fun `test manual fromJson with nullable nested object`() {
        DartConfigManager.isPropertyNullable = true
        val property = createProperty("address", "Address", "userAddress")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("address: json['userAddress'] != null ? Address.fromJson(json['userAddress']) : null,"))
    }

    // ========== Manual toJson Tests ==========

    @Test
    fun `test manual toJson with primitive types`() {
        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("data['userName'] = name;"))
    }

    @Test
    fun `test manual toJson with nullable primitive`() {
        DartConfigManager.isPropertyNullable = true
        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("if (name != null) {"))
        assertTrue(code.contains("data['userName'] = name;"))
    }

    @Test
    fun `test manual toJson with list of primitives`() {
        val property = createProperty("tags", "List<String>", "userTags")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("data['userTags'] = tags;"))
    }

    @Test
    fun `test manual toJson with nullable list of primitives`() {
        DartConfigManager.isPropertyNullable = true
        val property = createProperty("tags", "List<String>", "userTags")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("if (tags != null) {"))
        assertTrue(code.contains("data['userTags'] = tags;"))
    }

    @Test
    fun `test manual toJson with list of objects`() {
        val property = createProperty("addresses", "List<Address>", "userAddresses")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("data['userAddresses'] = addresses.map((v) => v.toJson()).toList();"))
    }

    @Test
    fun `test manual toJson with nullable list of objects`() {
        DartConfigManager.isPropertyNullable = true
        val property = createProperty("addresses", "List<Address>", "userAddresses")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("if (addresses != null) {"))
        assertTrue(code.contains("data['userAddresses'] = addresses.map((v) => v.toJson()).toList();"))
    }

    @Test
    fun `test manual toJson with nested object`() {
        val property = createProperty("address", "Address", "userAddress")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("data['userAddress'] = address.toJson();"))
    }

    @Test
    fun `test manual toJson with nullable nested object`() {
        DartConfigManager.isPropertyNullable = true
        val property = createProperty("address", "Address", "userAddress")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("if (address != null) {"))
        assertTrue(code.contains("data['userAddress'] = address.toJson();"))
    }

    // ========== Naming Convention Tests ==========

    @Test
    fun `test pascal case class name`() {
        DartConfigManager.isDartModelClassName = true
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "user_profile",
            properties = emptyList()
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("class UserProfile"))
    }

    // ========== Extra Indent Tests ==========

    @Test
    fun `test extra indent applied to all lines`() {
        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode(extraIndent = "  ")

        val lines = code.lines().filter { it.isNotBlank() }
        assertTrue(lines.all { it.startsWith("  ") })
    }

    // ========== Nested Classes Tests ==========

    @Test
    fun `test nested classes`() {
        val nestedClass = DartClass(
            annotations = emptyList(),
            name = "Address",
            properties = listOf(createProperty("street", "String", "street"))
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(createProperty("name", "String", "userName")),
            nestedClasses = listOf(nestedClass)
        )

        assertEquals(1, dartClass.nestedClasses.size)
        assertEquals("Address", dartClass.nestedClasses[0].name)
    }

    // ========== Property Comment Tests ==========

    @Test
    fun `test property with comment`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "name",
            type = "String",
            value = "",
            comment = "User's full name",
            isLast = false,
            originName = "userName"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )

        val code = dartClass.getCode()

        assertTrue(code.contains("String name; // User's full name"))
    }

    // ========== Default Value Tests ==========

    @Test
    fun `test getDefaultValue for all primitive types`() {
        assertEquals("0", DartClass.getDefaultValue("int"))
        assertEquals("0.0", DartClass.getDefaultValue("double"))
        assertEquals("false", DartClass.getDefaultValue("bool"))
        assertEquals("''", DartClass.getDefaultValue("String"))
        assertEquals("null", DartClass.getDefaultValue("CustomType"))
    }

    // ========== Helper Methods ==========

    private fun createProperty(
        name: String,
        type: String,
        originName: String,
        comment: String = ""
    ): Property {
        return Property(
            annotations = emptyList(),
            keyword = "",
            name = name,
            type = type,
            value = "",
            comment = comment,
            isLast = false,
            originName = originName
        )
    }
}
