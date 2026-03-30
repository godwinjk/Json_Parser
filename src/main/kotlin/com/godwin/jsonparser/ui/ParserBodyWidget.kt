package com.godwin.jsonparser.ui

import com.fasterxml.jackson.core.JsonProcessingException
import com.godwin.jsonparser.action.CopyToClipBoardAction
import com.godwin.jsonparser.action.JBRadioAction
import com.godwin.jsonparser.generator.common.ui.jBorderLayout
import com.godwin.jsonparser.util.EditorHintsNotifier
import com.godwin.jsonparser.util.JsonUtils
import com.godwin.jsonparser.util.Log
import com.godwin.jsonparser.util.TreeNodeCreator
import com.google.gson.JsonSyntaxException
import com.intellij.icons.AllIcons
import com.intellij.ide.highlighter.HtmlFileHighlighter
import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.ide.highlighter.XmlFileHighlighter
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.json.JsonFileType
import com.intellij.json.highlighting.JsonSyntaxHighlighterFactory
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.util.LayerDescriptor
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter
import com.intellij.openapi.fileTypes.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.psi.tree.IElementType
import com.intellij.ui.InplaceButton
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import java.awt.BorderLayout
import java.awt.CardLayout
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel

class ParserBodyWidget(private val project: Project) {

    private val prettyEditor: Editor = createEditor()
    private val minifyEditor: Editor = createEditor()
    private val queryEditor: Editor = createEditor()
    private val yamlEditor: Editor = createEditor()
    private val schemaEditor: Editor = createEditor()
    private val outputTree = Tree()
    private val previewTypeCardLayout = CardLayout()
    private val buttonGroup = ButtonGroup()
    private val statsBar = JLabel(" ").apply {
        border = BorderFactory.createEmptyBorder(2, 6, 2, 6)
        font = font.deriveFont(11f)
    }

    // Query panel state
    private var currentJson: String = ""
    private val queryExpressionField = JTextField().apply { toolTipText = "Enter expression" }
    private val queryTypeCombo: ComboBox<String> = ComboBox(arrayOf("JSONPath", "JMESPath"))
    private val queryAutoComplete: QueryAutoComplete

    private fun selectedQueryType(): String = queryTypeCombo.selectedItem as? String ?: "JSONPath"

    val container: JPanel = createContainer()

    init {
        queryAutoComplete = QueryAutoComplete(queryExpressionField, ::selectedQueryType)
        changeIcon()
        setEmptyTree()
        queryTypeCombo.addActionListener { queryAutoComplete.updateJson(currentJson) }
    }

    private fun createContainer(): JPanel {
        val previewTypeContainer = JPanel(previewTypeCardLayout).apply {
            add(createPrettyPanel(), "Pretty")
            add(createTreePanel(), "Tree")
            add(createMinifyPanel(), "Minify")
            add(createQueryPanel(), "Query")
            add(createYamlPanel(), "YAML")
            add(createSchemaPanel(), "Schema")
        }

        val previewTypeListener = { actionCommand: String ->
            previewTypeCardLayout.show(previewTypeContainer, actionCommand)
        }

        return jBorderLayout {
            putTop(createToolbar(previewTypeListener))
            putCenterFill(previewTypeContainer)
            putBottom(statsBar)
        }
    }

