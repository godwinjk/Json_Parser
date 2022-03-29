package com.godwin.jsonparser.ui.action;

import com.godwin.jsonparser.common.Logger;
import com.godwin.jsonparser.util.EditorHintsNotifier;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.NlsActions;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class CopyToClipBoardAction extends AnAction {
    public CopyToClipBoardAction(@Nullable @NlsActions.ActionText String text, @Nullable @NlsActions.ActionDescription String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            Editor mEditor = e.getData(PlatformDataKeys.EDITOR);
            if (null == mEditor) {
                Logger.i("Not in editor window");
                Notifications.Bus.notifyAndHide(new Notification(
                        "Json Parser",
                        "Text not copied",
                        "Please select text to copy",
                        NotificationType.ERROR));
                return;
            }

            SelectionModel model = mEditor.getSelectionModel();
            String selectText = model.getSelectedText();

            if (TextUtils.isEmpty(selectText)) {
                Logger.i("selectText == null");
                EditorHintsNotifier.notifyError(mEditor,"Nothing selected (ctrl+A to select all)",0);
                return;
            }
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(selectText), null);
            EditorHintsNotifier.notifyInfo(mEditor,"Text copied");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
