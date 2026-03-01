package com.godwin.jsonparser.util

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType

object EditorHintsNotifier {

    private fun notify(editor: Editor, message: String, position: Long, notifier: () -> Unit) {
        if (message.isBlank()) return

        if (position in 0..editor.document.textLength) {
            editor.caretModel.moveToOffset(position.toInt())
        }

        editor.scrollingModel.apply {
            scrollToCaret(ScrollType.MAKE_VISIBLE)
            runActionOnScrollingFinished(notifier)
        }
    }

    fun notifyInfo(editor: Editor, message: String) {
        notify(editor, message, -1) {
            HintManager.getInstance().showInformationHint(editor, message)
        }
    }

    fun notifyError(editor: Editor, message: String, position: Long) {
        notify(editor, message, position) {
            HintManager.getInstance().showErrorHint(editor, message)
        }
    }
}
