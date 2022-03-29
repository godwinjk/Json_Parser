package com.godwin.jsonparser.ui.action;

import com.godwin.jsonparser.ui.IParserWidget;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class NewWindowAction extends AnAction {
    private final IParserWidget mParserWidget;
    private JFrame frame;

    public NewWindowAction(IParserWidget parserWidget) {
        super("Open in new window", "Open in new window", AllIcons.Actions.ChangeView);
        mParserWidget = parserWidget;

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        if (frame == null && mParserWidget != null) {
            frame = new JFrame();
            frame.setLayout(new BorderLayout());
            frame.add(mParserWidget.getComponent(), BorderLayout.CENTER);

            frame.setVisible(true);
        }
    }
}
