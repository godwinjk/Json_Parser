package com.godwin.jsonparser.util.repair

import com.fasterxml.jackson.core.JsonParseException
import com.godwin.jsonparser.util.JsonUtils
import com.godwin.jsonparser.util.repair.analyzer.JsonState
import com.godwin.jsonparser.util.repair.analyzer.StructuralAnalyzer
import com.intellij.openapi.project.Project

class JacksonRepairStrategy : RepairStrategy() {

    private companion object {
        const val MAX_ITERATIONS = 5
    }

    override fun repair(project: Project, input: String): String {
        var json = input
        var attempts = 0

        while (attempts < MAX_ITERATIONS) {
            try {
                return JsonUtils.formatJson(json)
            } catch (e: JsonParseException) {
                val location = e.location
                val offset = location.charOffset.toInt()

                val analyzer = StructuralAnalyzer(json)
                val state = analyzer.scanUntil(offset)
                json = fixByError(json, offset, state)
            }
            attempts++
        }
        return json
    }

    private fun fixByError(
        json: String, errorOffset: Int,
        state: JsonState
    ): String {
        // Missing closing quote (professional version)
        if (state.insideString && errorOffset >= json.length - 1) {
            val structuralIndex = findLastStructuralBeforeEOF(json)

            if (structuralIndex != -1) {
                return insertAt(json, structuralIndex, "\"")
            }

            return json + "\""
        }

        //  Missing colon between key and value
        if (state.lastSignificantChar == '"') {
            val next = findNextNonWhitespace(json, errorOffset)
            if (next == '"') {
                return insertAt(json, errorOffset, ":")
            }
        }

        // Trailing comma
        val prev = findPreviousNonWhitespace(json, errorOffset)
        val next = findNextNonWhitespace(json, errorOffset)

        if (prev == ',' && (next == '}' || next == ']')) {
            return removePreviousComma(json, errorOffset)
        }

        // Missing closing brackets at EOF
        if (errorOffset >= json.length - 1 && state.bracketStack.isNotEmpty()) {
            val closing = state.bracketStack
                .asReversed()
                .map { if (it == '{') '}' else ']' }
                .joinToString("")
            return json + closing
        }

        return json
    }

    // ---------- Helpers ----------

    private fun insertAt(str: String, index: Int, value: String): String =
        str.substring(0, index) + value + str.substring(index)

    private fun removePreviousComma(str: String, index: Int): String {
        val i = str.lastIndexOf(',', index)
        return if (i >= 0) str.removeRange(i, i + 1) else str
    }

    private fun findNextNonWhitespace(str: String, start: Int): Char? =
        str.drop(start).firstOrNull { !it.isWhitespace() }

    private fun findPreviousNonWhitespace(str: String, start: Int): Char? =
        str.take(start).lastOrNull { !it.isWhitespace() }

    private fun findLastStructuralBeforeEOF(str: String): Int {
        for (i in str.length - 1 downTo 0) {
            val c = str[i]
            if (c == '}' || c == ']') {
                return i
            }
        }
        return -1
    }

}
