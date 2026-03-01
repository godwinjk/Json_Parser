package com.godwin.jsonparser.util.repair

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

class JsonRepairEngineTest : BasePlatformTestCase() {

    @Test
    fun testSingleQuotesToDoubleQuotes() {
        val input = "{'name': 'John', 'age': 30}"
        val result = JsonRepairEngine.repair(project, input)
        assertTrue(result.contains("\"name\""))
        assertTrue(result.contains("\"John\""))
    }

    @Test
    fun testUnquotedKeys() {
        val input = "{name: \"John\", age: 30}"
        val result = JsonRepairEngine.repair(project, input)
        assertTrue(result.contains("\"name\""))
        assertTrue(result.contains("\"age\""))
    }

    @Test
    fun testTrailingComma() {
        val input = "{\"name\": \"John\", \"age\": 30,}"
        val result = JsonRepairEngine.repair(project, input)
        assertFalse(result.contains(",}"))
    }

    @Test
    fun testBooleanCase() {
        val input = "{\"active\": True, \"verified\": False}"
        val result = JsonRepairEngine.repair(project, input)
        assertTrue(result.contains("true"))
        assertTrue(result.contains("false"))
    }

    @Test
    fun testNullCase() {
        val input = "{\"value\": NULL}"
        val result = JsonRepairEngine.repair(project, input)
        assertTrue(result.contains("null"))
    }

    @Test
    fun testSingleLineComments() {
        val input = """{
            "name": "John", // This is a comment
            "age": 30
        }"""
        val result = JsonRepairEngine.repair(project, input)
        assertFalse(result.contains("//"))
    }

    @Test
    fun testMultiLineComments() {
        val input = """{
            "name": "John", /* This is a 
            multi-line comment */
            "age": 30
        }"""
        val result = JsonRepairEngine.repair(project, input)
        assertFalse(result.contains("/*"))
    }

    @Test
    fun testMixedIssues() {
        val input = "{name: 'John', age: 30, active: True,}"
        val result = JsonRepairEngine.repair(project, input)
        assertTrue(result.contains("\"name\""))
        assertTrue(result.contains("\"John\""))
        assertTrue(result.contains("true"))
        assertFalse(result.contains(",}"))
    }

    @Test
    fun testNestedObjects() {
        val input = "{name: 'John', address: {city: 'NYC', zip: 10001}}"
        val result = JsonRepairEngine.repair(project, input)
        assertTrue(result.contains("\"name\""))
        assertTrue(result.contains("\"address\""))
        assertTrue(result.contains("\"city\""))
    }

    @Test
    fun testArrays() {
        val input = "{items: ['apple', 'banana', 'orange']}"
        val result = JsonRepairEngine.repair(project, input)
        assertTrue(result.contains("\"items\""))
        assertTrue(result.contains("\"apple\""))
    }

    @Test
    fun testValidJson() {
        val input = "{\"name\": \"John\", \"age\": 30}"
        val result = JsonRepairEngine.repair(project, input)
        assertTrue(result.contains("\"name\""))
        assertTrue(result.contains("\"age\""))
    }

    @Test
    fun testEmptyObject() {
        val input = "{}"
        val result = JsonRepairEngine.repair(project, input)
        assertEquals("{}", result.trim())
    }

    @Test
    fun testEmptyArray() {
        val input = "[]"
        val result = JsonRepairEngine.repair(project, input)
        assertEquals("[]", result.trim())
    }

    @Test
    fun testPythonNone() {
        val input = "{\"value\": None}"
        val result = JsonRepairEngine.repair(project, input)
        assertTrue(result.contains("null"))
    }

    @Test
    fun testComplexNestedStructure() {
        val input = """{
            name: 'Company',
            employees: [
                {name: 'John', active: True},
                {name: 'Jane', active: False,}
            ],
            metadata: {
                created: '2024-01-01',
                version: 1
            }
        }"""
        val result = JsonRepairEngine.repair(project, input)
        assertTrue(result.contains("\"name\""))
        assertTrue(result.contains("\"employees\""))
        assertTrue(result.contains("\"metadata\""))
        assertTrue(result.contains("true"))
        assertTrue(result.contains("false"))
    }

    @Test
    fun testSimpleJsonWithoutDoubleQuotes() {
        val input = "{\"name\":\"god}"
        val result = JsonRepairEngine.repair(project, input)
        assertTrue(result.contains("\"name\""))
        assertTrue(result.contains("\"god\""))
    }

    @Test
    fun testSimpleJsonWithoutDoubleQuotesNewLine() {
        val input = """{
            "name":"god
            }
        """.trimIndent()
        val result = JsonRepairEngine.repair(project, input)
        assertTrue(result.contains("\"name\""))
        assertTrue(result.contains("\"god\""))
    }
}
