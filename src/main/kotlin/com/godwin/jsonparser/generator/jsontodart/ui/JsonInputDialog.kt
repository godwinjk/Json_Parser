package com.godwin.jsonparser.generator.jsontodart.ui

import com.godwin.jsonparser.generator.jsontodart.filetype.GenFileType
import com.godwin.jsonparser.generator.jsontodart.utils.addComponentIntoVerticalBoxAlignmentLeft
import com.godwin.jsonparser.icons.JsonIcons
import com.google.gson.*
import com.intellij.json.JsonFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.progress.util.DispatchThreadProgressWindow
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBDimension
import com.intellij.util.ui.JBEmptyBorder
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.ActionEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.net.URL
import javax.swing.*
import javax.swing.text.JTextComponent


/**
 * Dialog widget relative
 * Created by Seal.wu on 2017/9/21.
 */

class MyInputValidator : InputValidator {
    lateinit var jsonInputEditor: Editor

    override fun checkInput(className: String): Boolean {
        return className.isNotBlank() && inputIsValidJson(jsonInputEditor.document.text)
    }

    override fun canClose(inputString: String): Boolean = true

    private fun inputIsValidJson(string: String) = try {
        val jsonElement = JsonParser().parse(string)
        (jsonElement.isJsonObject || jsonElement.isJsonArray)
    } catch (e: JsonSyntaxException) {
        false
    }
}

val myInputValidator = MyInputValidator()

/**
 * Json input Dialog
 */
