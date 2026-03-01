package com.godwin.jsonparser.util

import com.godwin.jsonparser.constants.PLUGIN_NAME
import com.intellij.openapi.diagnostic.LoggerRt

object Log {
    fun i(message: String?) = LoggerRt.getInstance(PLUGIN_NAME).info(message ?: "")
    fun e(message: String?) = LoggerRt.getInstance(PLUGIN_NAME).error(message ?: "")
    fun e(throwable: Throwable?) = throwable?.let { LoggerRt.getInstance(PLUGIN_NAME).error(it) }
}
