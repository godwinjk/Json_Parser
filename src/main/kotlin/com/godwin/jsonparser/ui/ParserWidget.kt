package com.godwin.jsonparser.ui

import com.godwin.jsonparser.generator.common.ui.bottomContainer
import com.godwin.jsonparser.generator.common.ui.jBorderLayout
import com.godwin.jsonparser.generator.common.ui.jButton
import com.godwin.jsonparser.generator.common.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.rx.Publisher
import com.godwin.jsonparser.rx.Subscriber
import com.godwin.jsonparser.ui.dialog.OptionDialog
import com.godwin.jsonparser.util.JsonDownloader
import com.godwin.jsonparser.util.JsonUtils
import com.godwin.jsonparser.util.NotificationUtil
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.progress.util.DispatchThreadProgressWindow
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import javax.swing.*
import javax.swing.plaf.basic.BasicSplitPaneDivider
import javax.swing.plaf.basic.BasicSplitPaneUI

class ParserWidget(
    private val project: Project,
    private val parent: Disposable,
    private val parserWidget: IParserWidget
) : Publisher {

    private val inputEditor: Editor = createEditor()
    private val bodyWidget = ParserBodyWidget(project)

    val container: JPanel = jBorderLayout {
        putCenterFill(createSplitPane())
    }

    init {
        Subscriber.add(this)
    }

    private fun createSplitPane(): JSplitPane {
        return JSplitPane(JSplitPane.VERTICAL_SPLIT).apply {
            dividerLocation = 241
            dividerSize = 5
            isContinuousLayout = false
            isOneTouchExpandable = false

            leftComponent = createInputPanel()
            rightComponent = createOutputPanel()

            hideSplitPaneDivider(this)
        }
    }

    private fun createInputPanel(): JPanel {
        return jBorderLayout {
            putCenterFill(inputEditor.component)
            bottomContainer {
                jHorizontalLinearLayout {
                    jButton("Parse", { handleParse() })
                    fillSpace()
                    jButton("Options", { handleOptions() })
                }
            }
        }.apply {
            border = BorderFactory.createTitledBorder("Enter JSON string")
            toolTipText = "Enter raw json"
        }
    }

    private fun createOutputPanel(): JPanel {
        return JPanel(BorderLayout()).apply {
            border = BorderFactory.createTitledBorder("Parsed Json")
            add(bodyWidget.container, BorderLayout.CENTER)
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
        }

        editor.component.componentPopupMenu = createPopupMenu()

        return editor
    }

    private fun createPopupMenu() = JPopupMenu().apply {
        add(createMenuItem("Paste from clipboard") { actionPaste() })
        add(createMenuItem("Retrieve content from Http URL") { actionGetFromUrl() })
        add(createMenuItem("Load from local file") { actionChooseFile() })
    }

    private fun createMenuItem(text: String, action: () -> Unit) = JMenuItem(text).apply {
        addActionListener { action() }
    }

    private fun hideSplitPaneDivider(splitPane: JSplitPane) {
        customizeSplitPane(splitPane)
        ApplicationManager.getApplication().messageBus
            .connect(parent)
            .subscribe(LafManagerListener.TOPIC, LafManagerListener {
                SwingUtilities.invokeLater { customizeSplitPane(splitPane) }
            })
    }

    private fun customizeSplitPane(splitPane: JSplitPane) {
        splitPane.setUI(object : BasicSplitPaneUI() {
            override fun createDefaultDivider() = object : BasicSplitPaneDivider(this) {
                override fun paint(g: Graphics) {}
            }
        })
    }

    private fun handleParse() {
        val jsonString = JsonUtils.cleanUpJsonString(inputEditor.document.text)
        showBody(jsonString)
        NotificationUtil.showDonateNotification()
    }

    private fun handleOptions() {
        val options = listOf("Retrieve from URL", "Load from file")
        val dialog = OptionDialog(options)
        dialog.show()

        when (dialog.selectedIndex) {
            0 -> actionGetFromUrl()
            1 -> actionChooseFile()
        }
    }

    private fun showBody(jsonString: String) {
        bodyWidget.showPretty(jsonString)
        bodyWidget.showRaw(jsonString)
        bodyWidget.showTree(jsonString)
    }

    override fun onMessage(message: String) {
        try {
            val shouldUpdate = parserWidget.getTabs()?.getCurrentComponent() == container
                    || parserWidget.getTabs() == null

            if (shouldUpdate) {
                WriteCommandAction.runWriteCommandAction(project) {
                    inputEditor.document.setText(message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun actionPaste() {
        val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
        if (transferable?.isDataFlavorSupported(DataFlavor.stringFlavor) == true) {
            try {
                val clipboardText = transferable.getTransferData(DataFlavor.stringFlavor) as String
                ApplicationManager.getApplication().runWriteAction {
                    inputEditor.document.setText(clipboardText)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun actionChooseFile() {
        FileChooser.chooseFile(
            FileChooserDescriptor(true, false, false, false, false, false),
            project,
            null
        ) { file ->
            try {
                val content = String(file.contentsToByteArray()).replace("\r\n", "\n")
                ApplicationManager.getApplication().runWriteAction {
                    inputEditor.document.setText(content)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun actionGetFromUrl() {
        val inputData = Messages.showMultilineInputDialog(
            project,
            "Retrieve Content from Http URL\n\nTip: Paste your header in NEXT LINE with a colon(:)",
            "URL",
            null,
            null,
            null
        )

        if (!inputData.isNullOrEmpty()) {
            val progressWindow = DispatchThreadProgressWindow(false, project)
            progressWindow.isIndeterminate = true
            progressWindow.setRunnable {
                try {
                    val data = JsonDownloader.getData(inputData)
                    ApplicationManager.getApplication().runWriteAction {
                        inputEditor.document.setText(data)
                    }
                } finally {
                    progressWindow.stop()
                }
            }
            progressWindow.start()
        }
    }
}
