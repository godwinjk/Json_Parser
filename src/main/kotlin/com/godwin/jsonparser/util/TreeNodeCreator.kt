package com.godwin.jsonparser.util

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

object TreeNodeCreator {
    fun getTreeModelFromMap(jsonMap: Any?): DefaultTreeModel {
        val root = DefaultMutableTreeNode("")
        when (jsonMap) {
            is List<*> -> {
                if (jsonMap.isNotEmpty()) {
                    root.userObject = " [${jsonMap.size}]"
                }
                processList(jsonMap, root)
            }

            is Map<*, *> -> createNodeFromMap(jsonMap as Map<String, Any?>, root)
        }
        return DefaultTreeModel(root)
    }

    private fun createNodeFromMap(jsonMap: Map<String, Any?>?, rootNode: DefaultMutableTreeNode) {
        if (jsonMap.isNullOrEmpty()) {
            rootNode.userObject = "{0}"
            return
        }

        val userObject = rootNode.userObject as? String ?: ""
        rootNode.userObject = "$userObject {${jsonMap.size}}"

        jsonMap.forEach { (key, value) ->
            val subNode = when (value) {
                is Map<*, *> -> DefaultMutableTreeNode(key).apply {
                    createNodeFromMap(value as Map<String, Any?>, this)
                }

                is List<*> -> DefaultMutableTreeNode("$key [${value.size}]").apply {
                    processList(value, this)
                }

                else -> DefaultMutableTreeNode("$key: ${value ?: "null"}")
            }
            rootNode.add(subNode)
        }
    }

    private fun processList(list: List<*>?, rootNode: DefaultMutableTreeNode) {
        if (list.isNullOrEmpty()) return

        list.forEach { obj ->
            val child = when (obj) {
                null -> DefaultMutableTreeNode("null")
                is Map<*, *> -> DefaultMutableTreeNode().apply {
                    createNodeFromMap(obj as Map<String, Any?>, this)
                }

                is List<*> -> DefaultMutableTreeNode("[${obj.size}]").apply {
                    processList(obj, this)
                }

                else -> DefaultMutableTreeNode(obj.toString())
            }
            rootNode.add(child)
        }
    }
}
