package com.godwin.jsonparser.ui.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.util.List;

public class OptionDialog extends DialogWrapper {

    private final JList<String> optionsList;
    private String selectedOption;
    private int selectedIndex = -1;

    public OptionDialog(List<String> options) {
        super(true); // true makes it modal
        setTitle("Select Actions");

        // Create a list to display the options
        optionsList = new JBList<>(options.toArray(new String[0]));
        optionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        optionsList.setSelectedIndex(0); // Optionally select the first item by default

        init(); // Initialize the dialog
    }

    @Override
    protected JComponent createCenterPanel() {
        // Add the list of options to the dialog
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Add some padding around the content
        panel.add(Box.createVerticalStrut(20)); // Top padding
        panel.add(optionsList);
        panel.add(Box.createVerticalStrut(20));
        return panel;
    }

    @Override
    protected void doOKAction() {
        // Get the selected option when the user presses OK
        selectedOption = optionsList.getSelectedValue();
        selectedIndex = optionsList.getSelectedIndex();
        super.doOKAction();
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}
