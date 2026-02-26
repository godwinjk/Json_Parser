package com.godwin.jsonparser.util

import com.godwin.jsonparser.constants.TOOL_WINDOW_ID
import com.godwin.jsonparser.services.JsonPersistence
import com.intellij.ide.BrowserUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnActionEvent
import java.net.URI

object NotificationUtil {

    fun showDonateNotification() {
        val now = System.currentTimeMillis()
        val persistence = JsonPersistence.getInstance()

        val daysBack = if (persistence.starClicked == 1 || persistence.donateClicked == 1) {
            now - (30L * 24 * 60 * 60 * 1000) // 30 days
        } else {
            now - (10L * 24 * 60 * 60 * 1000) // 10 days
        }

        val lastShownTime = persistence.jsonParserLastDisplayTime

        if (lastShownTime < daysBack) {
            persistence.apply {
                jsonParserLastDisplayTime = now
                starClicked = 0
                donateClicked = 0
            }

            val notification = Notification(
                TOOL_WINDOW_ID,
                "Love it?",
                "Love this plugin? Donate or Give it a star and spread the word",
                NotificationType.INFORMATION
            )

            notification.addAction(object : NotificationAction("Donate") {
                override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                    BrowserUtil.browse(URI.create("https://paypal.me/godwinj"))
                    persistence.donateClicked = 1
                }
            })

            notification.addAction(object : NotificationAction("Give it a star") {
                override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                    BrowserUtil.browse(URI.create("https://plugins.jetbrains.com/plugin/10650-json-parser"))
                    persistence.starClicked = 1
                }
            })

            Notifications.Bus.notify(notification)
        }
    }
}
