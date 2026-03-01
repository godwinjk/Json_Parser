package com.godwin.jsonparser.generator.jsontokotlin.interceptor


import com.godwin.jsonparser.generator.common.ui.jCheckBox
import com.godwin.jsonparser.generator.common.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct.KotlinClass
import javax.swing.JPanel


/**
 * Not used for Kotlin data class, only used for global config
 */
object AnalyticsSwitchSupport : com.godwin.jsonparser.generator.jsontokotlin.extensions.Extension() {

    const val configKey = "wu.seal.analytics_switch"

    override fun createUI(): JPanel {
        return jHorizontalLinearLayout {
            jCheckBox("Enable anonymous analytic", getConfig(configKey).toBoolean(), { isSelected ->
                setConfig(
                    configKey, isSelected.toString()
                )
            })
            fillSpace()
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        return kotlinClass  //do nothing for kotlinClass
    }

    fun enableAnalytics(): Boolean {
        return getConfig(configKey).toBoolean()
    }
}