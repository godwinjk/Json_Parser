package com.godwin.jsonparser.ui.action;

import com.godwin.jsonparser.common.Logger;
import com.godwin.jsonparser.ui.IParserWidget;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by fingerart on 17/2/20.
 */
public class CloseTabAction extends AnAction {
    private final IParserWidget mParserWidget;

    public CloseTabAction(IParserWidget parserWidget) {
        super("Close Tab", "Close parser session", AllIcons.Actions.Cancel);
        mParserWidget = parserWidget;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        mParserWidget.closeCurrentParserSession();
        Logger.i("CloseTabAction.actionPerformed");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setVisible(mParserWidget.getTabCount() > 1);
    }
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}