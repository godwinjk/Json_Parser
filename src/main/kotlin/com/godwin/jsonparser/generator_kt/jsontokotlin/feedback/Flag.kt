package com.godwin.jsonparser.generator_kt.jsontokotlin.feedback

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.test.TestConfig
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.extensions.PluginId

/**
 * Flag relative
 * Created by Godwin on 2024/12/20
 */

val PLUGIN_VERSION = if (TestConfig.isTestModel.not()) {
    PluginManager.getPlugin(PluginId.getId("wu.seal.tool.jsontokotlin"))?.version.toString()
} else "1.X"

val UUID = if (KotlinConfigManager.userUUID.isEmpty()) {
    val uuid = java.util.UUID.randomUUID().toString()
    KotlinConfigManager.userUUID = uuid
    uuid
} else KotlinConfigManager.userUUID


