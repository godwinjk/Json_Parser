package com.godwin.jsonparser.generator.jsontodart.utils

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.extensions.PluginId
import com.godwin.jsonparser.generator.jsontodart.ConfigManager
import com.godwin.jsonparser.generator.jsontodart.test.TestConfig

val PLUGIN_VERSION = if (TestConfig.isTestModel.not()){
    PluginManager.getPlugin(PluginId.getId("com.github.typ0520.jsontodart"))?.version.toString()
} else "1.X"

val UUID = if (ConfigManager.userUUID.isEmpty()) {
    val uuid = java.util.UUID.randomUUID().toString()
    ConfigManager.userUUID = uuid
    uuid
} else ConfigManager.userUUID


const val PLUGIN_NAME = "JSON To Kotlin Class"