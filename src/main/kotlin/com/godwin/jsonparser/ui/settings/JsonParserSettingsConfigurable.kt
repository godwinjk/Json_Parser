package com.godwin.jsonparser.ui.settings

import com.godwin.jsonparser.services.JsonPersistence
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

class JsonParserSettingsConfigurable : Configurable {
    private var analyticsCheckBox: JCheckBox? = null
    private var errorJsonTracking: JCheckBox? = null
    private var aiRepairEnabled: JCheckBox? = null

    override fun getDisplayName() = "Json Parser"

    override fun createComponent(): JComponent {
        analyticsCheckBox = JCheckBox(
            "Enable analytics (helps improve the plugin)",
            JsonPersistence.getInstance().analyticsEnabled
        )
        errorJsonTracking = JCheckBox(
            "Enable logging of json string when repair doesn't worked",
            JsonPersistence.getInstance().analyticsErrorJsonEnabled
        )
        aiRepairEnabled = JCheckBox(
            "Enable fixing your json using AI (We are not storing any of your json anywhere)",
            JsonPersistence.getInstance().repairUsingAi
        )

        val noteLabel =
            JBLabel("<html><i>Note: Your string will be logged when repair doesn't worked. You can disabled this anytime.</i></html>")

        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(analyticsCheckBox)
            add(errorJsonTracking)
            add(aiRepairEnabled)
            add(noteLabel)
        }
    }

    override fun isModified(): Boolean {
        return analyticsCheckBox?.isSelected != JsonPersistence.getInstance().analyticsEnabled
                || errorJsonTracking?.isSelected != JsonPersistence.getInstance().analyticsErrorJsonEnabled
                || aiRepairEnabled?.isSelected != JsonPersistence.getInstance().repairUsingAi
    }

    override fun apply() {
        JsonPersistence.getInstance().analyticsEnabled = analyticsCheckBox?.isSelected ?: true
        JsonPersistence.getInstance().analyticsErrorJsonEnabled = errorJsonTracking?.isSelected ?: true
        JsonPersistence.getInstance().repairUsingAi = aiRepairEnabled?.isSelected ?: true
    }

    override fun reset() {
        analyticsCheckBox?.isSelected = JsonPersistence.getInstance().analyticsEnabled
        errorJsonTracking?.isSelected = JsonPersistence.getInstance().analyticsErrorJsonEnabled
        aiRepairEnabled?.isSelected = JsonPersistence.getInstance().repairUsingAi
    }
}