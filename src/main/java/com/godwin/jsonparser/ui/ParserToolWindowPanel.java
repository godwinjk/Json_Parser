package com.godwin.jsonparser.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;

/**
 * Created by Godwin on 4/21/2018 12:32 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ParserToolWindowPanel extends SimpleToolWindowPanel {
    private PropertiesComponent myPropertiesComponent;
    private ToolWindow myWindow;

    public ParserToolWindowPanel(PropertiesComponent propertiesComponent, ToolWindow window) {
        super(false, true);
        myPropertiesComponent = propertiesComponent;
        myWindow = window;
    }
}
