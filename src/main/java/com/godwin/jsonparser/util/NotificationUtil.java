package com.godwin.jsonparser.util;

import com.godwin.jsonparser.services.JsonPersistence;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

import static com.godwin.jsonparser.constants.ConstantsKt.TOOL_WINDOW_ID;

public class NotificationUtil {

    public static void showDonateNotification() {
        long now = System.currentTimeMillis();
        long tenDaysBack = now - (10 * 24 * 60 * 60 * 1000);
//        long tenDaysBack = now - 60*1000;
        long lastShownTime = JsonPersistence.Companion.getInstance().getJsonParserLastDisplayTime();
        if ( lastShownTime < tenDaysBack) {
            JsonPersistence.Companion.getInstance().setJsonParserLastDisplayTime(now);
            Notification notification = new Notification(
                    TOOL_WINDOW_ID,
                    "Love it?",
                    "Love this plugin? Donate or Give it a star and spread the word",
                    NotificationType.INFORMATION);
            notification.addAction(new NotificationAction("Donate") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                    BrowserUtil.browse(URI.create("https://paypal.me/godwinj"));
                }
            });
            notification.addAction(new NotificationAction("Give it a star") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                    BrowserUtil.browse(URI.create("https://plugins.jetbrains.com/plugin/10650-json-parser"));
                }
            });
            Notifications.Bus.notify(notification
            );
        }
    }
}
