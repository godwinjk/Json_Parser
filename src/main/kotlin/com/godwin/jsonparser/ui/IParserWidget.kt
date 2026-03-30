package com.godwin.jsonparser.ui

import com.godwin.jsonparser.ui.tabs.IParserTabs
import javax.swing.JComponent

interface IParserWidget {
    fun createParserSession()
    fun createParserSessionWithContent(json: String)
    fun closeCurrentParserSession()
    fun getTabCount(): Int
    fun getTabs(): IParserTabs?
    fun getComponent(): JComponent
}