    private fun createToolbar(previewTypeListener: (String) -> Unit): JComponent {
        val simpleToolWindowPanel = SimpleToolWindowPanel(true, true)

        val group = DefaultActionGroup(
            JBRadioAction("Pretty", "Pretty", buttonGroup, { previewTypeListener(it.actionCommand) }, true),
            JBRadioAction("Tree", "Tree", buttonGroup, { previewTypeListener(it.actionCommand) }),
            JBRadioAction("Minify", "Minify", buttonGroup, { previewTypeListener(it.actionCommand) }),
            JBRadioAction("Query", "Query", buttonGroup, { previewTypeListener(it.actionCommand) }),
            JBRadioAction("YAML", "YAML", buttonGroup, { previewTypeListener(it.actionCommand) }),
            JBRadioAction("Schema", "Schema", buttonGroup, { previewTypeListener(it.actionCommand) }),
            CopyToClipBoardAction(
                "Copy to Clipboard",
                "Click to copy selected text to clipboard",
                AllIcons.Actions.Copy
            ),
            object : AnAction("Use Soft Wraps", "Toggle soft wraps", AllIcons.Actions.ToggleSoftWrap) {
                override fun actionPerformed(e: AnActionEvent) {
                    val editor = when (buttonGroup.selection?.actionCommand?.lowercase()) {
                        "pretty" -> prettyEditor
                        "minify" -> minifyEditor
                        "query" -> queryEditor
                        "yaml" -> yamlEditor
                        "schema" -> schemaEditor
                        else -> null
                    }
                    editor?.settings?.let { it.isUseSoftWraps = !it.isUseSoftWraps }
                }
            }
        )

        val toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, group, true)
        simpleToolWindowPanel.toolbar = toolbar.component
        return simpleToolWindowPanel
    }

    private fun createPrettyPanel() = JPanel(BorderLayout()).apply {
        add(prettyEditor.component, BorderLayout.CENTER)
    }

    private fun createMinifyPanel() = JPanel(BorderLayout()).apply {
        add(minifyEditor.component, BorderLayout.CENTER)
    }

    private fun createYamlPanel() = JPanel(BorderLayout()).apply {
        add(yamlEditor.component, BorderLayout.CENTER)
    }

    private fun createSchemaPanel() = JPanel(BorderLayout()).apply {
        add(schemaEditor.component, BorderLayout.CENTER)
    }

    private fun createQueryPanel() = JPanel(BorderLayout()).apply {
        val searchBar = JPanel(BorderLayout(4, 0)).apply {
            add(queryTypeCombo, BorderLayout.WEST)
            add(queryExpressionField, BorderLayout.CENTER)
            val rightButtons = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                add(JButton("Run").apply { addActionListener { executeQuery() } })
                val anchor = JPanel()
                val helpButton = InplaceButton(
                    "Query syntax help",
                    AllIcons.General.ContextHelp
                ) { showHelpPopup(anchor) }
                anchor.add(helpButton)
                add(anchor)
            }
            add(rightButtons, BorderLayout.EAST)
        }
        add(searchBar, BorderLayout.NORTH)
        add(queryEditor.component, BorderLayout.CENTER)
    }

    private fun createTreePanel() = JBScrollPane(outputTree)

    private fun executeQuery() {
        if (currentJson.isEmpty()) return
        val expression = queryExpressionField.text.trim()
        if (expression.isEmpty()) {
            writeToQueryEditor(JsonUtils.formatJson(currentJson))
            return
        }
        try {
            val result = when (queryTypeCombo.selectedItem as String) {
                "JSONPath" -> JsonUtils.queryJsonPath(currentJson, expression)
                "JMESPath" -> JsonUtils.queryJmesPath(currentJson, expression)
                else -> return
            }
            writeToQueryEditor(result)
        } catch (e: Exception) {
            writeToQueryEditor("Error: ${e.message}")
        }
    }

    private fun showHelpPopup(source: JComponent) {
        val tabs = JTabbedPane()

        tabs.addTab("JSONPath", scrollPane(JSONPATH_HELP))
        tabs.addTab("JMESPath", scrollPane(JMESPATH_HELP))

        tabs.preferredSize = java.awt.Dimension(480, 360)

        val popup = JPopupMenu()
        popup.add(tabs)
        popup.show(source, 0, source.height)
    }

    private fun scrollPane(text: String): JScrollPane {
        val area = JTextArea(text).apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
            font = font.deriveFont(12f)
            border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
        }
        return JScrollPane(area).also { pane ->
            pane.addMouseWheelListener { e ->
                val scrollBar = pane.verticalScrollBar
                val amount = e.unitsToScroll * scrollBar.unitIncrement
                scrollBar.value += amount
            }
        }
    }

    private fun createEditor(): Editor {
        val editorFactory = EditorFactory.getInstance()
        val document = editorFactory.createDocument("")
        val editor = editorFactory.createEditor(document, project)

        editor.settings.apply {
            isLineMarkerAreaShown = false
            isIndentGuidesShown = false
            isFoldingOutlineShown = true
            additionalColumnsCount = 3
            additionalLinesCount = 3
            isLineNumbersShown = true
            isCaretRowShown = true
            isUseSoftWraps =true
        }

        (editor as EditorEx).highlighter = createHighlighter(FileTypes.PLAIN_TEXT)
        return editor
    }

    private fun createHighlighter(fileType: LanguageFileType): LayeredLexerEditorHighlighter {
        val originalHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(fileType, null, null)
            ?: PlainSyntaxHighlighter()
        val scheme = EditorColorsManager.getInstance().globalScheme
        val highlighter = LayeredLexerEditorHighlighter(getFileHighlighter(fileType), scheme)
        highlighter.registerLayer(TEXT_ELEMENT_TYPE, LayerDescriptor(originalHighlighter, ""))
        return highlighter
    }

    private fun getFileHighlighter(fileType: FileType): SyntaxHighlighter {
        return when (fileType) {
            HtmlFileType.INSTANCE -> HtmlFileHighlighter()
            XmlFileType.INSTANCE -> XmlFileHighlighter()
            JsonFileType.INSTANCE -> JsonSyntaxHighlighterFactory.getSyntaxHighlighter(fileType, project, null)
            else -> PlainSyntaxHighlighter()
        }!!
    }

    fun showPretty(text: String, callback: (Exception) -> Unit) {
        currentJson = text
        queryAutoComplete.updateJson(text)
        try {
            val prettyJsonString = if (text.isEmpty()) "" else JsonUtils.formatJson(text)
            writeToEditor(prettyJsonString)
            writeToQueryEditor(prettyJsonString) // default query view = pretty JSON
        } catch (e: Exception) {
            when (e) {
                is JsonSyntaxException -> {
                    val message = e.message ?: e.cause?.message ?: ""
                    writeToEditor("$text\n\n\n$message")
                    callback(e)
                }

                is JsonProcessingException -> {
                    writeToEditor(text)
                    EditorHintsNotifier.notifyError(prettyEditor, e.originalMessage, e.location.charOffset)
                    callback(e)
                }
            }
        }
    }

    private fun writeToEditor(text: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            prettyEditor.document.apply {
                setReadOnly(false)
                setText(text)
                setReadOnly(true)
            }
        }
        (prettyEditor as EditorEx).highlighter = createHighlighter(JsonFileType.INSTANCE)
    }

    private fun writeToQueryEditor(text: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            queryEditor.document.apply {
                setReadOnly(false)
                setText(text)
                setReadOnly(true)
            }
        }
        (queryEditor as EditorEx).highlighter = createHighlighter(JsonFileType.INSTANCE)
    }

    private fun writeToReadOnlyEditor(editor: Editor, text: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.apply {
                setReadOnly(false)
                setText(text)
                setReadOnly(true)
            }
        }
    }

    fun showYaml(text: String?) {
        text ?: return
        try {
            val yaml = if (text.isEmpty()) "" else JsonUtils.toYaml(text)
            writeToReadOnlyEditor(yamlEditor, yaml)
        } catch (e: Exception) {
            Log.e("YAML error: ${e.message}")
        }
    }

    fun showSchema(text: String?) {
        text ?: return
        try {
            val schema = if (text.isEmpty()) "" else JsonUtils.toJsonSchema(text)
            writeToReadOnlyEditor(schemaEditor, schema)
            (schemaEditor as EditorEx).highlighter = createHighlighter(JsonFileType.INSTANCE)
        } catch (e: Exception) {
            Log.e("Schema error: ${e.message}")
        }
    }

    fun showStats(text: String?) {
        text ?: return
        if (text.isEmpty()) { statsBar.text = " "; return }
        try {
            val s = JsonUtils.computeStats(text)
            val kb = if (s.sizeBytes >= 1024) "%.1f KB".format(s.sizeBytes / 1024.0) else "${s.sizeBytes} B"
            statsBar.text = "  Keys: ${s.keys}   Depth: ${s.depth}   Objects: ${s.objects}   Arrays: ${s.arrays}   Nulls: ${s.nulls}   Size: $kb"
        } catch (_: Exception) {
            statsBar.text = " "
        }
    }

    fun showMinify(text: String?) {        text ?: return
        try {
            val minified = if (text.isEmpty()) "" else JsonUtils.minifyJson(text)
            WriteCommandAction.runWriteCommandAction(project) {
                minifyEditor.document.apply {
                    setReadOnly(false)
                    setText(minified)
                    setReadOnly(true)
                }
            }
            (minifyEditor as EditorEx).highlighter = createHighlighter(JsonFileType.INSTANCE)
        } catch (e: Exception) {
            Log.e("Minify error: ${e.message}")
        }
    }

    fun showTree(jsonString: String) {
        if (jsonString.isEmpty()) {
            setEmptyTree()
            return
        }
        try {
            val jsonMap = JsonUtils.getMap(jsonString)
            val model = TreeNodeCreator.getTreeModelFromMap(jsonMap)
            outputTree.model = model
            expandAllNodes(outputTree, 0, outputTree.rowCount)
        } catch (e: Exception) {
            Log.e("Json error catch ${e.message}")
        }
    }

    private fun changeIcon() {
        (outputTree.cellRenderer as? DefaultTreeCellRenderer)?.apply {
            closedIcon = null
            openIcon = null
            leafIcon = AllIcons.Nodes.C_plocal
        }
    }

    private fun setEmptyTree() {
        outputTree.model = DefaultTreeModel(DefaultMutableTreeNode(""))
    }

    private fun expandAllNodes(tree: JTree, startingIndex: Int, rowCount: Int) {
        for (i in startingIndex until rowCount) tree.expandRow(i)
        if (tree.rowCount != rowCount) expandAllNodes(tree, rowCount, tree.rowCount)
    }

    companion object {
        private val TEXT_ELEMENT_TYPE = IElementType("TEXT", Language.ANY)

        private val JSONPATH_HELP = """
JSONPath — Query JSON using path expressions.

SYNTAX
  $  — root element
  .  — child operator
  .. — recursive descent
  *  — wildcard (all elements)
  [] — subscript / filter

EXAMPLES
Given: {"store":{"books":[{"title":"A","price":9},{"title":"B","price":15}],"owner":"Alice"}}

  $.store.owner
    → "Alice"

  $.store.books[0].title
    → "A"

  $.store.books[*].title
    → ["A", "B"]

  $.store.books[?(@.price > 10)].title
    → ["B"]

  $..price
    → [9, 15]

  $.store.books.length()
    → 2
        """.trimIndent()

        private val JMESPATH_HELP = """
JMESPath — Query and transform JSON using JMESPath expressions.

SYNTAX
  identifier     — field access
  a.b.c          — nested field access
  [n]            — array index
  [*]            — array wildcard
  [?condition]   — filter projection
  {k: expr}      — multi-select hash
  [expr, expr]   — multi-select list

EXAMPLES
Given: {"store":{"books":[{"title":"A","price":9},{"title":"B","price":15}],"owner":"Alice"}}

  store.owner
    → "Alice"

  store.books[0].title
    → "A"

  store.books[*].title
    → ["A", "B"]

  store.books[?price > `10`].title
    → ["B"]

  store.books[*].{name: title, cost: price}
    → [{"name":"A","cost":9},{"name":"B","cost":15}]

  length(store.books)
    → 2
        """.trimIndent()
    }
}
