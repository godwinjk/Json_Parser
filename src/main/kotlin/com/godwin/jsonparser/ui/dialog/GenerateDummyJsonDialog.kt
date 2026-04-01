package com.godwin.jsonparser.ui.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

class GenerateDummyJsonDialog(project: Project) : DialogWrapper(project) {

    private val objectRadio = JRadioButton("Object", true)
    private val arrayRadio = JRadioButton("Array")
    private val propertiesSpinner = JSpinner(SpinnerNumberModel(5, 1, 100, 1))
    private val depthSpinner = JSpinner(SpinnerNumberModel(1, 1, 10, 1))
    private val arraySizeSpinner = JSpinner(SpinnerNumberModel(5, 1, 500, 1))
    private val arraySizeLabel = JLabel("Array size:")

    init {
        title = "Generate Dummy JSON"
        ButtonGroup().apply {
            add(objectRadio)
            add(arrayRadio)
        }
        objectRadio.addActionListener { updateArraySizeVisibility() }
        arrayRadio.addActionListener { updateArraySizeVisibility() }
        updateArraySizeVisibility()
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            insets = Insets(6, 8, 6, 8)
            anchor = GridBagConstraints.WEST
            fill = GridBagConstraints.HORIZONTAL
        }

        fun row(label: JComponent, field: JComponent, row: Int) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0
            panel.add(label, gbc)
            gbc.gridx = 1; gbc.weightx = 1.0
            panel.add(field, gbc)
        }

        // Type row
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2
        panel.add(JLabel("Type:"), gbc)
        gbc.gridy = 1
        val typePanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(objectRadio)
            add(Box.createHorizontalStrut(12))
            add(arrayRadio)
        }
        panel.add(typePanel, gbc)
        gbc.gridwidth = 1

        row(JLabel("Properties per object (1–100):"), propertiesSpinner, 2)
        row(JLabel("Nesting depth (1–10):"), depthSpinner, 3)
        row(arraySizeLabel, arraySizeSpinner, 4)

        return panel
    }

    private fun updateArraySizeVisibility() {
        val visible = arrayRadio.isSelected
        arraySizeLabel.isVisible = visible
        arraySizeSpinner.isVisible = visible
    }

    val isArray get() = arrayRadio.isSelected
    val propertyCount get() = propertiesSpinner.value as Int
    val depth get() = depthSpinner.value as Int
    val arraySize get() = arraySizeSpinner.value as Int
}
