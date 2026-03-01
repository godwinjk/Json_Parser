package com.godwin.jsonparser.util.repair.analyzer

data class JsonState(
    val insideString: Boolean,
    val stringStartIndex: Int?,
    val bracketStack: List<Char>,
    val lastSignificantChar: Char?
)

class StructuralAnalyzer(private val json: String) {

    fun scanUntil(limit: Int): JsonState {

        var insideString = false
        var stringStart: Int? = null
        var escapeNext = false
        val stack = ArrayDeque<Char>()
        var lastSignificant: Char? = null

        for (i in 0 until minOf(limit, json.length)) {
            val c = json[i]

            if (escapeNext) {
                escapeNext = false
                continue
            }

            if (c == '\\' && insideString) {
                escapeNext = true
                continue
            }

            if (c == '"') {
                insideString = !insideString
                if (insideString) stringStart = i
                continue
            }

            if (!insideString) {
                when (c) {
                    '{', '[' -> stack.addLast(c)
                    '}' -> if (stack.lastOrNull() == '{') stack.removeLast()
                    ']' -> if (stack.lastOrNull() == '[') stack.removeLast()
                }

                if (!c.isWhitespace()) {
                    lastSignificant = c
                }
            }
            if (insideString) {
                if (c == '}' || c == ']') {
                    // Structural token encountered while still inside string.
                    // This likely means missing closing quote.
                    return JsonState(
                        insideString = true,
                        stringStartIndex = stringStart,
                        bracketStack = stack.toList(),
                        lastSignificantChar = lastSignificant
                    )
                }
            }
        }

        return JsonState(
            insideString = insideString,
            stringStartIndex = stringStart,
            bracketStack = stack.toList(),
            lastSignificantChar = lastSignificant
        )
    }
}