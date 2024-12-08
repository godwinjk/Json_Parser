package com.godwin.jsonparser.ui;

import com.godwin.jsonparser.ui.tab.IParserTabs;
import com.godwin.jsonparser.ui.tab.ParserTabsIml;
import com.google.common.collect.Sets;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * crea
 */
public class ParserWidget extends JPanel implements IParserWidget {
    private final Project mProject;
    private final Disposable mParent;
    private final JBPanel<JBPanel> mPanel;
    private IParserTabs mTabs;


    public ParserWidget(Project project, Disposable disposable) {
        super(new BorderLayout());
        mProject = project;
        mParent = disposable;
        mPanel = new JBPanel<>(new BorderLayout());
        mPanel.add(this, BorderLayout.CENTER);
    }


    private ParserTabsIml.DebuggerTabListener createTabsListener() {
        return new ParserTabsIml.DebuggerTabListener() {
            @Override
            public void onLast() {

            }
        };
    }


    private IParserTabs createTabPanel() {
        return new ParserTabsIml(mProject, mParent);
    }

    private void addTab(JComponent innerDebuggerWidget, IParserTabs tabs) {
        String uniqueName = generateUniqueName(tabs);
        tabs.addTab(innerDebuggerWidget, uniqueName);
    }

    private String generateUniqueName(IParserTabs tabs) {
        Set<String> names = Sets.newHashSet();
        for (int i = 0; i < tabs.getTabCount(); i++) {
            names.add(tabs.getTitleAt(i));
        }
        String suggestedName = "Parser";
        String newSdkName = suggestedName;
        int i = 0;
        while (names.contains(newSdkName)) {
            newSdkName = suggestedName + " (" + (++i) + ")";
        }
        return newSdkName;
    }

    @Override
    public void createParserSession() {
        JComponent innerDebuggerWidget = createInnerDebuggerWidget();
//
        setupTabs(innerDebuggerWidget);
    }

    private void setupTabs(JComponent nextComponent) {
        if (null == mTabs) {
            mTabs = createTabPanel();
            mTabs.addListener(createTabsListener());
            add(mTabs.getComponent(), BorderLayout.CENTER);
        }
        addTab(nextComponent, mTabs);
    }

    @Override
    public void closeCurrentParserSession() {
        if (mTabs != null)
            mTabs.closeCurrentTab();
    }

    @Override
    public int getTabCount() {
        return mTabs == null ? 0 : mTabs.getTabCount();
    }

    @Override
    public IParserTabs getTabs() {
        return mTabs;
    }

    private JComponent createInnerDebuggerWidget() {
        return new com.godwin.jsonparser.ui.forms.ParserWidget(mProject, mParent, this).getContainer();
    }

    @Override
    public JComponent getComponent() {
        return mPanel;
    }
}
