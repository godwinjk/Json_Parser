package com.godwin.jsonparser.ui

import com.fasterxml.jackson.core.JsonProcessingException
import com.godwin.jsonparser.action.CopyToClipBoardAction
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
import com.intellij.openapi.actionSystem.*import com.intellij.openapi.command.WriteCommandAction
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
import org.jetbrains.yaml.YAMLFileType
import org.jetbrains.yaml.YAMLSyntaxHighlighter
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Dimension
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
    private val treeSearchField = JTextField().apply {
        toolTipText = "Search keys or values..."
        maximumSize = Dimension(Int.MAX_VALUE, 28)
    }
    private val treePathLabel = JLabel(" ").apply {
        border = BorderFactory.createEmptyBorder(2, 6, 2, 6)
        font = font.deriveFont(11f)
    }
    private val previewTypeCardLayout = CardLayout()
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
        var currentTab = "Pretty"

        fun tabAction(name: String, tooltip: String, icon: javax.swing.Icon) =
            object : ToggleAction(tooltip, tooltip, icon) {
                override fun isSelected(e: AnActionEvent) = currentTab == name
                override fun setSelected(e: AnActionEvent, state: Boolean) {
                    if (state) { currentTab = name; previewTypeListener(name) }
                }
                override fun getActionUpdateThread() = com.intellij.openapi.actionSystem.ActionUpdateThread.EDT
            }

        val group = DefaultActionGroup(
            tabAction("Pretty", "Pretty", AllIcons.FileTypes.Json),
            tabAction("Tree", "Tree", AllIcons.Actions.ShowAsTree),
            tabAction("YAML", "YAML", AllIcons.FileTypes.Yaml),
            tabAction("Query", "Query", AllIcons.Actions.Find),
            tabAction("Minify", "Minify", AllIcons.Actions.Collapseall),
            tabAction("Schema", "Schema", AllIcons.FileTypes.JsonSchema),
            Separator.getInstance(),
            CopyToClipBoardAction(
                "Copy to Clipboard",
                "Click to copy selected text to clipboard",
                AllIcons.Actions.Copy
            ),
            object : AnAction("Use Soft Wraps", "Toggle soft wraps", AllIcons.Actions.ToggleSoftWrap) {
                override fun actionPerformed(e: AnActionEvent) {
                    val editor = when (currentTab.lowercase()) {
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
        toolbar.targetComponent = simpleToolWindowPanel
        simpleToolWindowPanel.toolbar = toolbar.component

        // Register toolbar component for tutorial (the whole toolbar panel)
        com.godwin.jsonparser.ui.onboarding.TutorialSteps.prettyTabButton = simpleToolWindowPanel

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

    private fun createTreePanel(): JComponent {
        val searchBar = JPanel(BorderLayout(4, 0)).apply {
            border = BorderFactory.createEmptyBorder(2, 2, 2, 2)
            val clearBtn = JButton("×").apply {
                preferredSize = Dimension(24, 24)
                addActionListener { treeSearchField.text = ""; applyTreeSearch("") }
            }
            add(treeSearchField, BorderLayout.CENTER)
            add(clearBtn, BorderLayout.EAST)
        }
        treeSearchField.document.addDocumentListener(object : javax.swing.event.DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent) = applyTreeSearch(treeSearchField.text)
            override fun removeUpdate(e: javax.swing.event.DocumentEvent) = applyTreeSearch(treeSearchField.text)
            override fun changedUpdate(e: javax.swing.event.DocumentEvent) = applyTreeSearch(treeSearchField.text)
        })
        outputTree.addTreeSelectionListener { e ->
            val path = e.path ?: return@addTreeSelectionListener
            val jsonPath = buildJsonPath(path)
            treePathLabel.text = "  $jsonPath"
        }
        return JPanel(BorderLayout()).apply {
            add(searchBar, BorderLayout.NORTH)
            add(JBScrollPane(outputTree), BorderLayout.CENTER)
            add(treePathLabel, BorderLayout.SOUTH)
        }
    }

    private fun buildJsonPath(treePath: javax.swing.tree.TreePath): String {
        val parts = mutableListOf<String>()
        for (i in 1 until treePath.pathCount) {
            val node = treePath.getPathComponent(i).toString()
            // node text is like "key: value" or "key {n}" — extract key
            val key = node.substringBefore(":").substringBefore("{").trim()
            if (key.isNotBlank()) parts.add(key)
        }
        return if (parts.isEmpty()) "$" else "$.${parts.joinToString(".")}"
    }

    private fun applyTreeSearch(query: String) {
        val model = outputTree.model as? javax.swing.tree.DefaultTreeModel ?: return
        val root = model.root as? javax.swing.tree.DefaultMutableTreeNode ?: return
        if (query.isBlank()) {
            // Restore full tree
            expandAllNodes(outputTree, 0, outputTree.rowCount)
            outputTree.cellRenderer = null
            changeIcon()
            return
        }
        val lower = query.lowercase()
        // Set custom renderer to highlight matches
        outputTree.setCellRenderer(object : javax.swing.tree.DefaultTreeCellRenderer() {
            override fun getTreeCellRendererComponent(
                tree: javax.swing.JTree, value: Any?, sel: Boolean,
                expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean
            ): java.awt.Component {
                val comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
                val text = value.toString()
                if (text.lowercase().contains(lower)) {
                    foreground = java.awt.Color(0, 120, 215)
                    font = font.deriveFont(java.awt.Font.BOLD)
                } else {
                    foreground = if (sel) textSelectionColor else textNonSelectionColor
                    font = font.deriveFont(java.awt.Font.PLAIN)
                }
                return comp
            }
        })
        // Expand all and scroll to first match
        expandAllNodes(outputTree, 0, outputTree.rowCount)
        for (i in 0 until outputTree.rowCount) {
            val path = outputTree.getPathForRow(i)
            val node = path?.lastPathComponent?.toString() ?: continue
            if (node.lowercase().contains(lower)) {
                outputTree.scrollPathToVisible(path)
                outputTree.selectionPath = path
                break
            }
        }
        outputTree.repaint()
    }

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
            YAMLFileType.YML -> YAMLSyntaxHighlighter()
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
            val yamlFileType = FileTypeManager.getInstance().findFileTypeByName("YAML")
            if (yamlFileType is LanguageFileType) {
                (yamlEditor as EditorEx).highlighter = createHighlighter(yamlFileType)
            }
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
