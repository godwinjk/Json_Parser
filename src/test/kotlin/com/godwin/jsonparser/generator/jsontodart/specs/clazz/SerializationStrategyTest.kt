package com.godwin.jsonparser.generator.jsontodart.specs.clazz

import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.test.TestConfig
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for different serialization strategies
 */
class SerializationStrategyTest {

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
    }

    // ========== FreezedWithJsonSerializable Strategy Tests ==========

    @Test
    fun `test FreezedWithJsonSerializable generates only fromJson`() {
        DartConfigManager.isFreezedAnnotation = true
        DartConfigManager.isJsonSerializationAnnotation = true

        val dartClass = createSimpleClass()
        val code = dartClass.getCode()

        assertTrue(code.contains("factory User.fromJson(Map<String, dynamic> json) => _\$UserFromJson(json);"))
        assertFalse(code.contains("Map<String, dynamic> toJson()"))
    }

    @Test
    fun `test FreezedWithJsonSerializable with empty properties`() {
        DartConfigManager.isFreezedAnnotation = true
        DartConfigManager.isJsonSerializationAnnotation = true

        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Empty",
            properties = emptyList()
        )
        val code = dartClass.getCode()

        assertTrue(code.contains("factory Empty.fromJson(Map<String, dynamic> json) => _\$EmptyFromJson(json);"))
    }

    // ========== FreezedOnly Strategy Tests ==========

    @Test
    fun `test FreezedOnly generates no serialization methods`() {
        DartConfigManager.isFreezedAnnotation = true
        DartConfigManager.isJsonSerializationAnnotation = false

        val dartClass = createSimpleClass()
        val code = dartClass.getCode()

        assertTrue(code.contains("fromJson"))
        assertTrue(code.contains("toJson"))
    }

    @Test
    fun `test FreezedOnly with properties`() {
        DartConfigManager.isFreezedAnnotation = true

        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )
        val code = dartClass.getCode()

        assertTrue(code.contains("const factory User"))
        assertFalse(code.contains("String name;")) // No property declarations
        assertTrue(code.contains("fromJson"))
    }

    // ========== JsonSerializableOnly Strategy Tests ==========

    @Test
    fun `test JsonSerializableOnly generates both methods`() {
        DartConfigManager.isJsonSerializationAnnotation = true

        val dartClass = createSimpleClass()
        val code = dartClass.getCode()

        assertTrue(code.contains("factory User.fromJson(Map<String, dynamic> json) => _\$UserFromJson(json);"))
        assertTrue(code.contains("Map<String, dynamic> toJson() => _\$UserToJson(this);"))
    }

    @Test
    fun `test JsonSerializableOnly with multiple properties`() {
        DartConfigManager.isJsonSerializationAnnotation = true

        val properties = listOf(
            createProperty("name", "String", "userName"),
            createProperty("age", "int", "userAge")
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = properties
        )
        val code = dartClass.getCode()

        assertTrue(code.contains("_\$UserFromJson(json)"))
        assertTrue(code.contains("_\$UserToJson(this)"))
    }

    // ========== ManualSerialization Strategy Tests ==========

    @Test
    fun `test ManualSerialization with primitive types`() {
        val properties = listOf(
            createProperty("name", "String", "userName"),
            createProperty("age", "int", "userAge"),
            createProperty("active", "bool", "isActive"),
            createProperty("score", "double", "userScore")
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = properties
        )
        val code = dartClass.getCode()

        // fromJson with default values
        assertTrue(code.contains("name: json['userName'] ?? '',"))
        assertTrue(code.contains("age: json['userAge'] ?? 0,"))
        assertTrue(code.contains("active: json['isActive'] ?? false,"))
        assertTrue(code.contains("score: json['userScore'] ?? 0.0,"))

        // toJson
        assertTrue(code.contains("data['userName'] = name;"))
        assertTrue(code.contains("data['userAge'] = age;"))
        assertTrue(code.contains("data['isActive'] = active;"))
        assertTrue(code.contains("data['userScore'] = score;"))
    }

    @Test
    fun `test ManualSerialization with nullable primitives`() {
        DartConfigManager.isPropertyNullable = true

        val property = createProperty("name", "String", "userName")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )
        val code = dartClass.getCode()

        // fromJson without default value
        assertTrue(code.contains("name: json['userName'],"))
        assertFalse(code.contains("??"))

        // toJson with null check
        assertTrue(code.contains("if (name != null) {"))
        assertTrue(code.contains("data['userName'] = name;"))
    }

    @Test
    fun `test ManualSerialization with list of primitives`() {
        val property = createProperty("tags", "List<String>", "userTags")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )
        val code = dartClass.getCode()

        // fromJson
        assertTrue(code.contains("tags: json['userTags'] != null ? List<String>.from(json['userTags']) : [],"))

        // toJson
        assertTrue(code.contains("data['userTags'] = tags;"))
    }

    @Test
    fun `test ManualSerialization with nullable list of primitives`() {
        DartConfigManager.isPropertyNullable = true

        val property = createProperty("tags", "List<String>", "userTags")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )
        val code = dartClass.getCode()

        // fromJson
        assertTrue(code.contains("tags: json['userTags'] != null ? List<String>.from(json['userTags']) : null,"))

        // toJson with null check
        assertTrue(code.contains("if (tags != null) {"))
        assertTrue(code.contains("data['userTags'] = tags;"))
    }

    @Test
    fun `test ManualSerialization with list of objects`() {
        val property = createProperty("addresses", "List<Address>", "userAddresses")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )
        val code = dartClass.getCode()

        // fromJson
        assertTrue(code.contains("addresses: json['userAddresses'] != null ? (json['userAddresses'] as List).map((i) => Address.fromJson(i)).toList() : [],"))

        // toJson
        assertTrue(code.contains("data['userAddresses'] = addresses.map((v) => v.toJson()).toList();"))
    }

    @Test
    fun `test ManualSerialization with nullable list of objects`() {
        DartConfigManager.isPropertyNullable = true

        val property = createProperty("addresses", "List<Address>", "userAddresses")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )
        val code = dartClass.getCode()

        // fromJson
        assertTrue(code.contains("addresses: json['userAddresses'] != null ? (json['userAddresses'] as List).map((i) => Address.fromJson(i)).toList() : null,"))

        // toJson with null check
        assertTrue(code.contains("if (addresses != null) {"))
        assertTrue(code.contains("data['userAddresses'] = addresses.map((v) => v.toJson()).toList();"))
    }

    @Test
    fun `test ManualSerialization with nested object`() {
        val property = createProperty("address", "Address", "userAddress")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )
        val code = dartClass.getCode()

        // fromJson
        assertTrue(code.contains("address: Address.fromJson(json['userAddress'] ?? {}),"))

        // toJson
        assertTrue(code.contains("data['userAddress'] = address.toJson();"))
    }

    @Test
    fun `test ManualSerialization with nullable nested object`() {
        DartConfigManager.isPropertyNullable = true

        val property = createProperty("address", "Address", "userAddress")
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )
        val code = dartClass.getCode()

        // fromJson
        assertTrue(code.contains("address: json['userAddress'] != null ? Address.fromJson(json['userAddress']) : null,"))

        // toJson with null check
        assertTrue(code.contains("if (address != null) {"))
        assertTrue(code.contains("data['userAddress'] = address.toJson();"))
    }

    @Test
    fun `test ManualSerialization with mixed property types`() {
        val properties = listOf(
            createProperty("id", "String", "id"),
            createProperty("count", "int", "count"),
            createProperty("tags", "List<String>", "tags"),
            createProperty("metadata", "Map", "metadata"),
            createProperty("profile", "Profile", "profile")
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = properties
        )
        val code = dartClass.getCode()

        // All properties should be in fromJson
        assertTrue(code.contains("id: json['id']"))
        assertTrue(code.contains("count: json['count']"))
        assertTrue(code.contains("tags: json['tags']"))
        assertTrue(code.contains("metadata: json['metadata']"))
        assertTrue(code.contains("profile: Profile.fromJson"))

        // All properties should be in toJson
        assertTrue(code.contains("data['id'] = id;"))
        assertTrue(code.contains("data['count'] = count;"))
        assertTrue(code.contains("data['tags'] = tags"))
        assertTrue(code.contains("data['metadata'] = metadata"))
        assertTrue(code.contains("data['profile'] = profile.toJson();"))
    }

    @Test
    fun `test ManualSerialization with empty properties`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Empty",
            properties = emptyList()
        )
        val code = dartClass.getCode()

        assertTrue(code.contains("factory Empty.fromJson(Map<String, dynamic> json) {"))
        assertTrue(code.contains("return Empty("))
        assertTrue(code.contains(");"))
        assertTrue(code.contains("Map<String, dynamic> toJson() {"))
        assertTrue(code.contains("final data = <String, dynamic>{};"))
        assertTrue(code.contains("return data;"))
    }

    // ========== Strategy Selection Tests ==========

    @Test
    fun `test strategy selection with no flags`() {
        // Should use ManualSerialization
        val dartClass = createSimpleClass()
        val code = dartClass.getCode()

        assertTrue(code.contains("factory User.fromJson(Map<String, dynamic> json) {"))
        assertTrue(code.contains("return User("))
        assertTrue(code.contains("Map<String, dynamic> toJson() {"))
        assertTrue(code.contains("final data = <String, dynamic>{};"))
    }

    @Test
    fun `test strategy selection with only Freezed`() {
        DartConfigManager.isFreezedAnnotation = true

        val dartClass = createSimpleClass()
        val code = dartClass.getCode()

        assertTrue(code.contains("fromJson"))
        assertTrue(code.contains("toJson"))
    }

    @Test
    fun `test strategy selection with only JsonSerializable`() {
        DartConfigManager.isJsonSerializationAnnotation = true

        val dartClass = createSimpleClass()
        val code = dartClass.getCode()

        assertTrue(code.contains("_\$UserFromJson(json)"))
        assertTrue(code.contains("_\$UserToJson(this)"))
    }

    @Test
    fun `test strategy selection with both Freezed and JsonSerializable`() {
        DartConfigManager.isFreezedAnnotation = true
        DartConfigManager.isJsonSerializationAnnotation = true

        val dartClass = createSimpleClass()
        val code = dartClass.getCode()

        assertTrue(code.contains("_\$UserFromJson(json)"))
        assertFalse(code.contains("_\$UserToJson"))
    }

    // ========== Helper Methods ==========

    private fun createSimpleClass(): DartClass {
        val property = createProperty("name", "String", "userName")
        return DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property)
        )
    }

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
