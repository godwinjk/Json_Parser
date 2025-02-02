package com.godwin.jsonparser.generator.jsontodart.utils

import com.godwin.jsonparser.constants.NOTIFICATION_GROUP_ID
import com.godwin.jsonparser.constants.PLUGIN_NAME
import com.godwin.jsonparser.constants.TOOL_WINDOW_ID
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project

/**
 * File contains functions which simply other function's invoke
 * Created by Godwin on 2024/12/20.
 */


/**
 * do the action that could be roll-back
 */
fun executeCouldRollBackAction(project: Project?, action: (Project?) -> Unit) {
    CommandProcessor.getInstance().executeCommand(project, {
        ApplicationManager.getApplication().runWriteAction {
            action.invoke(project)
        }
    }, "InsertClass", "JsonToDartOrKotlin")
}

/**
 * get the indent when generate kotlin class code
 */
fun getIndent(): String {

    return buildString {

        for (i in 1..DartConfigManager.indent) {
            append(" ")
        }
    }
}

/**
 * get class string block list from a big string which contains classes
 */
fun getClassesStringList(classesString: String): List<String> {
    return classesString.split("\n\n").filter { it.isNotBlank() }
}

/**
 * export the class name from class block string
 */
fun getClassNameFromClassBlockString(classBlockString: String): String {
    return classBlockString.substringAfter("class").substringBefore("{").trim()
    //throw IllegalStateException("cannot find class name in classBlockString: $classBlockString")
}

fun getFileNameFromClassBlockString(classBlockString: String): String {
    val className = getClassNameFromClassBlockString(classBlockString)
    if (DartConfigManager.isDartModelClassName) {
        return camelCaseToSnakeCase(className)
    }
    return className
    //throw IllegalStateException("cannot find class name in classBlockString: $classBlockString")
}

fun showNotify(notifyMessage: String, project: Project?) {

    val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP_ID)
    ApplicationManager.getApplication().invokeLater {
        val notification =
            notificationGroup?.createNotification(notifyMessage, NotificationType.INFORMATION) ?: Notification(
                TOOL_WINDOW_ID, PLUGIN_NAME, notifyMessage, NotificationType.INFORMATION
            )
        Notifications.Bus.notify(notification, project)

    }
}

fun <E> List<E>.firstIndexAfterSpecificIndex(element: E, afterIndex: Int): Int {
    forEachIndexed { index, e ->
        if (e == element && index > afterIndex) {
            return index
        }
    }
    return -1
}

fun getCommentCode(comment: String): String {
    return comment.replace(Regex("[\n\r]"), "")
}

fun camelCaseToSnakeCase(input: String): String {
    // Insert an underscore before each uppercase letter, then convert the entire string to lowercase
    return input.replace(Regex("([a-z0-9])([A-Z])"), "$1_$2").lowercase()
}
