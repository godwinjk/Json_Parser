package com.godwin.jsonparser.generator_kt.jsontokotlin

import com.godwin.jsonparser.generator.jsontodart.utils.LogUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.godwin.jsonparser.generator_kt.jsontokotlin.feedback.PLUGIN_VERSION
import com.godwin.jsonparser.generator_kt.jsontokotlin.feedback.sendConfigInfo
import com.godwin.jsonparser.generator_kt.jsontokotlin.feedback.sendHistoryActionInfo
import com.godwin.jsonparser.generator_kt.jsontokotlin.feedback.sendHistoryExceptionInfo

/**
 *
 * Created by Seal.wu on 2017/8/21.
 */

class JsonToKotlinApplication : StartupActivity, DumbAware {

    override fun runActivity(project: Project) {

        LogUtil.i("init JSON To Kotlin Class version ==$PLUGIN_VERSION")

        Thread {
            try {
                sendConfigInfo()
                sendHistoryExceptionInfo()
                sendHistoryActionInfo()
            } catch (e: Exception) {
                LogUtil.e(e.message.toString(),e)
            }
        }.start()
    }
}
