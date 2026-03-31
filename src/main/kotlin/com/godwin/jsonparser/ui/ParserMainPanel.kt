package com.godwin.jsonparser.ui

import com.godwin.jsonparser.rx.Subscriber
import com.godwin.jsonparser.services.JsonPersistence
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
        val widget = ParserWidget(project, parent, this)
        setupTabs(widget.container)
    }

    override fun createParserSessionWithContent(json: String) {
        createParserSession()
        Subscriber.publishMessage(json)
    }

    fun restoreSessionsOrDefault() {
        val saved = try {
            JsonPersistence.getInstance().tabSessions.filter { it.isNotBlank() }
        } catch (_: Exception) { emptyList() }

        if (saved.isEmpty()) {
            createParserSession()
        } else {
            saved.forEach { json ->
                createParserSession()
                Subscriber.publishMessage(json)
            }
        }
    }

    fun removeSessionAt(index: Int) {
        try {
            val prefs = JsonPersistence.getInstance()
            if (index in prefs.tabSessions.indices) {
                prefs.tabSessions.removeAt(index)
            }
        } catch (_: Exception) {}
    }

    private fun setupTabs(nextComponent: JComponent) {
        if (tabs == null) {
            val impl = ParserTabsImpl(project, parent).apply {
                addListener { }
                removeSessionCallback = { index -> removeSessionAt(index) }
            }
            tabs = impl
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
            newName = "JSON ${++i}"
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
