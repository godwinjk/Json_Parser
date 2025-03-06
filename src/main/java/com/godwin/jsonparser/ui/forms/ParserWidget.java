package com.godwin.jsonparser.ui.forms;

import com.godwin.jsonparser.rx.Publisher;
import com.godwin.jsonparser.rx.Subscriber;
import com.godwin.jsonparser.ui.IParserWidget;
import com.godwin.jsonparser.ui.dialog.OptionDialog;
import com.godwin.jsonparser.util.JsonDownloader;
import com.godwin.jsonparser.util.NotificationUtil;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.progress.util.DispatchThreadProgressWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Consumer;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.List;

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
    private JSplitPane splitPane;
    private JButton optionButton;

    private final Editor mInputEditor;

    private final Project mProject;
    private final Disposable mParent;

    private final ParserBodyWidget mBodyWidget;

    IParserWidget mParserWidget;

    public ParserWidget(Project project, Disposable disposable, IParserWidget parserWidget) {
        this.mProject = project;
        this.mParent = disposable;
        this.mParserWidget = parserWidget;

        this.mInputEditor = createEditor();

        this.mBodyWidget = new ParserBodyWidget(mProject, parserWidget);

        this.inputEditorContainer.add(mInputEditor.getComponent(), BorderLayout.CENTER);
        this.outputContainer.add(this.mBodyWidget.container, BorderLayout.CENTER);

        hideSplitPaneDivider();


        setEventListeners();
    }

    private Editor createEditor() {
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document doc = editorFactory.createDocument("");
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

        setPopupMenu(editor.getComponent());

        return editor;
    }
    /**
     * Hides the white divider line in JSplitPane when in dark mode.
     * This method:
     * 1. Applies a custom UI to the JSplitPane to remove the visible divider.
     * 2. Listens for IntelliJ Look and Feel (LaF) theme changes.
     * 3. Reapplies the custom UI whenever the theme is switched to ensure the divider stays hidden.
     * Fixes: <a href="https://github.com/godwinjk/Json_Parser/issues/12"</a>
     */
    private void hideSplitPaneDivider() {
        customizeSplitPane(); // Apply initial customization

         /*Subscribe to IntelliJ's Look and Feel (LaF) change events
         Without this, the white line appears when a user switches to light theme and back to dark theme*/
        ApplicationManager.getApplication().getMessageBus()
                .connect(mParent).subscribe(LafManagerListener.TOPIC, (LafManagerListener)
                        source -> SwingUtilities.invokeLater(this::customizeSplitPane));// Reapply fix when theme changes
    }

    /**
     * Customizes the JSplitPane to completely hide the divider.
     * This method overrides the default divider UI with a custom BasicSplitPaneDivider,
     * preventing it from drawing any visible lines, effectively making it invisible.
     */
    private void customizeSplitPane() {
        splitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        // Empty implementation to fully hide the divider
                    }
                };
            }
        });
    }



    private JPopupMenu setPopupMenu(JComponent component) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(createPasteFromClipboardMenuItem());
        popupMenu.add(createRetrieveContentFromHttpURLMenuItem());
        popupMenu.add(createLoadFromLocalFileMenu());

        component.setComponentPopupMenu(null);
        component.getActionMap().clear();
        component.setComponentPopupMenu(popupMenu);

        return popupMenu;
    }

    private void setEventListeners() {
        optionButton.addActionListener(e -> {

            List<String> options = List.of("Retrieve from URL", "Load from file");
            OptionDialog dialog = new OptionDialog(options);
            dialog.show();

            int selectedIndex = dialog.getSelectedIndex();
            if (selectedIndex == 0) {
                actionGetFromUrl();
            } else if (selectedIndex == 1) {
                actionChooseFile();
            }

        });
        parseButton.addActionListener(e -> {
            String jsonString = mInputEditor.getDocument().getText();
            showBody(jsonString);
            NotificationUtil.showDonateNotification();
        });
        mInputEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                if (e != null && !TextUtils.isEmpty(e.getDocument().getText())) {
//                    try {
//                        showBody(e.getDocument().getText())
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
            if (mParserWidget != null && mParserWidget.getTabs() != null) {
                if (mParserWidget.getTabs().getCurrentTab().getComponent() == container) {
                    WriteCommandAction.runWriteCommandAction(mProject, () -> mInputEditor.getDocument().setText(message));
                }
            } else {
                WriteCommandAction.runWriteCommandAction(mProject, () -> mInputEditor.getDocument().setText(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public JMenuItem createPasteFromClipboardMenuItem() {
        JMenuItem menuItem = new JMenuItem("Paste from clipboard");
        menuItem.addActionListener(e -> {
            actionPaste();
        });
        return menuItem;
    }

    public JMenuItem createRetrieveContentFromHttpURLMenuItem() {
        JMenuItem menuItem = new JMenuItem("Retrieve content from Http URL");
        menuItem.addActionListener(e -> {
            actionGetFromUrl();
        });
        return menuItem;
    }

    public JMenuItem createLoadFromLocalFileMenu() {
        JMenuItem menuItem = new JMenuItem("Load from local file");
        menuItem.addActionListener(e -> {
            actionChooseFile();

        });
        return menuItem;
    }


    private void actionPaste() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String clipboardText = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                ApplicationManager.getApplication().runWriteAction(() ->
                        mInputEditor.getDocument().setText(clipboardText));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void actionChooseFile() {
        FileChooser.chooseFile(
                new FileChooserDescriptor(true, false, false, false, false, false),
                mProject,
                null, new Consumer<VirtualFile>() {
                    @Override
                    public void consume(VirtualFile file) {
                        try {
                            String content = new String(file.contentsToByteArray()).replace("\r\n", "\n");
                            ApplicationManager.getApplication().runWriteAction(() ->
                                    mInputEditor.getDocument().setText(content));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    private void actionGetFromUrl() {
        String inputData = Messages.showMultilineInputDialog(mProject, "Retrieve Content from Http URL\n\nTip: Paste your header in NEXT LINE with a colon(:)", "URL", null, null, null);
        if (inputData != null && !inputData.isEmpty()) {
            DispatchThreadProgressWindow progressWindow = new DispatchThreadProgressWindow(false, mProject);
            progressWindow.setIndeterminate(true);
            progressWindow.setRunnable(() -> {
                try {
                    String data = JsonDownloader.getData(inputData);
                    ApplicationManager.getApplication().runWriteAction(() ->
                            mInputEditor.getDocument().setText(data));
                } finally {
                    progressWindow.stop();
                }
            });
            progressWindow.start();
        }
    }
}