class JsonInputDialog(classsName: String, private val project: Project) : Messages.InputDialog(
    project,
    "Please input the class name, JSON String and select type of file to generate class",
    "Generate Classes from JSON",
    JsonIcons.jsonLogo,
    "",
    myInputValidator
) {
    private lateinit var jsonContentEditor: Editor

    private val prettyGson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create()

    private var fileType: GenFileType = GenFileType.Dart

    init {
        setOKButtonText("Generate")
        myField.text = classsName
    }

    override fun createMessagePanel(): JPanel {

        jsonContentEditor = createJsonContentEditor()
        myField = createTextFieldComponent()
        myInputValidator.jsonInputEditor = jsonContentEditor

        val classNameInputContainer = createLinearLayoutVertical()
            .apply {
                val classNameTitle = JBLabel("Class name: ")
                classNameTitle.border = JBEmptyBorder(5, 0, 5, 0)
                addComponentIntoVerticalBoxAlignmentLeft(classNameTitle)
                addComponentIntoVerticalBoxAlignmentLeft(myField)
                preferredSize = JBDimension(500, 56)
            }


        val jsonInputContainer = createLinearLayoutVertical()
            .apply {
                preferredSize = JBDimension(700, 400)
                border = JBEmptyBorder(5, 0, 5, 5)
                val jsonTitle = JBLabel("JSON text:")
                jsonTitle.border = JBEmptyBorder(5, 0, 5, 0)
                addComponentIntoVerticalBoxAlignmentLeft(jsonTitle)
                addComponentIntoVerticalBoxAlignmentLeft(jsonContentEditor.component)
            }


        val centerContainer = JPanel()
            .apply {
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                addComponentIntoVerticalBoxAlignmentLeft(classNameInputContainer)
                addComponentIntoVerticalBoxAlignmentLeft(jsonInputContainer)
            }

        val advancedButton = JButton("Advanced")
            .apply {
                horizontalAlignment = SwingConstants.CENTER
                addActionListener(object : AbstractAction() {
                    override fun actionPerformed(e: ActionEvent) {
                        AdvancedDialog(false).show()
                    }
                })
            }

        val formatButton = JButton("Format")
            .apply {
                horizontalAlignment = SwingConstants.CENTER
                addActionListener(object : AbstractAction() {
                    override fun actionPerformed(p0: ActionEvent?) {
                        handleFormatJSONString()
                    }
                })
            }

        val settingContainer = JPanel()
            .apply {
                border = JBEmptyBorder(0, 5, 5, 7)
                layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                add(advancedButton)
                add(Box.createHorizontalGlue())
                add(formatButton)
            }

        // Create two JRadioButtons
        val dartButton = JRadioButton("Dart").apply {
            addActionListener { e ->
                run {
                    fileType = GenFileType.Dart
                }
            }

        }
        val kotlinButton = JRadioButton("Kotlin").apply {
            addActionListener { e ->
                run {
                    fileType = GenFileType.Kotlin
                }
            }
        }
        dartButton.isSelected = true

        // Create a ButtonGroup and add the radio buttons to it
        val group = ButtonGroup()
        group.add(dartButton)
        group.add(kotlinButton)
        val fileTypeContainer = JPanel()
            .apply {
                border = JBEmptyBorder(0, 5, 5, 7)
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                add(advancedButton)
                add(Box.createHorizontalGlue())
                add(dartButton)
                add(kotlinButton)
            }
        val bottomContainer = JPanel(BorderLayout())
            .apply {
                add(settingContainer, BorderLayout.NORTH)
                add(fileTypeContainer, BorderLayout.SOUTH)
            }
        return JPanel(BorderLayout())
            .apply {
                if (myMessage != null) {
                    add(createTextComponent(), BorderLayout.NORTH)
                }
                add(centerContainer, BorderLayout.CENTER)
                add(bottomContainer, BorderLayout.SOUTH)
            }
    }

    private fun createJsonContentEditor(): Editor {
        val editorFactory = EditorFactory.getInstance()
        val document = editorFactory.createDocument("").apply {
            setReadOnly(false)
            addDocumentListener(object : com.intellij.openapi.editor.event.DocumentListener {
                override fun documentChanged(event: DocumentEvent) = revalidate()

                override fun beforeDocumentChange(event: DocumentEvent) = Unit
            })
        }

        val editor = editorFactory.createEditor(document, null, JsonFileType.INSTANCE, false)

        editor.component
            .apply {
                isEnabled = true
                preferredSize = Dimension(640, 480)
                autoscrolls = true
            }

        val contentComponent = editor.contentComponent
        contentComponent.isFocusable = true
        contentComponent.componentPopupMenu = JPopupMenu().apply {
            add(createPasteFromClipboardMenuItem())
            add(createRetrieveContentFromHttpURLMenuItem())
            add(createLoadFromLocalFileMenu())
        }

        return editor
    }

    override fun createTextFieldComponent(): JTextComponent {

        return JTextField()
            .apply {
                preferredSize = JBDimension(400, 40)
                addKeyListener(object : KeyAdapter() {
                    override fun keyTyped(e: KeyEvent) {
                        if (e.keyChar == '˚') {
                            e.consume()
                        }
                    }
                })
            }
    }

    private fun createPasteFromClipboardMenuItem() = JMenuItem("Paste from clipboard").apply {
        addActionListener {
            val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                jsonContentEditor.document.setText(transferable.getTransferData(DataFlavor.stringFlavor).toString())
            }
        }
    }

    private fun createRetrieveContentFromHttpURLMenuItem() = JMenuItem("Retrieve content from Http URL").apply {
        addActionListener {
            val url = Messages.showInputDialog("URL", "Retrieve content from Http URL", null, null, UrlInputValidator)
            val p = DispatchThreadProgressWindow(false, project)
            p.isIndeterminate = true
            p.setRunnable {
                try {
                    val urlContent = URL(url).readText()
                    jsonContentEditor.document.setText(urlContent.replace("\r\n", "\n"))
                } finally {
                    p.stop()
                }
            }
            p.start()
        }
    }

    private fun createLoadFromLocalFileMenu() = JMenuItem("Load from local file").apply {
        addActionListener {
            FileChooser.chooseFile(FileChooserDescriptor(true, false, false, false, false, false), null, null) { file ->
                val content = String(file.contentsToByteArray())
                ApplicationManager.getApplication().runWriteAction {
                    jsonContentEditor.document.setText(content.replace("\r\n", "\n"))
                }
            }
        }
    }

    /**
     * get the user input class name
     */
    fun getClassName(): String = if (exitCode == 0) this.myField.text.trim() else ""

    override fun getInputString(): String = if (exitCode == 0) jsonContentEditor.document.text.trim() else ""

    override fun getPreferredFocusedComponent(): JComponent? {
        return if (this.myField.text.isNullOrEmpty()) {
            this.myField
        } else {
            jsonContentEditor.contentComponent
        }
    }

    fun getGetFileType(): GenFileType {
        return fileType
    }

    fun handleFormatJSONString() {
        val currentText = jsonContentEditor.document.text
        if (currentText.isNotEmpty()) {
            try {
                val jsonElement = prettyGson.fromJson<JsonElement>(currentText, JsonElement::class.java)
                val formatJSON = prettyGson.toJson(jsonElement)
                jsonContentEditor.document.setText(formatJSON)
            } catch (e: Exception) {
            }
        }
    }

    private fun revalidate() {
        okAction.isEnabled = myInputValidator.checkInput(myField.text)
    }
}


fun createLinearLayoutVertical(): JPanel {
    return JPanel()
        .apply {
            layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        }
}
