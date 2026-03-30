package com.godwin.jsonparser.ui

import com.godwin.jsonparser.rx.Subscriber
import com.godwin.jsonparser.ui.tabs.IParserTabs
import com.godwin.jsonparser.ui.tabs.ParserTabsImpl
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBPanel
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class ParserMainPanel(
    private val project: Project,
    private val parent: Disposable
) : JPanel(BorderLayout()), IParserWidget {

    private val panel = JBPanel<JBPanel<*>>(BorderLayout()).apply {
        add(this@ParserMainPanel, BorderLayout.CENTER)
    }

    private var tabs: IParserTabs? = null

    override fun createParserSession() {
        val innerWidget = ParserWidget(project, parent, this).container
        setupTabs(innerWidget)
    }

    override fun createParserSessionWithContent(json: String) {
        createParserSession()
        Subscriber.publishMessage(json)
    }

    private fun setupTabs(nextComponent: JComponent) {
        if (tabs == null) {
            tabs = ParserTabsImpl(project, parent).apply {
                addListener { }
            }
            add(tabs?.getComponent(), BorderLayout.CENTER)
        }
        tabs?.let { addTab(nextComponent, it) }
    }

    private fun addTab(innerWidget: JComponent, tabs: IParserTabs) {
        val uniqueName = generateUniqueName(tabs)
        tabs.addTab(innerWidget, uniqueName)
    }

    private fun generateUniqueName(tabs: IParserTabs): String {
        val names = (0 until tabs.getTabCount()).mapTo(mutableSetOf()) { tabs.getTitleAt(it) }
        var newName = "JSON"
        var i = 0
        while (newName in names) {
            newName = "JSON (${++i})"
        }
        return newName
    }

    override fun closeCurrentParserSession() {
        tabs?.closeCurrentTab()
    }

    override fun getTabCount() = tabs?.getTabCount() ?: 0

    override fun getTabs() = tabs

    override fun getComponent(): JComponent = panel
}
