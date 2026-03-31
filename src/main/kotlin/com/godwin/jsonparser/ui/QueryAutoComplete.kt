package com.godwin.jsonparser.ui

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * Attaches autocomplete to a JTextField for JSONPath / JMESPath expressions.
 * Suggestions are derived from the actual JSON keys + syntax tokens.
 */
class QueryAutoComplete(
    private val field: JTextField,
    private val queryTypeProvider: () -> String
) {
    private val mapper = ObjectMapper()
    private val popup = JPopupMenu()
    private var allPaths: List<String> = emptyList()
    private var suppressListener = false

    private val jsonPathTokens = listOf("[*]", "[0]", "[?(@.)]", "..", "length()", "keys()", "min()", "max()", "avg()")
    private val jmesPathTokens = listOf("[*]", "[0]", "[?()]", "length()", "keys(@)", "values(@)", "sort(@)", "reverse(@)", "to_array(@)")

    init {
        field.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = onTextChanged()
            override fun removeUpdate(e: DocumentEvent) = onTextChanged()
            override fun changedUpdate(e: DocumentEvent) = onTextChanged()
        })

        field.addKeyListener(object : KeyListener {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_ESCAPE -> popup.isVisible = false
                    KeyEvent.VK_DOWN -> (popup.getComponent(0) as? JList<*>)?.requestFocusInWindow()
                    else -> {}
                }
            }
            override fun keyReleased(e: KeyEvent) {}
            override fun keyTyped(e: KeyEvent) {}
        })
    }

    fun updateJson(json: String) {
        allPaths = try {
            if (json.isBlank()) emptyList()
            else extractPaths(mapper.readTree(json), queryTypeProvider())
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun onTextChanged() {
        if (suppressListener) return
        val text = field.text
        if (text.isBlank()) { popup.isVisible = false; return }

        val isJsonPath = queryTypeProvider() == "JSONPath"
        val tokens = if (isJsonPath) jsonPathTokens else jmesPathTokens
        val paths = if (isJsonPath) allPaths else allPaths.map { it.removePrefix("$.") }

        showPopup(buildSuggestions(text, paths, tokens))
    }

    private fun buildSuggestions(text: String, paths: List<String>, tokens: List<String>): List<String> {
        val lower = text.lowercase()
        val matchingPaths = paths.filter { it.lowercase().startsWith(lower) && it != text }
        val matchingTokens = if (text.endsWith(".") || text.endsWith("[")) {
            tokens.map { text + it }
        } else {
            tokens.filter { it.lowercase().startsWith(lower) }.map { text + it }
        }
        return (matchingPaths + matchingTokens).distinct().take(12)
    }

    private fun showPopup(suggestions: List<String>) {
        popup.removeAll()
        if (suggestions.isEmpty()) { popup.isVisible = false; return }

        val list = JList(suggestions.toTypedArray())
        list.selectionMode = ListSelectionModel.SINGLE_SELECTION
        list.visibleRowCount = suggestions.size.coerceAtMost(8)

        list.addKeyListener(object : KeyListener {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_ENTER -> applySuggestion(list.selectedValue)
                    KeyEvent.VK_ESCAPE -> { popup.isVisible = false; field.requestFocusInWindow() }
                    else -> {}
                }
            }
            override fun keyReleased(e: KeyEvent) {}
            override fun keyTyped(e: KeyEvent) {}
        })

        list.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                if (e.clickCount >= 1) applySuggestion(list.selectedValue)
            }
        })

        popup.add(JScrollPane(list))
        popup.isFocusable = false
        popup.show(field, 0, field.height)
    }

    private fun applySuggestion(value: String?) {
        value ?: return
        suppressListener = true
        field.text = value
        suppressListener = false
        popup.isVisible = false
        field.requestFocusInWindow()
        field.caretPosition = field.text.length
    }

    private fun extractPaths(node: JsonNode, queryType: String): List<String> {
        val paths = mutableListOf<String>()
        val isJsonPath = queryType == "JSONPath"
        val dollarDot = "$" + "."

        fun walk(n: JsonNode, path: String) {
            when {
                n.isObject -> {
                    val iter = n.fields()
                    while (iter.hasNext()) {
                        val entry = iter.next()
                        val key = entry.key
                        val child = entry.value
                        val childPath = if (path.isEmpty()) key else "$path.$key"
                        paths.add(if (isJsonPath) dollarDot + childPath else childPath)
                        walk(child, childPath)
                    }
                }
                n.isArray -> {
                    val arrayPath = "$path[*]"
                    paths.add(if (isJsonPath) dollarDot + arrayPath else arrayPath)
                    if (n.size() > 0) walk(n[0], "$path[0]")
                }
                else -> {}
            }
        }

        walk(node, "")
        return paths.distinct()
    }
}
