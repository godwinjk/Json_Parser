package com.godwin.jsonparser.ui.action;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Created by Godwin on 4/21/2018 12:32 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class JBRadioAction extends AnAction implements CustomComponentAction {
    private final ButtonGroup mButtonGroup;
    private String mActionCommand;
    private ActionListener mActionListener;
    private boolean selected;

//    public JBRadioAction(@Nullable String text) {
//        this(text, null);
//    }

    public JBRadioAction(@Nullable String text, ButtonGroup buttonGroup) {
        super(text);
        this.mButtonGroup = buttonGroup;
    }

    public JBRadioAction(String text, String actionCommand, ButtonGroup buttonGroup, ActionListener actionListener) {
        this(text, buttonGroup);
        this.mActionCommand = actionCommand;
        this.mActionListener = actionListener;
    }

    public JBRadioAction(String text, String actionCommand, ButtonGroup buttonGroup, ActionListener actionListener, boolean selected) {
        this(text, actionCommand, buttonGroup, actionListener);
        this.selected = selected;
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        return getComponent(presentation);
    }

    @NotNull
    private JRadioButton getComponent(Presentation presentation) {
        JRadioButton jRadioButton = new JRadioButton("");
        jRadioButton.addActionListener(e -> {
            JRadioButton radioButton = (JRadioButton) e.getSource();
            ActionToolbar actionToolbar = UIUtil.getParentOfType(ActionToolbar.class, radioButton);
            DataContext dataContext = actionToolbar != null ? actionToolbar.getToolbarDataContext() : DataManager.getInstance().getDataContext(radioButton);
            JBRadioAction.this.actionPerformed(AnActionEvent.createEvent(JBRadioAction.this, dataContext, null, "unknown", ActionUiKind.NONE, null));
            System.out.println("JBRadioAction.createCustomComponent");
            if (mActionListener != null) {
                mActionListener.actionPerformed(e);
            }
        });
        presentation.putClientProperty(Key.create("selected"), selected);
        this.updateCustomComponent(jRadioButton, presentation);
        return jRadioButton;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
    }

    protected void updateCustomComponent(JRadioButton radioButton, Presentation presentation) {
        radioButton.setText(presentation.getText());
        radioButton.setToolTipText(presentation.getDescription());
        radioButton.setMnemonic(presentation.getMnemonic());
        radioButton.setDisplayedMnemonicIndex(presentation.getDisplayedMnemonicIndex());
        radioButton.setSelected(Boolean.TRUE.equals(presentation.getClientProperty(Key.create("selected"))));
        radioButton.setEnabled(true);
        radioButton.setVisible(true);

        if (!StringUtil.isEmpty(mActionCommand)) {
            radioButton.setActionCommand(mActionCommand);
        }
        if (mButtonGroup != null) {
            mButtonGroup.add(radioButton);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(true);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
