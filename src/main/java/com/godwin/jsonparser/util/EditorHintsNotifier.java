package com.godwin.jsonparser.util;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.ScrollingModel;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class EditorHintsNotifier {

    private EditorHintsNotifier() {
    }

    private static void notify(@NotNull Editor editor, @NotNull String message, long position, @NotNull Runnable notifier) {
        if (StringUtils.isBlank(message)) {
            return;
        }

        if (position <= editor.getDocument().getTextLength() && position >= 0) {
            // move caret to position
            editor.getCaretModel().moveToOffset((int) position);
        }

        // scroll to caret
        ScrollingModel scrollingModel = editor.getScrollingModel();
        scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE);
        scrollingModel.runActionOnScrollingFinished(notifier);
    }

    public static void notifyInfo(@NotNull Editor editor, @NotNull String message)  {
        notify(editor, message, -1, () -> HintManager.getInstance().showInformationHint(editor, message));
    }

    public static void notifyError(@NotNull Editor editor, @NotNull String message, long position)  {
        notify(editor, message, position, () -> HintManager.getInstance().showErrorHint(editor, message));
    }
}