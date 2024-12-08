package com.godwin.jsonparser.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "json_parser_conf", storages = [Storage(value = StoragePathMacros.NON_ROAMABLE_FILE)])
class JsonPersistence : PersistentStateComponent<JsonPersistence> {

    companion object {
        fun getInstance(): JsonPersistence {
            return ApplicationManager.getApplication().getService(JsonPersistence::class.java)
        }
    }

    var jsonParserLastDisplayTime: Long = 0
    var donateClicked: Int = 0
    var starClicked: Int = 0
    override fun getState(): JsonPersistence {
        return this
    }

    override fun loadState(state: JsonPersistence) {
        XmlSerializerUtil.copyBean(state, this)
    }
}