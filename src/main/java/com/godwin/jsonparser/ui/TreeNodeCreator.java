package com.godwin.jsonparser.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Godwin on 4/21/2018 12:32 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class TreeNodeCreator {
    public static DefaultTreeModel getTreeModel(String jsonString) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");

        String[] lines = jsonString.split("\n");
        List<String> list = Arrays.asList(lines);
        ListIterator<String> iterator = list.listIterator();

        createNode(iterator, root);
        return new DefaultTreeModel(root);
    }

    private static DefaultMutableTreeNode createNode(ListIterator<String> iterator, DefaultMutableTreeNode rootNode) {

        if (!iterator.hasNext()) {
            return rootNode;
        }

        while (iterator.hasNext()) {
            String line = iterator.next();
            line = line.trim();
            if (line.contains("{") || line.contains("[")) {
                DefaultMutableTreeNode root = new DefaultMutableTreeNode(line);
                rootNode.add(createNode(iterator, root));
            } else {
                boolean isBreakable = true;
                while (true) {
                    DefaultMutableTreeNode dataNode = new DefaultMutableTreeNode(line);

                    if (line.contains("}") || line.contains("]")) {
                        rootNode.add(dataNode);
                        break;
                    } else if (line.contains("{") || line.contains("[")) {
                        iterator.previous();
                        isBreakable = false;
                        break;
                    } else {
                        rootNode.add(dataNode);
                        if (iterator.hasNext()) {
                            line = iterator.next();
                            line = line.trim();
                        } else {
                            break;
                        }
                    }
                }
                if (isBreakable)
                    break;
            }
        }
        return rootNode;
    }

}
