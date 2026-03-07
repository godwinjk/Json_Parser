package com.godwin.jsonparser.ui.settings

import com.godwin.jsonparser.services.JsonPersistence
import com.intellij.openapi.options.Configurable
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

class JsonParserSettingsConfigurable : Configurable {
    private var analyticsCheckBox: JCheckBox? = null

    override fun getDisplayName() = "Json Parser"

    override fun createComponent(): JComponent {
        analyticsCheckBox = JCheckBox(
            "Enable analytics (helps improve the plugin)",
            JsonPersistence.Companion.getInstance().analyticsEnabled
        )
        return JPanel().apply {
            add(analyticsCheckBox)
        }
    }

    override fun isModified(): Boolean {
        return analyticsCheckBox?.isSelected != JsonPersistence.getInstance().analyticsEnabled
    }

    override fun apply() {
        JsonPersistence.getInstance().analyticsEnabled = analyticsCheckBox?.isSelected ?: true
    }

    override fun reset() {
        analyticsCheckBox?.isSelected = JsonPersistence.getInstance().analyticsEnabled
    }
}