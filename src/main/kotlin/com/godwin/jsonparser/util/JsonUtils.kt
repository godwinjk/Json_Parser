package com.godwin.jsonparser.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.godwin.jsonparser.services.JsonPersistence

object JsonUtils {

    private val mapper = CustomMapper().apply {
        configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        configure(JsonParser.Feature.ALLOW_COMMENTS, true)
        configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true)
    }

    fun formatJson(jsonStr: String): String {
        val prefs = JsonPersistence.getInstance()
        val printer = CustomPrettyPrinter(prefs.indentSize)
        val node = mapper.readTree(jsonStr)
        val writeMapper = if (prefs.sortKeys) {
            mapper.copy().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
        } else mapper
        return writeMapper.writer(printer).writeValueAsString(node)
    }

    fun isValidJson(jsonStr: String): Boolean {
        return try {
            mapper.readTree(jsonStr)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun isValidJsonError(jsonStr: String): String? {
        return try {
            ObjectMapper().readTree(jsonStr)
            null
        } catch (e: Exception) {
            return if (e is JsonProcessingException) {
                e.originalMessage
            } else {
                e.message
            }
        }
    }


    fun getMap(jsonStr: String): Any {
        val node = mapper.readTree(jsonStr)

        return when {
            node.isObject -> mapper.convertValue(node, Map::class.java)
            node.isArray -> mapper.convertValue(node, List::class.java)
            else -> throw IllegalArgumentException("Unsupported JSON structure")
        }
    }

    fun minifyJson(jsonStr: String): String {
        val node = mapper.readTree(jsonStr)
        return mapper.writeValueAsString(node)
    }

    fun queryJsonPath(jsonStr: String, expression: String): String {
        val result = com.jayway.jsonpath.JsonPath.read<Any>(jsonStr, expression)
        return mapper.writer(CustomPrettyPrinter()).writeValueAsString(result)
    }

    fun queryJmesPath(jsonStr: String, expression: String): String {
        val runtime = io.burt.jmespath.jackson.JacksonRuntime()
        val expr = runtime.compile(expression)
        val node = mapper.readTree(jsonStr)
        val result = expr.search(node)
        return mapper.writer(CustomPrettyPrinter()).writeValueAsString(result)
    }

    fun toYaml(jsonStr: String): String {
        val node = mapper.readTree(jsonStr)
        return YAMLMapper().writeValueAsString(node)
    }

    fun toJsonSchema(jsonStr: String): String {
        val node = mapper.readTree(jsonStr)
        val schema = inferSchema(node, "root")
        return mapper.writer(CustomPrettyPrinter()).writeValueAsString(schema)
    }

    data class JsonStats(
        val keys: Int,
        val depth: Int,
        val objects: Int,
        val arrays: Int,
        val nulls: Int,
        val strings: Int,
        val numbers: Int,
        val booleans: Int,
        val sizeBytes: Int
    )

    fun computeStats(jsonStr: String): JsonStats {
        val node = mapper.readTree(jsonStr)
        var keys = 0; var maxDepth = 0; var objects = 0
        var arrays = 0; var nulls = 0; var strings = 0
        var numbers = 0; var booleans = 0

        fun walk(n: JsonNode, depth: Int) {
            if (depth > maxDepth) maxDepth = depth
            when {
                n.isObject -> { objects++; n.fields().forEach { (_, v) -> keys++; walk(v, depth + 1) } }
                n.isArray -> { arrays++; n.forEach { walk(it, depth + 1) } }
                n.isNull -> nulls++
                n.isTextual -> strings++
                n.isNumber -> numbers++
                n.isBoolean -> booleans++
            }
        }
        walk(node, 0)
        return JsonStats(keys, maxDepth, objects, arrays, nulls, strings, numbers, booleans, jsonStr.toByteArray().size)
    }

    private fun inferSchema(node: JsonNode, title: String): Map<String, Any> {
        return when {
            node.isObject -> {
                val props = mutableMapOf<String, Any>()
                node.fields().forEach { (k, v) -> props[k] = inferSchema(v, k) }
                mapOf("type" to "object", "title" to title, "properties" to props)
            }
            node.isArray -> {
                val items = if (node.size() > 0) inferSchema(node[0], "item") else mapOf("type" to "object")
                mapOf("type" to "array", "items" to items)
            }
            node.isTextual -> mapOf("type" to "string")
            node.isInt || node.isLong -> mapOf("type" to "integer")
            node.isNumber -> mapOf("type" to "number")
            node.isBoolean -> mapOf("type" to "boolean")
            node.isNull -> mapOf("type" to "null")
            else -> mapOf("type" to "string")
        }
    }

    fun cleanUpJsonString(jsonString: String): String {
        val trimmed = jsonString.trim()

        if (isValidJson(trimmed)) {
            return trimmed
        }

        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            val fixed = fixMissingBraces(trimmed)
            if (isValidJson(fixed)) {
                return fixed
            }
        }

        return trimmed
    }

    private fun fixMissingBraces(jsonString: String): String {
        var result = jsonString.trim()
        var openBracesCount = 0
        var closeBracesCount = 0
        var inString = false

        result.forEachIndexed { i, ch ->
            if (ch == '"') {
                val isEscaped = i > 0 && result[i - 1] == '\\'
                if (!isEscaped) {
                    inString = !inString
                }
            }

            if (!inString) {
                when (ch) {
                    '{' -> openBracesCount++
                    '}' -> closeBracesCount++
                }
            }
        }

        if (openBracesCount == closeBracesCount && !result.startsWith("{")) {
            result = "{$result}"
        }

        return result
    }

    private class CustomPrettyPrinter(indentSize: Int = 2) : DefaultPrettyPrinter() {
        init {
            _objectFieldValueSeparatorWithSpaces = ":"
            val indent = " ".repeat(indentSize)
            _objectIndenter = DefaultIndenter(indent, "\n")
            _arrayIndenter = DefaultIndenter(indent, "\n")
        }

        override fun createInstance() = CustomPrettyPrinter()
    }

    class CustomMapper : ObjectMapper() {
        init {
            configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
            configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }
}
