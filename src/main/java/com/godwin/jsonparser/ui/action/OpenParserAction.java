package com.godwin.jsonparser.ui.action;

import com.godwin.jsonparser.ParserToolWindowFactory;
import com.godwin.jsonparser.common.Logger;
import com.godwin.jsonparser.rx.Subscriber;
import com.godwin.jsonparser.ui.IParserWidget;
import com.godwin.jsonparser.ui.ParserToolWindowPanel;
import com.godwin.jsonparser.ui.ParserWidget;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class OpenParserAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        try {
            Editor mEditor = anActionEvent.getData(PlatformDataKeys.EDITOR);
            if (null == mEditor) {
                Logger.i("Not in editor window");
                return;
            }

            SelectionModel model = mEditor.getSelectionModel();

            String selectText = model.getSelectedText();


            if (TextUtils.isEmpty(selectText)) {
                Logger.i("selectText == null");
                return;
            }
//            ActionManager.getInstance().getAction("Asd").actionPerformed(anActionEvent);


            ToolWindow window =ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow(ParserToolWindowFactory.TOOL_WINDOW_ID);
            window.activate(()->{
                Subscriber.INSTANCE.publishMessage(selectText);
            },true,true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Content createParserContentPanel(ToolWindow toolWindow, Project myProject) {
        toolWindow.setToHideOnEmptyContent(true);

        ParserToolWindowPanel panel = new ParserToolWindowPanel(PropertiesComponent.getInstance(myProject), toolWindow);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "Parser", false);

        IParserWidget debuggerWidget = createContent(content,myProject);
        ActionToolbar toolBar = createToolBar(debuggerWidget);

        panel.setToolbar(toolBar.getComponent());
        panel.setContent(debuggerWidget.getComponent());

        return content;
    }

    private IParserWidget createContent(Content content, Project myProject) {
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
}
