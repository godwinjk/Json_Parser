package com.godwin.jsonparser.ui.forms;

import com.godwin.jsonparser.rx.Publisher;
import com.godwin.jsonparser.rx.Subscriber;
import com.godwin.jsonparser.ui.IParserWidget;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Godwin on 4/21/2018 12:32 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ParserWidget implements Publisher {
    private JPanel container;
    private JPanel inputContainer;
    private JPanel outputContainer;
    private JPanel inputEditorContainer;
    private JButton parseButton;

    private Editor mInputEditor;

    private Project mProject;
    private Disposable mParent;

    private ParserBodyWidget mBodyWidget;

    public ParserWidget(Project project, Disposable disposable, IParserWidget parserWidget) {
        this.mProject = project;
        this.mParent = disposable;

        this.mInputEditor = createEditor();

        this.mBodyWidget = new ParserBodyWidget(mProject,parserWidget);

        this.inputEditorContainer.add(mInputEditor.getComponent(), BorderLayout.CENTER);
        this.outputContainer.add(this.mBodyWidget.container, BorderLayout.CENTER);

        setEventListeners();
    }

    private Editor createEditor() {
        PsiFile myFile = null;
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document doc = myFile == null
                ? editorFactory.createDocument("")
                : PsiDocumentManager.getInstance(mProject).getDocument(myFile);
        Editor editor = editorFactory.createEditor(doc, mProject);
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setIndentGuidesShown(false);
        editorSettings.setFoldingOutlineShown(true);
        editorSettings.setAdditionalColumnsCount(3);
        editorSettings.setAdditionalLinesCount(3);
        editorSettings.setLineNumbersShown(true);
        editorSettings.setCaretRowShown(true);

//        ((EditorEx) editor).setHighlighter(createHighlighter(FileTypes.PLAIN_TEXT));
        return editor;
    }

    private void setEventListeners() {
        parseButton.addActionListener(e -> {
            String jsonString = mInputEditor.getDocument().getText();
            showBody(jsonString);
        });
        mInputEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                if (e != null && !TextUtils.isEmpty(e.getDocument().getText())) {
//                    try {
//                        showBody(e.getDocument().getText());
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
                }
            }
        });

        Subscriber.INSTANCE.add(this);
    }

    private void showBody(String jsonString) {
        mBodyWidget.showPretty(jsonString);
        mBodyWidget.showRaw(jsonString);
        mBodyWidget.showTree(jsonString);

    }

    public JPanel getContainer() {
        return container;
    }

    @Override
    public void onMessage(@NotNull String message) {
        try {
            WriteCommandAction.runWriteCommandAction(mProject, () -> mInputEditor.getDocument().setText(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
