package com.godwin.jsonparser.ui.tabs

import com.godwin.jsonparser.util.Log
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.ui.InplaceButton
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.TabsListener
import com.intellij.ui.tabs.impl.JBEditorTabs
import javax.swing.JComponent

class ParserTabsImpl(project: Project, parent: Disposable) : IParserTabs {
    private val tabs: JBEditorTabs = JBEditorTabs(project, parent).apply {
        setTabDraggingEnabled(true)
    }

    private var listener: DebuggerTabListener? = null
    var removeSessionCallback: ((Int) -> Unit)? = null

    init {
        tabs.addListener(createListener())
    }

    override fun addListener(listener: DebuggerTabListener): IParserTabs {
        this.listener = listener
        return this
    }

    override fun addTab(component: JComponent, name: String): IParserTabs {
        val tabInfo = TabInfo(component).setText(name)
        val closeAction = object : AnAction("Close", "Close tab", AllIcons.Actions.Close) {
            override fun actionPerformed(e: AnActionEvent) {
                if (tabs.tabCount > 1) tabs.removeTab(tabInfo)
            }
        }
        tabInfo.setTabLabelActions(
            DefaultActionGroup(closeAction),
            ActionPlaces.EDITOR_TAB
        )
        tabs.addTab(tabInfo)
        tabs.select(tabInfo, true)
        return this
    }

    override fun getTabCount() = tabs.tabCount

    override fun getTabAt(i: Int): TabInfo = tabs.getTabAt(i)

    override fun closeTab(index: Int): IParserTabs {
        if (index in 0 until tabs.tabCount) {
            tabs.removeTab(tabs.getTabAt(index))
        }
        return this
    }

    override fun closeCurrentTab(): IParserTabs {
        tabs.removeTab(tabs.selectedInfo)
        return this
    }

    override fun getComponent() = tabs

    override fun getCurrentTab(): TabInfo? = tabs.selectedInfo

    override fun getTitleAt(i: Int) = getTabAt(i).text

    override fun getCurrentComponent(): JComponent? = tabs.selectedInfo?.component

    private fun createListener() = object : TabsListener {
        override fun selectionChanged(oldTab: TabInfo?, newTab: TabInfo?) {
            try {
                Log.i("On Tab selection change: ${oldTab?.text} to ${newTab?.text}")
            } catch (ignored: Exception) {
            }
        }

        override fun beforeSelectionChanged(oldTab: TabInfo?, newTab: TabInfo?) {
            try {
                Log.i("On before Tab selection change: ${oldTab?.text} to ${newTab?.text}")
            } catch (ignored: Exception) {
            }
        }

        override fun tabRemoved(tabInfo: TabInfo) {
            val index = tabs.tabs.indexOf(tabInfo)
            if (listener != null && getTabCount() == 1) {
                listener?.onLast()
            }
            removeSessionCallback?.invoke(index)
        }

        override fun tabsMoved() {}
    }

    fun interface DebuggerTabListener {
        fun onLast()
    }
}
