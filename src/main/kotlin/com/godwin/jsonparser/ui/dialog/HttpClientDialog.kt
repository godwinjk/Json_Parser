package com.godwin.jsonparser.ui.dialog

import com.godwin.jsonparser.ui.components.CircularProgress
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.awt.Dimension
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import javax.swing.*
import javax.swing.table.DefaultTableModel

class HttpClientDialog(
    private val project: Project,
    private val onResult: (String) -> Unit
) : JFrame("HTTP Client") {

    private val urlField = JTextField().apply { toolTipText = "https://..." }
    private val methodCombo = ComboBox(arrayOf("GET", "POST"))
    private val headersModel = DefaultTableModel(arrayOf("Key", "Value"), 0)
    private val headersTable = JBTable(headersModel)
    private val bodyArea = JTextArea(5, 40).apply { lineWrap = true }
    private val bodyPanel = JPanel(BorderLayout())
    private val statusLabel = JLabel(" ")
    private val spinner = CircularProgress().apply {
        preferredSize = Dimension(18, 18)
        isVisible = false
    }
    private val sendButton = JButton("Send")

    init {
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setSize(600, 500)
        setLocationRelativeTo(null)
        isResizable = true
        contentPane = buildUI()

        methodCombo.addActionListener {
            bodyPanel.isVisible = methodCombo.selectedItem == "POST"
            revalidate()
        }
        bodyPanel.isVisible = false

        sendButton.addActionListener { sendRequest() }
    }

    private fun buildUI(): JPanel {
        return JPanel(BorderLayout(8, 8)).apply {
            border = BorderFactory.createEmptyBorder(12, 12, 12, 12)

            // Top: URL + method
            add(JPanel(BorderLayout(6, 0)).apply {
                add(JLabel("Method:"), BorderLayout.WEST)
                add(methodCombo.apply { preferredSize = Dimension(80, 28) }, BorderLayout.CENTER)
                    
                val top = JPanel(BorderLayout(6, 0))
                top.add(methodCombo.apply { maximumSize = Dimension(80, 28) }, BorderLayout.WEST)
                top.add(urlField, BorderLayout.CENTER)
                add(top, BorderLayout.CENTER)
            }, BorderLayout.NORTH)

            // Center: headers + body
            add(JPanel(BorderLayout(0, 8)).apply {
                add(JPanel(BorderLayout()).apply {
                    add(JLabel("Headers:"), BorderLayout.NORTH)
                    add(JBScrollPane(headersTable).apply { preferredSize = Dimension(0, 120) }, BorderLayout.CENTER)
                    add(JPanel().apply {
                        add(JButton("Add").apply { addActionListener { headersModel.addRow(arrayOf("", "")) } })
                        add(JButton("Remove").apply {
                            addActionListener {
                                val row = headersTable.selectedRow
                                if (row >= 0) headersModel.removeRow(row)
                            }
                        })
                    }, BorderLayout.SOUTH)
                }, BorderLayout.NORTH)

                bodyPanel.apply {
                    add(JLabel("Body:"), BorderLayout.NORTH)
                    add(JBScrollPane(bodyArea), BorderLayout.CENTER)
                }
                add(bodyPanel, BorderLayout.CENTER)
            }, BorderLayout.CENTER)

            // Bottom: send + status
            add(JPanel(BorderLayout(8, 0)).apply {
                add(JPanel().apply {
                    add(sendButton)
                    add(spinner)
                }, BorderLayout.WEST)
                add(statusLabel, BorderLayout.CENTER)
            }, BorderLayout.SOUTH)
        }
    }

    private fun sendRequest() {
        val url = urlField.text.trim()
        if (url.isBlank()) {
            statusLabel.text = "URL is required"; return
        }

        setLoading(true)

        val method = methodCombo.selectedItem as String
        val client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build()

        val bodyPublisher = if (method == "POST") {
            HttpRequest.BodyPublishers.ofString(bodyArea.text)
        } else {
            HttpRequest.BodyPublishers.noBody()
        }

        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .method(method, bodyPublisher)
            .timeout(Duration.ofSeconds(30))

        // Add headers from table
        for (i in 0 until headersModel.rowCount) {
            val key = headersModel.getValueAt(i, 0)?.toString()?.trim() ?: continue
            val value = headersModel.getValueAt(i, 1)?.toString()?.trim() ?: ""
            if (key.isNotBlank()) requestBuilder.header(key, value)
        }

        client.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
            .whenComplete { response, error ->
                SwingUtilities.invokeLater {
                    setLoading(false)
                    when {
                        error != null -> statusLabel.text = "Error: ${error.message}"
                        else -> {
                            statusLabel.text = "Status: ${response.statusCode()}"
                            onResult(response.body())
                            dispose()
                        }
                    }
                }
            }
    }

    private fun setLoading(loading: Boolean) {
        sendButton.isEnabled = !loading
        spinner.isVisible = loading
        if (loading) spinner.start() else spinner.stop()
    }
}
