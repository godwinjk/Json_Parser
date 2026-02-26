package com.godwin.jsonparser.ui

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow

class ParserToolWindowPanel(
    private val propertiesComponent: PropertiesComponent,
    private val window: ToolWindow
) : SimpleToolWindowPanel(false, true)
