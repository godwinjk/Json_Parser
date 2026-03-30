package com.godwin.jsonparser.ui

import com.godwin.jsonparser.generator.common.ui.*
import com.godwin.jsonparser.generator.common.util.FileGenerationUtil
import com.godwin.jsonparser.rx.Publisher
import com.godwin.jsonparser.rx.Subscriber
import com.godwin.jsonparser.ui.components.CircularProgress
import com.godwin.jsonparser.ui.dialog.ParserSettingsDialog
import com.godwin.jsonparser.util.JsonDownloader
import com.godwin.jsonparser.util.JsonUtils
import com.godwin.jsonparser.util.NotificationUtil
import com.godwin.jsonparser.util.analytics.AnalyticsConstant
import com.godwin.jsonparser.util.analytics.AnalyticsService
import com.godwin.jsonparser.util.repair.JsonRepairEngine
import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.DiffRequestFactory
import com.intellij.diff.merge.MergeResult
import com.intellij.icons.AllIcons
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
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.ui.InplaceButton
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
    private var repairLoadingIndicator: CircularProgress? = null
    private var generateCodeButton: JButton? = null
    private var jsonException: Exception? = null
    private var autoParseTimer: javax.swing.Timer? = null
    val container: JPanel = jBorderLayout {
        putCenterFill(createSplitPane())
    }

    init {
        Subscriber.add(this)
        setupAutoparse()
    }

    private fun setupAutoparse() {
        autoParseTimer = javax.swing.Timer(600) { handleParse() }.apply { isRepeats = false }
        inputEditor.document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                autoParseTimer?.restart()
            }
        })
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
                        repairLoadingIndicator = CircularProgress().apply {
                            isVisible = false
                            preferredSize = java.awt.Dimension(18, 18)
                            toolTipText = "Repairing JSON..."
                        }
                        add(repairLoadingIndicator)
                        fillSpace()
                        val settingsBtn = InplaceButton("Parser settings", AllIcons.General.Settings) { handleSettings() }
                        settingsBtn.preferredSize = java.awt.Dimension(22, 22)
                        settingsBtn.maximumSize = java.awt.Dimension(22, 22)
                        add(settingsBtn)
                    }
                    jHorizontalLinearLayout {
                        jButton("Support ❤️", { handleDonate() })
                        fillSpace()
                        if (isAndroidStudioOrIntelliJ()) {
                            generateCodeButton = JButton("Generate Code").apply {
                                isEnabled = false
                                addActionListener { handleGenerateCode() }
                            }
                            add(generateCodeButton)
                        }
                    }
                }
            }
        }.apply {
            border = BorderFactory.createTitledBorder("Enter JSON string")
            toolTipText = "Enter raw json"
        }
    }

    private fun handleSettings() {
        ParserSettingsDialog().show()
    }

    private fun handleDonate() {
        AnalyticsService.track(AnalyticsConstant.ACTION_DONATE)
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
        installPopupMenu(editor)

        return editor
    }

    private fun installPopupMenu(editor: Editor) {
        val popup = createPopupMenu()
        editor.contentComponent.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mousePressed(e: java.awt.event.MouseEvent) = maybeShow(e)
            override fun mouseReleased(e: java.awt.event.MouseEvent) = maybeShow(e)
            private fun maybeShow(e: java.awt.event.MouseEvent) {
                if (e.isPopupTrigger) popup.show(e.component, e.x, e.y)
            }
        })
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
        AnalyticsService.track(AnalyticsConstant.ACTION_PARSE)
        val jsonString = JsonUtils.cleanUpJsonString(inputEditor.document.text)
        try {
            showBody(jsonString)
            NotificationUtil.showDonateNotification()
        } catch (_: Exception) {
            showHideGenerateCodeButton(false)
            AnalyticsService.track(AnalyticsConstant.PARSE_FAILED)
        }
    }

    private fun handleGenerateCode() {
        AnalyticsService.track(AnalyticsConstant.ACTION_GENERATE_CODE)
        val jsonString = inputEditor.document.text.trim()
        if (jsonString.isEmpty()) {
            Messages.showWarningDialog(project, "Please enter JSON first", "No JSON")
            return
        }

        FileChooser.chooseFile(
            FileChooserDescriptor(false, true, false, false, false, false),
            project,
            LocalFileSystem.getInstance().findFileByPath(project.basePath ?: "")
        ) { directory ->
            val psiDirectory =
                com.intellij.psi.PsiManager.getInstance(project).findDirectory(directory) ?: return@chooseFile
            val directoryFactory = com.intellij.psi.impl.file.PsiDirectoryFactory.getInstance(project)
            val packageName = directoryFactory.getQualifiedName(psiDirectory, false)
            val packageDeclare = if (packageName.isNotEmpty()) "package $packageName" else ""
            val psiFileFactory = com.intellij.psi.PsiFileFactory.getInstance(project)

            val inputDialog = JsonInputDialog("", project, jsonString)
            inputDialog.show()
            val className = inputDialog.getClassName()
            if (className.isEmpty()) return@chooseFile

            val fileType = inputDialog.getFileType()
            FileGenerationUtil.generate(
                className, jsonString, packageDeclare, project, psiFileFactory, psiDirectory, fileType
            )
            showHideGenerateCodeButton(false)
        }
    }

    private fun showBody(jsonString: String) {
        bodyWidget.showPretty(jsonString, { e ->
            jsonException = e
            showHideRepairButton(true)
            showHideGenerateCodeButton(false)
            AnalyticsService.track(AnalyticsConstant.PARSE_FAILED)
        })
        showHideGenerateCodeButton(true)
        bodyWidget.showMinify(jsonString)
        bodyWidget.showYaml(jsonString)
        bodyWidget.showSchema(jsonString)
        bodyWidget.showStats(jsonString)
        bodyWidget.showTree(jsonString)
    }

    private fun handleRepair() {
        AnalyticsService.track(AnalyticsConstant.ACTION_REPAIR)
        val input = inputEditor.document.text

        setRepairLoading(true)

        JsonRepairEngine.repair(input).thenAccept { repairedJson ->
            SwingUtilities.invokeLater {
                setRepairLoading(false)
                if (repairedJson == null) {
                    AnalyticsService.track(AnalyticsConstant.REPAIR_FAILED, jsonData = input)
                    NotificationUtil.showJsonRepairFailed()
                    return@invokeLater
                }
                showHideRepairButton(false)
                showMergeRepair(input, repairedJson)
                handleParse()
            }
        }.exceptionally { e ->
            SwingUtilities.invokeLater {
                setRepairLoading(false)
                Messages.showErrorDialog(project, "Failed to repair JSON: ${e.message}", "Repair Failed")
                AnalyticsService.track(
                    AnalyticsConstant.REPAIR_FAILED, jsonData = input, additionaldata = e.message
                )
            }
            null
        }
    }

    private fun setRepairLoading(loading: Boolean) {
        SwingUtilities.invokeLater {
            repairButton?.isVisible = !loading
            repairLoadingIndicator?.isVisible = loading
            repairLoadingIndicator?.parent?.revalidate()
            repairLoadingIndicator?.parent?.repaint()
            if (loading) {
                repairLoadingIndicator?.start()
            } else {
                repairLoadingIndicator?.stop()
            }
        }
    }

    private fun showMergeRepair(original: String, repaired: String) {
        AnalyticsService.track(AnalyticsConstant.ACTION_MERGE_REPAIR)
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
        AnalyticsService.track(AnalyticsConstant.ACTION_PASTE)
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
        AnalyticsService.track(AnalyticsConstant.ACTION_LOAD_FILE)
        FileChooser.chooseFile(
            FileChooserDescriptor(
                true,
                false,
                false,
                false,
                false,
                false
            ),
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
        AnalyticsService.track(AnalyticsConstant.ACTION_RETRIEVE_URL)
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

    private fun showHideGenerateCodeButton(isEnabled: Boolean) {
        SwingUtilities.invokeLater {
            generateCodeButton?.isEnabled = isEnabled
        }
    }

    private fun isAndroidStudioOrIntelliJ(): Boolean {
        val platformPrefix = System.getProperty("idea.platform.prefix") ?: ""
        return platformPrefix.isEmpty() || platformPrefix == "Idea" || platformPrefix == "AndroidStudio"
    }
}
