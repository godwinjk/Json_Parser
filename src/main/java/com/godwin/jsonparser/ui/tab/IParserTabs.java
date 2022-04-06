package com.godwin.jsonparser.ui.tab;

import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBEditorTabs;

import javax.swing.*;

/**
 * Created by fingerart on 17/2/27.
 */
public interface IParserTabs {
    IParserTabs addListener(ParserTabsIml.DebuggerTabListener listener);

    IParserTabs addTab(JComponent component, String name);

    int getTabCount();

    TabInfo getTabAt(int i);

    IParserTabs closeTab(int index);

    IParserTabs closeCurrentTab();

    JBEditorTabs getComponent();

    TabInfo getCurrentTab();

    String getTitleAt(int i);

    JComponent getCurrentComponent();
}
