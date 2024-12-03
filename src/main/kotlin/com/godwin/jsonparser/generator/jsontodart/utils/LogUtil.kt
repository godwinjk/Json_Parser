package com.godwin.jsonparser.generator.jsontodart.utils

import com.godwin.jsonparser.constants.PLUGIN_NAME
import com.godwin.jsonparser.generator.jsontodart.test.TestConfig
import com.intellij.openapi.diagnostic.LoggerRt
import java.util.logging.Logger

object LogUtil {

    fun i(info: String) {
        if (TestConfig.isTestModel) {
            Logger.getLogger(PLUGIN_NAME).info(info)
        } else {
            LoggerRt.getInstance(PLUGIN_NAME).info(info)
        }
    }

    fun w(warning: String) {
        if (TestConfig.isTestModel) {
            Logger.getLogger(PLUGIN_NAME).warning(warning)
        } else {
            LoggerRt.getInstance(PLUGIN_NAME).warn(warning)
        }
    }

    fun e(message: String, e: Throwable) {
        if (TestConfig.isTestModel) {
            e.printStackTrace()
        } else {
            LoggerRt.getInstance(PLUGIN_NAME).error(message, e)
        }
    }
}