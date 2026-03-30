package com.godwin.jsonparser.ui.dialog

import com.godwin.jsonparser.services.JsonPersistence
import com.intellij.openapi.ui.DialogWrapper
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

class ParserSettingsDialog : DialogWrapper(true) {

    private val prefs = JsonPersistence.getInstance()

    private val indentSpinner = JSpinner(SpinnerNumberModel(prefs.indentSize, 1, 8, 1))
    private val sortKeysCheckbox = JCheckBox("Sort keys alphabetically", prefs.sortKeys)

    init {
        title = "Parser Settings"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            insets = Insets(6, 8, 6, 8)
            anchor = GridBagConstraints.WEST
            fill = GridBagConstraints.HORIZONTAL
        }

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0
        panel.add(JLabel("Indentation size (spaces):"), gbc)
        gbc.gridx = 1; gbc.weightx = 1.0
        panel.add(indentSpinner, gbc)

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2
        panel.add(sortKeysCheckbox, gbc)

        return panel
    }

    override fun doOKAction() {
        prefs.indentSize = indentSpinner.value as Int
        prefs.sortKeys = sortKeysCheckbox.isSelected
        super.doOKAction()
    }
}
