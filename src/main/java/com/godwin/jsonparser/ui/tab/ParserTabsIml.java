package com.godwin.jsonparser.ui.tab;

import com.godwin.jsonparser.common.Logger;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.ui.tabs.impl.JBEditorTabs;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 *
 */
public class ParserTabsIml implements com.godwin.jsonparser.ui.tab.IParserTabs {
    private JBEditorTabs mTabs;
    private DebuggerTabListener mListener;

    public ParserTabsIml(Project project, Disposable parent) {
        mTabs = new JBEditorTabs(project, ActionManager.getInstance(), IdeFocusManager.getInstance(project), parent);
        mTabs.addListener(createListener());
        mTabs.setTabDraggingEnabled(true);
    }

    @Override
    public IParserTabs addListener(DebuggerTabListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public IParserTabs addTab(JComponent component, String name) {
        TabInfo tabInfo = new TabInfo(component).setText(name);
        mTabs.addTab(tabInfo);
        setSelectComponent(tabInfo);
        return this;
    }

    private void setSelectComponent(TabInfo tabInfo) {
        mTabs.select(tabInfo, true);
    }

    @Override
    public int getTabCount() {
        return mTabs.getTabCount();
    }

    @Override
    public TabInfo getTabAt(int i) {
        return mTabs.getTabAt(i);
    }

    @Override
    public com.godwin.jsonparser.ui.tab.IParserTabs closeTab(int index) {
        if (index >= 0 && index < mTabs.getTabCount()) {
            mTabs.removeTab(mTabs.getTabAt(index));
        }
        return this;
    }

    @Override
    public IParserTabs closeCurrentTab() {
        mTabs.removeTab(mTabs.getSelectedInfo());
        return this;
    }

    @Override
    public JBEditorTabs getComponent() {
        return mTabs;
    }

    @Override
    public String getTitleAt(int i) {
        return getTabAt(i).getText();
    }

    @Override
    public JComponent getCurrentComponent() {
        return mTabs.getSelectedInfo().getComponent();
    }

    @NotNull
    private TabsListener createListener() {
        return new TabsListener() {
            @Override
            public void selectionChanged(TabInfo tabInfo, TabInfo tabInfo1) {
                try {
                    Logger.i("On before Tab selection change :"+tabInfo.getText()+" to "+tabInfo1.getText());
                }catch (Exception ignored){
                }
            }

            @Override
            public void beforeSelectionChanged(TabInfo tabInfo, TabInfo tabInfo1) {
                try {
                    Logger.i("On before Tab selection change :"+tabInfo.getText()+" to "+tabInfo1.getText());
                }catch (Exception ignored){
                }

            }

            @Override
            public void tabRemoved(@NotNull TabInfo tabInfo) {
                if (mListener != null && getTabCount() == 1) {
                    mListener.onLast();
                }
            }

            @Override
            public void tabsMoved() {

            }
        };
    }

    public interface DebuggerTabListener {
        void onLast();
    }
}
