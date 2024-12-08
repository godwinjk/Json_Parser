package com.godwin.jsonparser.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Godwin on 4/21/2018 12:32 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class TreeNodeCreator {

    public static DefaultTreeModel getTreeModelFromMap(Object jsonMap) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
        if (jsonMap instanceof List) {
            if (!((List) jsonMap).isEmpty()) {
                root = new DefaultMutableTreeNode(" [" + ((List) jsonMap).size() + "]");
            }
            processList((List<Object>) jsonMap, root);
        } else if (jsonMap instanceof Map) {
            createNodeFromMap((Map<String, Object>) jsonMap, root);
        }
        return new DefaultTreeModel(root);
    }

    private static DefaultMutableTreeNode createNodeFromMap(Map<String, Object> jsonMap, DefaultMutableTreeNode rootNode) {
        if (null == jsonMap || jsonMap.isEmpty()) {
            rootNode.setUserObject("{0}");
            return rootNode;
        }

        Iterator<Map.Entry<String, Object>> entries = jsonMap.entrySet().iterator();
        String userObject = (String) rootNode.getUserObject();
        if (userObject == null) {
            userObject = "";
        }
        rootNode.setUserObject(userObject + " {" + jsonMap.size() + "}");
        while (entries.hasNext()) {
            Map.Entry<String, Object> item = entries.next();
            if (item.getValue() instanceof Map) {
                DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(item.getKey());
                Map<String, Object> subMap = (Map<String, Object>) item.getValue();
                createNodeFromMap(subMap, subNode);
                rootNode.add(subNode);
            } else if (item.getValue() instanceof List) {
                List<Object> list = (List<Object>) item.getValue();
                DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(item.getKey() + " [" + list.size() + "]");
                processList(list, subNode);
                rootNode.add(subNode);
            } else {
                DefaultMutableTreeNode subNode;
                if (null == item.getValue()) {
                    subNode = new DefaultMutableTreeNode(item.getKey() + ": null");
                } else {
                    subNode = new DefaultMutableTreeNode(item.getKey() + ": " + item.getValue());
                }
                rootNode.add(subNode);
            }
        }
        return rootNode;
    }

    private static void processList(List<Object> list, DefaultMutableTreeNode rootNode) {
        if (null == list || list.isEmpty()) return;

        for (Object obj : list) {
            DefaultMutableTreeNode child;
            if (null == obj) {
                child = new DefaultMutableTreeNode("null");
            } else {
                child = new DefaultMutableTreeNode();
                if (obj instanceof Map) {
                    Map<String, Object> subMap = (Map<String, Object>) obj;
                    createNodeFromMap(subMap, child);
                } else if (obj instanceof List) {
                    List<Object> subList = (List<Object>) obj;
                    child.setUserObject("[" + subList.size() + "]");
                    processList(subList, child);
                } else {
                    child.setUserObject(obj.toString());
                }
            }
            rootNode.add(child);
        }
    }
}
