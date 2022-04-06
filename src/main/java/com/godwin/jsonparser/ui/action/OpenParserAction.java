package com.godwin.jsonparser.ui.action;

import com.godwin.jsonparser.ParserToolWindowFactory;
import com.godwin.jsonparser.common.Logger;
import com.godwin.jsonparser.rx.Subscriber;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

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

            ToolWindow window =ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow(ParserToolWindowFactory.TOOL_WINDOW_ID);
            window.activate(()->{
                Subscriber.INSTANCE.publishMessage(selectText);
            },true,true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
