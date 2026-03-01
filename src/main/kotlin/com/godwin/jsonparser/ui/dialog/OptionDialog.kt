package com.godwin.jsonparser.ui.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import javax.swing.*

class OptionDialog(options: List<String>) : DialogWrapper(true) {
    private val optionsList = JBList(options).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        selectedIndex = 0
    }

    var selectedOption: String? = null
        private set
    var selectedIndex: Int = -1
        private set

    init {
        title = "Select Actions"
        init()
    }

    override fun createCenterPanel(): JComponent = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(Box.createVerticalStrut(20))
        add(optionsList)
        add(Box.createVerticalStrut(20))
    }

    override fun doOKAction() {
        selectedOption = optionsList.selectedValue
        selectedIndex = optionsList.selectedIndex
        super.doOKAction()
    }
}
