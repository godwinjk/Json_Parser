package com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor


import com.godwin.jsonparser.generator_kt.extensions.Extension
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jCheckBox
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jHorizontalLinearLayout
import javax.swing.JPanel


/**
 * Not used for Kotlin data class, only used for global config
 */
object AnalyticsSwitchSupport: Extension() {

    const val configKey = "wu.seal.analytics_switch"

    override fun createUI(): JPanel {
        return jHorizontalLinearLayout {
            jCheckBox("Enable anonymous analytic", getConfig(configKey).toBoolean(), { isSelected -> setConfig(
                configKey, isSelected.toString()) })
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