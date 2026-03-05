package com.godwin.jsonparser.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

object JsonUtils {

    private val mapper = CustomMapper().apply {
        configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        configure(JsonParser.Feature.ALLOW_COMMENTS, true)
        configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true)
    }
    private val prettyPrinter = CustomPrettyPrinter()

    fun formatJson(jsonStr: String): String {
        val jsonObject = mapper.readValue(jsonStr, Any::class.java)
        return mapper.writer(prettyPrinter).writeValueAsString(jsonObject)
    }

    fun isValidJson(jsonStr: String): Boolean {
        return try {
            mapper.readTree(jsonStr)
            true
        } catch (_: Exception) {
            false
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

    private class CustomPrettyPrinter : DefaultPrettyPrinter() {
        init {
            _objectFieldValueSeparatorWithSpaces = ":"
            _objectIndenter = UNIX_LINE_FEED_INSTANCE
            _arrayIndenter = UNIX_LINE_FEED_INSTANCE
            _spacesInObjectEntries = true
        }

        override fun createInstance() = CustomPrettyPrinter()

        companion object {
            private val UNIX_LINE_FEED_INSTANCE = DefaultIndenter("  ", "\n")
        }
    }

    class CustomMapper : ObjectMapper() {
        init {
            configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
            configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }
}
