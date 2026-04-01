package com.godwin.jsonparser.services

import com.godwin.jsonparser.action.*
import com.godwin.jsonparser.ui.IParserWidget
import com.godwin.jsonparser.ui.ParserMainPanel
import com.godwin.jsonparser.ui.ParserToolWindowPanel
import com.godwin.jsonparser.ui.tutorial.TutorialManager
import com.godwin.jsonparser.ui.tutorial.TutorialSteps
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import javax.swing.SwingConstants
import javax.swing.Timer

@Service(value = [Service.Level.PROJECT])
class ProjectService {
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
        val content = ContentFactory.getInstance().createContent(panel, "Parser", false)

        val debuggerWidget = createContent(content)
        val toolBar = createToolBar(debuggerWidget)
        panel.toolbar = toolBar.component
        panel.setContent(debuggerWidget.getComponent())
        return content
    }

    private fun createContent(content: Content): IParserWidget {
        val debuggerWidget = ParserMainPanel(project, content)
        debuggerWidget.restoreSessionsOrDefault()
        return debuggerWidget
    }

    private fun createToolBar(debuggerWidget: IParserWidget): ActionToolbar {
        val group = DefaultActionGroup()
        val addTab = AddTabAction(debuggerWidget)
        val closeTab = CloseTabAction(debuggerWidget)
        val loadFile = LoadFromFileAction(debuggerWidget)
        val loadUrl = LoadFromUrlAction(debuggerWidget)
        val generate = GenerateDummyJsonAction(debuggerWidget)
        val diff = DiffCheckerAction(debuggerWidget)
        val settings = ParserSettingsAction()
        val tutorial = StartTutorialAction()


        group.add(addTab)
        group.add(closeTab)
        group.add(Separator.getInstance())
        group.add(loadFile)
        group.add(loadUrl)
        group.add(Separator.getInstance())
        group.add(generate)
        group.add(Separator.getInstance())
        group.add(diff)
        group.add(Separator.getInstance())
        group.add(settings)
        group.add(Separator.getInstance())
        group.add(tutorial)
        val toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, group, false)
        toolbar.orientation = SwingConstants.VERTICAL
        // Store for tutorial component resolution
        javax.swing.SwingUtilities.invokeLater {
            TutorialSteps.toolbarComponent =
                toolbar.component
        }
        return toolbar
    }

}
