package com.godwin.jsonparser.util.repair

import com.godwin.jsonparser.util.repair.strategy.JsonAutoRepairStrategy
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class JsonRepairEngineTest : BasePlatformTestCase() {

    fun testSingleQuotesToDoubleQuotes() {
        val input = "{'name': 'John', 'age': 30}"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertTrue(result!!.contains("\"name\""))
        assertTrue(result.contains("\"John\""))
    }

    fun testUnquotedKeys() {
        val input = "{name: \"John\", age: 30}"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertTrue(result!!.contains("\"name\""))
        assertTrue(result.contains("\"age\""))
    }


    fun testTrailingComma() {
        val input = "{\"name\": \"John\", \"age\": 30,}"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertFalse(result!!.contains(",}"))
    }


    fun testBooleanCase() {
        val input = "{\"active\": True, \"verified\": False}"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertTrue(result!!.contains("true"))
        assertTrue(result.contains("false"))
    }


    fun testNullCase() {
        val input = "{\"value\": NULL}"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertTrue(result!!.contains("null"))
    }


    fun testSingleLineComments() {
        val input = """{
            "name": "John", // This is a comment
            "age": 30
        }"""
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertFalse(result!!.contains("//"))
    }


    fun testMultiLineComments() {
        val input = """{
            "name": "John", /* This is a 
            multi-line comment */
            "age": 30
        }"""
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertFalse(result!!.contains("/*"))
    }


    fun testMixedIssues() {
        val input = "{name: 'John', age: 30, active: True,}"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertTrue(result!!.contains("\"name\""))
        assertTrue(result.contains("\"John\""))
        assertTrue(result.contains("true"))
        assertFalse(result.contains(",}"))
    }


    fun testNestedObjects() {
        val input = "{name: 'John', address: {city: 'NYC', zip: 10001}}"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertTrue(result!!.contains("\"name\""))
        assertTrue(result.contains("\"address\""))
        assertTrue(result.contains("\"city\""))
    }


    fun testArrays() {
        val input = "{items: ['apple', 'banana', 'orange']}"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertTrue(result!!.contains("\"items\""))
        assertTrue(result.contains("\"apple\""))
    }


    fun testValidJson() {
        val input = "{\"name\": \"John\", \"age\": 30}"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertTrue(result!!.contains("\"name\""))
        assertTrue(result.contains("\"age\""))
    }


    fun testEmptyObject() {
        val input = "{}"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertEquals("{}", result!!.trim())
    }


    fun testEmptyArray() {
        val input = "[]"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertEquals("[]", result!!.trim())
    }


    fun testPythonNone() {
        val input = "{\"value\": None}"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertTrue(result!!.contains("null"))
    }


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
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertTrue(result!!.contains("\"name\""))
        assertTrue(result.contains("\"employees\""))
        assertTrue(result.contains("\"metadata\""))
        assertTrue(result.contains("true"))
        assertTrue(result.contains("false"))
    }

    fun testSimpleJsonWithoutDoubleQuotes() {
        val input = "{\"name\":\"god}"
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertTrue(result!!.contains("\"name\""))
        assertTrue(result.contains("\"god}\""))
    }

    fun testSimpleJsonWithoutDoubleQuotesNewLine() {
        val input = """{
            "name":"god
            }
        """.trimIndent()
        val result = JsonRepairEngine.repair(input, strategies = listOf(JsonAutoRepairStrategy())).join()
        assertNotNull(result)
        assertTrue(result!!.contains("\"name\""))
        assertTrue(result.contains("\"god\""))
    }
}
