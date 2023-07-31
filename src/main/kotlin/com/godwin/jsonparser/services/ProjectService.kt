package com.godwin.jsonparser.services

import com.godwin.jsonparser.MyBundle
import com.godwin.jsonparser.ui.IParserWidget
import com.godwin.jsonparser.ui.ParserToolWindowPanel
import com.godwin.jsonparser.ui.ParserWidget
import com.godwin.jsonparser.ui.action.AddTabAction
import com.godwin.jsonparser.ui.action.CloseTabAction
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import javax.swing.SwingConstants

@Service(value = [Service.Level.PROJECT])
class ProjectService() {
    lateinit var project: Project
    init {
        field = this
    }

    companion object {
        private lateinit var field: ProjectService
        fun init(project: Project): ProjectService {
            field = ProjectService()
            field.project = project
            return field
        }

        fun getInstance(): ProjectService {
            return field
        }
    }

    fun initParser(toolWindow: ToolWindow) {
        val content = createParserContentPanel(toolWindow)
        content.isCloseable = true
        toolWindow.contentManager.addContent(content)
    }

    private fun createParserContentPanel(toolWindow: ToolWindow): Content {
        toolWindow.setToHideOnEmptyContent(true)
        val panel = ParserToolWindowPanel(PropertiesComponent.getInstance(project), toolWindow)
        val content = ContentFactory.SERVICE.getInstance().createContent(panel, "Parser", false)
        val debuggerWidget = createContent(content)
        val toolBar = createToolBar(debuggerWidget)
        panel.toolbar = toolBar.component
        panel.setContent(debuggerWidget.component)
        return content
    }

    private fun createContent(content: Content): IParserWidget {
        val debuggerWidget: IParserWidget = ParserWidget(project, content)
        debuggerWidget.createParserSession()
        return debuggerWidget
    }

    private fun createToolBar(debuggerWidget: IParserWidget): ActionToolbar {
        val group = DefaultActionGroup()
        group.add(AddTabAction(debuggerWidget))
        group.add(CloseTabAction(debuggerWidget))
        //        group.add(new NewWindowAction(debuggerWidget));
        val toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, group, false)
        toolbar.setOrientation(SwingConstants.VERTICAL)
        return toolbar
    }
}
