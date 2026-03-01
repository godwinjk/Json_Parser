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
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.psi.tree.IElementType
import java.awt.BorderLayout
import java.awt.CardLayout
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel

class ParserBodyWidget(
    private val project: Project,
) {
    private val prettyEditor: Editor = createEditor()
    private val rawEditor: Editor = createEditor()
    private val outputTree = JTree()
    private val previewTypeCardLayout = CardLayout()
    private val buttonGroup = ButtonGroup()

    val container: JPanel = createContainer()

    init {
        changeIcon()
        setEmptyTree()
    }

    private fun createContainer(): JPanel {
        val previewTypeContainer = JPanel(previewTypeCardLayout).apply {
            add(createPrettyPanel(), "Pretty")
            add(createRawPanel(), "Raw")
            add(createTreePanel(), "Tree")
        }

        val previewTypeListener = { actionCommand: String ->
            previewTypeCardLayout.show(previewTypeContainer, actionCommand)
        }

        return jBorderLayout {
            putTop(createToolbar(previewTypeListener))
            putCenterFill(previewTypeContainer)
        }
    }

    private fun createToolbar(previewTypeListener: (String) -> Unit): JComponent {
        val simpleToolWindowPanel = SimpleToolWindowPanel(true, true)

        val group = DefaultActionGroup(
            JBRadioAction("Pretty", "Pretty", buttonGroup, { previewTypeListener(it.actionCommand) }, true),
            JBRadioAction("Raw", "Raw", buttonGroup, { previewTypeListener(it.actionCommand) }),
            JBRadioAction("Tree", "Tree", buttonGroup, { previewTypeListener(it.actionCommand) }),
            CopyToClipBoardAction(
                "Copy to Clipboard",
                "Click to copy selected text to clipboard",
                AllIcons.Actions.Copy
            ),
            object : AnAction(
                "Use Soft Wraps",
                "Toggle using soft wraps in current editor",
                AllIcons.Actions.ToggleSoftWrap
            ) {
                override fun actionPerformed(e: AnActionEvent) {
                    try {
                        val actionCommand = buttonGroup.selection?.actionCommand
                        val editor = when (actionCommand?.lowercase()) {
                            "pretty" -> prettyEditor
                            "raw" -> rawEditor
                            else -> null
                        }
                        editor?.settings?.let { settings ->
                            settings.isUseSoftWraps = !settings.isUseSoftWraps()
                        }
                    } catch (e: Exception) {
                        Log.e(e.toString())
                    }
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

    private fun createRawPanel() = JPanel(BorderLayout()).apply {
        add(rawEditor.component, BorderLayout.CENTER)
    }

    private fun createTreePanel() = JScrollPane(outputTree)

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
        try {
            val prettyJsonString = if (text.isEmpty()) "" else JsonUtils.formatJson(text)
            writeToEditor(prettyJsonString)
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

    fun showRaw(text: String?) {
        text ?: return
        try {
            WriteCommandAction.runWriteCommandAction(project) {
                rawEditor.document.setText(text)
            }
        } catch (e: Exception) {
            Log.e("Json error catch ${e.message}")
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
        val root = DefaultMutableTreeNode("")
        outputTree.model = DefaultTreeModel(root)
    }

    private fun expandAllNodes(tree: JTree, startingIndex: Int, rowCount: Int) {
        for (i in startingIndex until rowCount) {
            tree.expandRow(i)
        }
        if (tree.rowCount != rowCount) {
            expandAllNodes(tree, rowCount, tree.rowCount)
        }
    }

    companion object {
        private val TEXT_ELEMENT_TYPE = IElementType("TEXT", Language.ANY)
    }
}
