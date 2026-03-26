package com.godwin.jsonparser.generator.jsontodart.specs.clazz

import com.godwin.jsonparser.generator.jsontodart.interceptor.clazz.IDartClassInterceptor
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for DartClass interceptor functionality
 */
class DartClassInterceptorTest {

    @Test
    fun `test single interceptor applied`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = emptyList()
        )

        val interceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                return dartClass.copy(name = "ModifiedUser")
            }
        }

        val result = dartClass.applyInterceptors(listOf(interceptor))

        assertEquals("ModifiedUser", result.name)
    }

    @Test
    fun `test multiple interceptors applied in order`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = emptyList()
        )

        val interceptor1 = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                return dartClass.copy(name = dartClass.name + "1")
            }
        }

        val interceptor2 = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                return dartClass.copy(name = dartClass.name + "2")
            }
        }

        val result = dartClass.applyInterceptors(listOf(interceptor1, interceptor2))

        assertEquals("User12", result.name)
    }

    @Test
    fun `test interceptor with no changes`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = emptyList()
        )

        val interceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                return dartClass
            }
        }

        val result = dartClass.applyInterceptors(listOf(interceptor))

        assertEquals(dartClass, result)
    }

    @Test
    fun `test interceptor modifying properties`() {
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
            name = "User",
            properties = listOf(property)
        )

        val interceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                val newProperties = dartClass.properties.map { prop ->
                    prop.copy(type = "dynamic")
                }
                return dartClass.copy(properties = newProperties)
            }
        }

        val result = dartClass.applyInterceptors(listOf(interceptor))

        assertEquals("dynamic", result.properties[0].type)
    }

    @Test
    fun `test interceptor adding annotations`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = emptyList()
        )

        val interceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                val newAnnotations = listOf(Annotation("@freezed", ""))
                return dartClass.copy(annotations = newAnnotations)
            }
        }

        val result = dartClass.applyInterceptors(listOf(interceptor))

        assertEquals(1, result.annotations.size)
        assertEquals("@freezed", result.annotations[0].annotationTemplate)
    }

    @Test
    fun `test interceptor with nested classes`() {
        val nestedClass = DartClass(
            annotations = emptyList(),
            name = "Address",
            properties = emptyList()
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = emptyList(),
            nestedClasses = listOf(nestedClass)
        )

        val interceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                return dartClass.copy(name = dartClass.name + "Modified")
            }
        }

        val result = dartClass.applyInterceptors(listOf(interceptor))

        assertEquals("UserModified", result.name)
        assertEquals("AddressModified", result.nestedClasses[0].name)
    }

    @Test
    fun `test interceptor with deeply nested classes`() {
        val level3 = DartClass(
            annotations = emptyList(),
            name = "Level3",
            properties = emptyList()
        )
        val level2 = DartClass(
            annotations = emptyList(),
            name = "Level2",
            properties = emptyList(),
            nestedClasses = listOf(level3)
        )
        val level1 = DartClass(
            annotations = emptyList(),
            name = "Level1",
            properties = emptyList(),
            nestedClasses = listOf(level2)
        )

        val interceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                return dartClass.copy(name = dartClass.name + "X")
            }
        }

        val result = level1.applyInterceptors(listOf(interceptor))

        assertEquals("Level1X", result.name)
        assertEquals("Level2X", result.nestedClasses[0].name)
        assertEquals("Level3X", result.nestedClasses[0].nestedClasses[0].name)
    }

    @Test
    fun `test empty interceptor list`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = emptyList()
        )

        val result = dartClass.applyInterceptors(emptyList())

        assertEquals(dartClass, result)
    }

    @Test
    fun `test interceptor modifying parent class template`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Admin",
            properties = emptyList(),
            parentClassTemplate = "User"
        )

        val interceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                return dartClass.copy(parentClassTemplate = "SuperUser")
            }
        }

        val result = dartClass.applyInterceptors(listOf(interceptor))

        assertEquals("SuperUser", result.parentClassTemplate)
    }

    @Test
    fun `test interceptor modifying mixin class`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = emptyList(),
            mixinClass = "Serializable"
        )

        val interceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                return dartClass.copy(mixinClass = "JsonSerializable")
            }
        }

        val result = dartClass.applyInterceptors(listOf(interceptor))

        assertEquals("JsonSerializable", result.mixinClass)
    }

    @Test
    fun `test interceptor chain with property transformation`() {
        val property = Property(
            annotations = emptyList(),
            keyword = "",
            name = "value",
            type = "int",
            value = "",
            comment = "",
            isLast = false,
            originName = "value"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "Data",
            properties = listOf(property)
        )

        val addFinalInterceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                val newProperties = dartClass.properties.map { it.copy(keyword = "final") }
                return dartClass.copy(properties = newProperties)
            }
        }

        val changeTypeInterceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                val newProperties = dartClass.properties.map { it.copy(type = "String") }
                return dartClass.copy(properties = newProperties)
            }
        }

        val result = dartClass.applyInterceptors(listOf(addFinalInterceptor, changeTypeInterceptor))

        assertEquals("final", result.properties[0].keyword)
        assertEquals("String", result.properties[0].type)
    }

    @Test
    fun `test interceptor preserves immutability`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = emptyList()
        )

        val interceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                return dartClass.copy(name = "ModifiedUser")
            }
        }

        val result = dartClass.applyInterceptors(listOf(interceptor))

        // Original should be unchanged
        assertEquals("User", dartClass.name)
        assertEquals("ModifiedUser", result.name)
        assertNotSame(dartClass, result)
    }

    @Test
    fun `test interceptor with multiple nested classes at same level`() {
        val nested1 = DartClass(
            annotations = emptyList(),
            name = "Address",
            properties = emptyList()
        )
        val nested2 = DartClass(
            annotations = emptyList(),
            name = "Contact",
            properties = emptyList()
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = emptyList(),
            nestedClasses = listOf(nested1, nested2)
        )

        val interceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                return dartClass.copy(name = dartClass.name + "Mod")
            }
        }

        val result = dartClass.applyInterceptors(listOf(interceptor))

        assertEquals("UserMod", result.name)
        assertEquals(2, result.nestedClasses.size)
        assertEquals("AddressMod", result.nestedClasses[0].name)
        assertEquals("ContactMod", result.nestedClasses[1].name)
    }

    @Test
    fun `test interceptor adding properties`() {
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = emptyList()
        )

        val interceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                val newProperty = Property(
                    annotations = emptyList(),
                    keyword = "",
                    name = "id",
                    type = "String",
                    value = "",
                    comment = "",
                    isLast = false,
                    originName = "id"
                )
                return dartClass.copy(properties = dartClass.properties + newProperty)
            }
        }

        val result = dartClass.applyInterceptors(listOf(interceptor))

        assertEquals(1, result.properties.size)
        assertEquals("id", result.properties[0].name)
    }

    @Test
    fun `test interceptor removing properties`() {
        val property1 = Property(
            annotations = emptyList(),
            keyword = "",
            name = "name",
            type = "String",
            value = "",
            comment = "",
            isLast = false,
            originName = "name"
        )
        val property2 = Property(
            annotations = emptyList(),
            keyword = "",
            name = "age",
            type = "int",
            value = "",
            comment = "",
            isLast = false,
            originName = "age"
        )
        val dartClass = DartClass(
            annotations = emptyList(),
            name = "User",
            properties = listOf(property1, property2)
        )

        val interceptor = object : IDartClassInterceptor {
            override fun intercept(dartClass: DartClass): DartClass {
                val filteredProperties = dartClass.properties.filter { it.name != "age" }
                return dartClass.copy(properties = filteredProperties)
            }
        }

        val result = dartClass.applyInterceptors(listOf(interceptor))

        assertEquals(1, result.properties.size)
        assertEquals("name", result.properties[0].name)
    }
}
