package org.edadeal;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.NotNull;

public class StLintProjectComponent implements ProjectComponent {
    protected Project project;
    protected Settings settings;

    private static final String PLUGIN_NAME = "Stylus Linter";

    public StLintProjectComponent(Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return StLintProjectComponent.class.getName();
    }

    void showInfoNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    private void showInfoNotification(String content, NotificationType type, NotificationListener notificationListener) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type, notificationListener);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    static void showNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type);
        Notifications.Bus.notify(errorNotification);
    }
}
