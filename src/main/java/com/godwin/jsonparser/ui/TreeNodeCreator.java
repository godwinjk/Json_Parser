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

        createNode(iterator, root, 0);
        return new DefaultTreeModel(root);
    }

    private static DefaultMutableTreeNode createNode(ListIterator<String> iterator, DefaultMutableTreeNode rootNode, int count) {

        if (!iterator.hasNext()) {
            return rootNode;
        }

        while (iterator.hasNext()) {
            String line = iterator.next();
            line = line.trim();
            if (line.endsWith("{") || line.endsWith("[")) {
                String sub;
                if (line.length() > 3) {
                    sub = line.substring(0, line.length() - 3);
                } else {
                    sub = "" + count;
                }
                if (line.equals("{")) count++;
                else count = 0;
                DefaultMutableTreeNode root = new DefaultMutableTreeNode(sub);
                DefaultMutableTreeNode children = createNode(iterator, root, count);
                root.setUserObject(sub + "  {" + children.getChildCount() + "}");
                rootNode.add(children);
            } else {
                boolean isBreakable = true;
                while (true) {
                    if (line.endsWith("},") || line.endsWith("],") || line.endsWith("}") || line.endsWith("]")) {
                        if (line.length() > 2) {
                            String sub;
                            if(line.endsWith("}") || line.endsWith("]")){
                                sub = line;
                            }else{
                                sub = line.substring(0, line.length() - 2);
                            }

                            DefaultMutableTreeNode dataNode = new DefaultMutableTreeNode(sub);
                            rootNode.add(dataNode);
                        }
                        break;
                    } else if (line.endsWith("{") || line.endsWith("[")) {
                        iterator.previous();
                        isBreakable = false;
                        break;
                    } else {
                        DefaultMutableTreeNode dataNode = new DefaultMutableTreeNode(line);
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
