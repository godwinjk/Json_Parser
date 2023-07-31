package com.godwin.jsonparser.util;

import com.godwin.jsonparser.services.JsonPersistence;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

import static com.godwin.jsonparser.constants.ConstantsKt.TOOL_WINDOW_ID;

public class NotificationUtil {

    public static void showDonateNotification() {
        long now = System.currentTimeMillis();
        long tenDaysBack = now - (10 * 24 * 60 * 60 * 1000);
//        long tenDaysBack = now - 60*1000;
        long lastShownTime = JsonPersistence.Companion.getInstance().getJsonParserLastDisplayTime();
        if (lastShownTime < tenDaysBack) {
            JsonPersistence.Companion.getInstance().setJsonParserLastDisplayTime(now);
            Notifications.Bus.notify(
                    new Notification(
                            TOOL_WINDOW_ID,
                            "Like it",
                            "Like this plugin? <a href=https://paypal.me/godwinj>Donate</a> or <b>Give it a star</b>  <a href=https://plugins.jetbrains.com/plugin/10650-json-parser>Json Parser</a> and spread the word",
                            NotificationType.INFORMATION,
                            new NotificationListener.UrlOpeningListener(true)));
        }
    }
}
