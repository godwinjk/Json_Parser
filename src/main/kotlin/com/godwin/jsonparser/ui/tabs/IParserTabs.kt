package com.godwin.jsonparser.ui.tabs

import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.impl.JBEditorTabs
import javax.swing.JComponent

interface IParserTabs {
    fun addListener(listener: ParserTabsImpl.DebuggerTabListener): IParserTabs
    fun addTab(component: JComponent, name: String): IParserTabs
    fun getTabCount(): Int
    fun getTabAt(i: Int): TabInfo
    fun closeTab(index: Int): IParserTabs
    fun closeCurrentTab(): IParserTabs
    fun getComponent(): JBEditorTabs
    fun getCurrentTab(): TabInfo?
    fun getTitleAt(i: Int): String
    fun getCurrentComponent(): JComponent?
}
