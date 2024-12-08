package com.godwin.jsonparser.util;

import com.godwin.jsonparser.services.JsonPersistence;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

import static com.godwin.jsonparser.constants.ConstantsKt.TOOL_WINDOW_ID;

public class NotificationUtil {

    public static void showDonateNotification() {
        long now = System.currentTimeMillis();
        long daysBack = now - (10 * 24 * 60 * 60 * 1000); //10 days back
        long starClicked = JsonPersistence.Companion.getInstance().getStarClicked();
        long donateClicked = JsonPersistence.Companion.getInstance().getDonateClicked();
        if (starClicked == 1 || donateClicked == 1) {
            daysBack = now - (30L * 24 * 60 * 60 * 1000); //30 days back
        }
        long lastShownTime = JsonPersistence.Companion.getInstance().getJsonParserLastDisplayTime();
        if (lastShownTime < daysBack) {
            JsonPersistence.Companion.getInstance().setJsonParserLastDisplayTime(now);
            JsonPersistence.Companion.getInstance().setStarClicked(0);
            JsonPersistence.Companion.getInstance().setDonateClicked(0);
            Notification notification = new Notification(
                    TOOL_WINDOW_ID,
                    "Love it?",
                    "Love this plugin? Donate or Give it a star and spread the word",
                    NotificationType.INFORMATION);
            notification.addAction(new NotificationAction("Donate") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                    BrowserUtil.browse(URI.create("https://paypal.me/godwinj"));
                    JsonPersistence.Companion.getInstance().setDonateClicked(1);
                }
            });
            notification.addAction(new NotificationAction("Give it a star") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                    BrowserUtil.browse(URI.create("https://plugins.jetbrains.com/plugin/10650-json-parser"));
                    JsonPersistence.Companion.getInstance().setStarClicked(1);
                }
            });
            Notifications.Bus.notify(notification);
        }
    }
}
