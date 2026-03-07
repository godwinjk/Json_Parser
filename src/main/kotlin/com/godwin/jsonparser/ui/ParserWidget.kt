package com.godwin.jsonparser.ui

import com.godwin.jsonparser.generator.common.ui.*
import com.godwin.jsonparser.rx.Publisher
import com.godwin.jsonparser.rx.Subscriber
import com.godwin.jsonparser.ui.dialog.OptionDialog
import com.godwin.jsonparser.util.JsonDownloader
import com.godwin.jsonparser.util.JsonUtils
import com.godwin.jsonparser.util.NotificationUtil
import com.godwin.jsonparser.util.analytics.Analytics
import com.godwin.jsonparser.util.analytics.AnalyticsConstant
import com.godwin.jsonparser.util.repair.JsonRepairEngine
import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.DiffRequestFactory
import com.intellij.diff.merge.MergeResult
import com.intellij.ide.BrowserUtil
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
import java.net.URI
import javax.swing.*
import javax.swing.plaf.basic.BasicSplitPaneDivider
import javax.swing.plaf.basic.BasicSplitPaneUI

class ParserWidget(
    private val project: Project, private val parent: Disposable, private val parserWidget: IParserWidget
) : Publisher {

    private val inputEditor: Editor = createEditor()
    private val bodyWidget = ParserBodyWidget(project)
    private var repairButton: JButton? = null
    private var jsonException: Exception? = null
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
                jVerticalLinearLayout {
                    jHorizontalLinearLayout {
                        jButton("Parse", { handleParse() })
                        repairButton = JButton("Repair").apply {
                            isVisible = false
                            addActionListener { handleRepair() }
                        }
                        add(repairButton)
                        fillSpace()
                        jButton("Options", { handleOptions() })
                    }
                    jHorizontalLinearLayout {
                        jButton("Support ❤️", { handleDonate() })
                        fillSpace()
                    }
                }
            }
        }.apply {
            border = BorderFactory.createTitledBorder("Enter JSON string")
            toolTipText = "Enter raw json"
        }
    }

    private fun handleDonate() {
        Analytics.track(AnalyticsConstant.ACTION_DONATE)
        BrowserUtil.browse(URI.create("https://paypal.me/godwinj"))
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
        ApplicationManager.getApplication().messageBus.connect(parent)
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
        Analytics.track(AnalyticsConstant.ACTION_PARSE)
        val jsonString = JsonUtils.cleanUpJsonString(inputEditor.document.text)
        try {
            showBody(jsonString)
            NotificationUtil.showDonateNotification()
        } catch (e: Exception) {
        }
    }

    private fun handleOptions() {
        Analytics.track(AnalyticsConstant.ACTION_OPTIONS)
        val options = listOf("Retrieve from URL", "Load from file")
        val dialog = OptionDialog(options)
        dialog.show()

        when (dialog.selectedIndex) {
            0 -> actionGetFromUrl()
            1 -> actionChooseFile()
        }
    }

    private fun showBody(jsonString: String) {
        bodyWidget.showPretty(jsonString, { e ->
            jsonException = e
            showHideRepairButton(true)
        })
        bodyWidget.showRaw(jsonString)
        bodyWidget.showTree(jsonString)
    }

    private fun handleRepair() {
        Analytics.track(AnalyticsConstant.ACTION_REPAIR)
        try {
            val input = inputEditor.document.text
            val repairedJson = JsonRepairEngine.repair(project, input)

            showHideRepairButton(false)
//            showDiff(input, repairedJson)
            if (repairedJson == null) {
                NotificationUtil.showJsonRepairFailed()
                return
            }
            showMergeRepair(input, repairedJson)
            handleParse()
        } catch (e: Exception) {
            Messages.showErrorDialog(project, "Failed to repair JSON: ${e.message}", "Repair Failed")
        }
    }

    private fun showMergeRepair(original: String, repaired: String) {
        Analytics.track(AnalyticsConstant.ACTION_MERGE_REPAIR)
        val contentFactory = DiffContentFactory.getInstance()

        // 1. Create contents
        val localContent = contentFactory.create(original)
        val baseContent = contentFactory.create(original)
        val remoteContent = contentFactory.create(repaired)

        // 2. IMPORTANT: Manually disable read-only mode for the center (base) document
        // This allows the merge tool to write the user's choices into this buffer.
        baseContent.document.setReadOnly(false)

        val contents = listOf(localContent, baseContent, remoteContent)
        val titles = listOf("Original", "Base", "Repaired")

        val request = DiffRequestFactory.getInstance().createMergeRequest(
            project, null, baseContent.document, // The center document
            contents.map { it.document.text }, "JSON Repair Merge", titles
        ) { result ->
            if (result != MergeResult.CANCEL) {
                val finalJson = baseContent.document.text
                WriteCommandAction.runWriteCommandAction(project) {
                    inputEditor.document.setText(finalJson)
                    handleParse()
                }
            }
        }

        DiffManager.getInstance().showMerge(project, request)
    }

    override fun onMessage(message: String) {
        try {
            val shouldUpdate =
                parserWidget.getTabs()?.getCurrentComponent() == container || parserWidget.getTabs() == null

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
        Analytics.track(AnalyticsConstant.ACTION_PASTE)
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
        Analytics.track(AnalyticsConstant.ACTION_LOAD_FILE)
        FileChooser.chooseFile(
            FileChooserDescriptor(true, false, false, false, false, false), project, null
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
        Analytics.track(AnalyticsConstant.ACTION_RETRIEVE_URL)
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

    private fun showHideRepairButton(isVisible: Boolean) {
        SwingUtilities.invokeLater {
            repairButton?.isVisible = isVisible
            repairButton?.parent?.revalidate()
            repairButton?.parent?.repaint()
        }
    }
}
