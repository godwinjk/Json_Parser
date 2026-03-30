package com.godwin.jsonparser.ui.dialog

import com.godwin.jsonparser.util.JsonUtils
import com.fasterxml.jackson.databind.JsonNode
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBLabel
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.*

class JsonDiffDialog(
    private val project: Project,
    private val leftJson: String
) : DialogWrapper(project) {

    private val leftEditor: Editor = createEditor(readOnly = true)
    private val rightEditor: Editor = createEditor(readOnly = false)
    private val diffPanel = JPanel(BorderLayout())
    private val statusLabel = JBLabel(" ").apply {
        border = BorderFactory.createEmptyBorder(4, 8, 4, 8)
        font = font.deriveFont(11f)
    }

    init {
        title = "JSON Diff"
        isModal = false
        setSize(900, 600)
        init()
        populateLeft()
        setupRightAutoFormat()
    }

    override fun createCenterPanel(): JComponent {
        val splitter = JBSplitter(false, 0.5f).apply {
            firstComponent = labeledPanel("Original (current tab)", leftEditor.component)
            secondComponent = labeledPanel("Compare (paste JSON here)", rightEditor.component)
            preferredSize = Dimension(900, 540)
        }

        return JPanel(BorderLayout()).apply {
            add(splitter, BorderLayout.CENTER)
            add(diffPanel, BorderLayout.SOUTH)
            add(statusLabel, BorderLayout.NORTH)
        }
    }

    private fun labeledPanel(title: String, content: JComponent) = JPanel(BorderLayout()).apply {
        add(JBLabel(title).apply {
            border = BorderFactory.createEmptyBorder(4, 6, 4, 6)
        }, BorderLayout.NORTH)
        add(content, BorderLayout.CENTER)
    }

    private fun populateLeft() {
        val formatted = try {
            if (leftJson.isBlank()) "" else JsonUtils.formatJson(leftJson)
        } catch (_: Exception) { leftJson }

        com.intellij.openapi.application.ApplicationManager.getApplication()
            .runWriteAction {
                leftEditor.document.apply {
                    setReadOnly(false)
                    setText(formatted)
                    setReadOnly(true)
                }
            }
    }

    private fun setupRightAutoFormat() {
        rightEditor.document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                val text = rightEditor.document.text
                if (text.isBlank()) { statusLabel.text = " "; return }
                // Auto-format after a short pause (debounce via invokeLater)
                SwingUtilities.invokeLater {
                    tryFormatRight(text)
                    computeDiff()
                }
            }
        })
    }

    private fun tryFormatRight(text: String) {
        if (!JsonUtils.isValidJson(text)) return
        val formatted = try { JsonUtils.formatJson(text) } catch (_: Exception) { return }
        if (formatted == text) return
        com.intellij.openapi.application.ApplicationManager.getApplication()
            .runWriteAction {
                rightEditor.document.setText(formatted)
            }
    }

    private fun computeDiff() {
        val left = leftEditor.document.text.trim()
        val right = rightEditor.document.text.trim()

        if (right.isBlank()) { statusLabel.text = " "; statusLabel.foreground = null; return }

        if (!JsonUtils.isValidJson(right)) {
            statusLabel.text = "  ⚠ Right side is not valid JSON"
            statusLabel.foreground = Color(200, 100, 0)
            return
        }

        val diffs = structuralDiff(left, right)
        if (diffs.isEmpty()) {
            statusLabel.text = "  ✓ JSON documents are identical"
            statusLabel.foreground = Color(0, 150, 0)
        } else {
            statusLabel.text = "  ${diffs.size} difference(s) found: ${diffs.take(3).joinToString(" | ")}"
            statusLabel.foreground = Color(180, 0, 0)
        }
    }

    private fun structuralDiff(left: String, right: String): List<String> {
        return try {
            val mapper = JsonUtils.CustomMapper()
            val leftNode = mapper.readTree(left)
            val rightNode = mapper.readTree(right)
            val diffs = mutableListOf<String>()
            diffNodes(leftNode, rightNode, "$", diffs)
            diffs
        } catch (_: Exception) {
            listOf("Parse error")
        }
    }

    private fun diffNodes(
        left: JsonNode,
        right: JsonNode,
        path: String,
        diffs: MutableList<String>
    ) {
        if (left == right) return
        when {
            left.isObject && right.isObject -> {
                val leftKeys = left.fieldNames().asSequence().toSet()
                val rightKeys = right.fieldNames().asSequence().toSet()
                (leftKeys - rightKeys).forEach { diffs.add("$path.$it: removed") }
                (rightKeys - leftKeys).forEach { diffs.add("$path.$it: added") }
                (leftKeys intersect rightKeys).forEach { key ->
                    diffNodes(left[key], right[key], "$path.$key", diffs)
                }
            }
            left.isArray && right.isArray -> {
                if (left.size() != right.size()) diffs.add("$path: array size ${left.size()} → ${right.size()}")
                val min = minOf(left.size(), right.size())
                for (i in 0 until min) diffNodes(left[i], right[i], "$path[$i]", diffs)
            }
            else -> diffs.add("$path: ${left.asText()} → ${right.asText()}")
        }
    }

    private fun createEditor(readOnly: Boolean): Editor {
        val factory = EditorFactory.getInstance()
        val doc = factory.createDocument("")
        val editor = factory.createEditor(doc, project)
        editor.settings.apply {
            isLineNumbersShown = true
            isLineMarkerAreaShown = false
            isFoldingOutlineShown = true
            isUseSoftWraps = true
        }
        if (readOnly) doc.setReadOnly(true)
        return editor
    }

    override fun createActions() = arrayOf(okAction)

    override fun dispose() {
        EditorFactory.getInstance().releaseEditor(leftEditor)
        EditorFactory.getInstance().releaseEditor(rightEditor)
        super.dispose()
    }
}
