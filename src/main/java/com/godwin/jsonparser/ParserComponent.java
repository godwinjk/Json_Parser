package com.godwin.jsonparser;


import com.godwin.jsonparser.ui.IParserWidget;
import com.godwin.jsonparser.ui.ParserToolWindowPanel;
import com.godwin.jsonparser.ui.ParserWidget;
import com.godwin.jsonparser.ui.action.AddTabAction;
import com.godwin.jsonparser.ui.action.CloseTabAction;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;

/**
 * Created by Godwin on 4/21/2018 12:32 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ParserComponent implements ProjectComponent {
    private long time = 0;
    private boolean isShown = false;
    private final Project myProject;

    protected ParserComponent(Project project) {
        myProject = project;
    }

    public static ParserComponent getInstance(Project project) {
        return project.getComponent(ParserComponent.class);
    }

    public void initParser(ToolWindow toolWindow) {
        Content content = createParserContentPanel(toolWindow);
        content.setCloseable(true);
        toolWindow.getContentManager().addContent(content);
    }

    private Content createParserContentPanel(ToolWindow toolWindow) {
        toolWindow.setToHideOnEmptyContent(true);

        ParserToolWindowPanel panel = new ParserToolWindowPanel(PropertiesComponent.getInstance(myProject), toolWindow);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "Parser", false);

        IParserWidget debuggerWidget = createContent(content);
        ActionToolbar toolBar = createToolBar(debuggerWidget);

        panel.setToolbar(toolBar.getComponent());
        panel.setContent(debuggerWidget.getComponent());

        return content;
    }

    private IParserWidget createContent(Content content) {
        IParserWidget debuggerWidget = new ParserWidget(myProject, content);
        debuggerWidget.createParserSession();

        return debuggerWidget;
    }

    private ActionToolbar createToolBar(IParserWidget debuggerWidget) {
        DefaultActionGroup group = new DefaultActionGroup();

        group.add(new AddTabAction(debuggerWidget));
        group.add(new CloseTabAction(debuggerWidget));
//        group.add(new NewWindowAction(debuggerWidget));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, group, false);
        toolbar.setOrientation(SwingConstants.VERTICAL);
        return toolbar;
    }

//    private ToolWindowManagerListener getToolWindowListener() {
//        return new ToolWindowManagerListener() {
//            @Override
//            public void toolWindowRegistered(@NotNull String s) {
//                Logger.d("DebuggerComponent.toolWindowRegistered");
//            }
//
//            @Override
//            public void stateChanged() {
//                ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(ParserToolWindowFactory.TOOL_WINDOW_ID);
//                if (toolWindow != null) {
//                    if (toolWindow.isVisible() && toolWindow.getContentManager().getContentCount() == 0) {
//                        Logger.d("DebuggerComponent.isVisible ContentCount>0");
//                        initParser(toolWindow);
//                    } else if (!toolWindow.isVisible()) {
//                        if (!isShown && (time <= 0 || time < System.currentTimeMillis() - 3600000)) {
//                            isShown = true;
//                            time = System.currentTimeMillis();
//                            Notifications.Bus.notify(new Notification(
//                                    "Json Parser",
//                                    "Like it",
//                                    "Like this plugin? <a href=https://paypal.me/godwinj>Donate</a> or <b>Give it a star</b>  <a href=https://plugins.jetbrains.com/plugin/10650-json-parser>Json Parser</a> and spread the word",
//                                    NotificationType.INFORMATION,
//                                    new NotificationListener.UrlOpeningListener(true)));
//                        }
//                    }
//                }
//            }
//        };
//    }
}
