package com.godwin.jsonparser.services

import com.intellij.ide.util.PropertiesComponent

object JsonConfigManager {
    private const val INDENT_KEY = "jsonParser_common_indent"

    var indent: Int
        get() = PropertiesComponent.getInstance().getInt(
            INDENT_KEY, 4
        )
        set(value) = PropertiesComponent.getInstance().setValue(INDENT_KEY, value, 4)
}