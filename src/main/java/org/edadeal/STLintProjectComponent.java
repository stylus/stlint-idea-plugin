package org.edadeal;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.edadeal.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.edadeal.settings.Settings;

public class STLintProjectComponent implements ProjectComponent {
    public static final String FIX_CONFIG_HREF = "\n<a href=\"#\">Fix Configuration</a>";
    protected Project project;
    protected Settings settings;
    protected boolean settingValidStatus;
    protected String settingValidVersion;
    protected String settingVersionLastShowNotification;

    private static final Logger LOG = Logger.getInstance(StLintBundle.LOG_ID);

    public String stylusLintConfigFile;
    public String stylusLintExecutable;
    public boolean treatAsWarnings;
    public boolean pluginEnabled;

    public static final String PLUGIN_NAME = "Stylus Linter";

    public STLintProjectComponent(Project project) {
        this.project = project;
        settings = Settings.getInstance(project);
    }

    @Override
    public void projectOpened() {
        if (isEnabled()) {
            isSettingsValid();
        }
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
        if (isEnabled()) {
            isSettingsValid();
        }
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return STLintProjectComponent.class.getName();
    }

    public boolean isEnabled() {
        return Settings.getInstance(project).pluginEnabled;
    }

    public boolean isSettingsValid() {
        if (!settings.getVersion().equals(settingValidVersion)) {
            validateSettings();
            settingValidVersion = settings.getVersion();
        }
        return settingValidStatus;
    }

    public void validateSettings() {
        settingValidStatus = isValid();
        if (!settingValidStatus) {
            return;
        }

//        if (StringUtil.isNotEmpty(settings.stylusLintExecutable)) {
//            File file = new File(project.getBasePath(), settings.stylusLintExecutable);
//            if (!file.exists()) {
//                showErrorConfigNotification(ESLintBundle.message("eslint.rules.dir.does.not.exist", file.toString()));
//                LOG.debug("Rules directory not found");
//                settingValidStatus = false;
//                return false;
//            }
//        }
        stylusLintExecutable = settings.stLintExecutable;
        stylusLintConfigFile = settings.stLintConfigFile;
        treatAsWarnings = settings.treatAllIssuesAsWarnings;
        pluginEnabled = settings.pluginEnabled;
    }

    public boolean isValid() {
        // do not validate if disabled
        if (!settings.pluginEnabled) {
            return true;
        }
//        boolean status = validateField("Node Interpreter", settings.nodeInterpreter, true, false, true);
//        if (!status) {
//            return false;
//        }
//        status = validateField("Rules", settings.rulesPath, false, true, false);
//        if (!status) {
//            return false;
//        }
        boolean status = validateField("Stylus Lint bin", settings.stLintExecutable, false, false, true);
        if (!status) {
            return false;
        }
        return true;
    }

    private boolean validateField(String fieldName, String value, boolean shouldBeAbsolute, boolean allowEmpty, boolean isFile) {
        FileUtils.ValidationStatus r = FileUtils.validateProjectPath(shouldBeAbsolute ? null : project, value, allowEmpty, isFile);

        if (r == FileUtils.ValidationStatus.IS_EMPTY && !allowEmpty) {
            String msg = StLintBundle.message("edadeal.path.is.empty", fieldName);
            validationFailed(msg);
            return false;
        }

        if (isFile) {
            if (r == FileUtils.ValidationStatus.NOT_A_FILE) {
                String msg = StLintBundle.message("stylus.file.is.not.a.file", fieldName, value);
                validationFailed(msg);
                return false;
            }
        } else {
            if (r == FileUtils.ValidationStatus.NOT_A_DIRECTORY) {
                String msg = StLintBundle.message("stylus.directory.is.not.a.dir", fieldName, value);
                validationFailed(msg);
                return false;
            }
        }

        if (r == FileUtils.ValidationStatus.DOES_NOT_EXIST) {
            String msg = StLintBundle.message("stylus.file.does.not.exist", fieldName, value);
            validationFailed(msg);
            return false;
        }

        return true;
    }

    private void validationFailed(String msg) {
        String errorMessage = msg + FIX_CONFIG_HREF;
        showInfoNotification(errorMessage, NotificationType.WARNING, null);
        LOG.debug(msg);
        settingValidStatus = false;
    }

    protected void showErrorConfigNotification(String content) {
        if (!settings.getVersion().equals(settingVersionLastShowNotification)) {
            settingVersionLastShowNotification = settings.getVersion();
            showInfoNotification(content, NotificationType.WARNING);
        }
    }

    public void showInfoNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    public void showInfoNotification(String content, NotificationType type, NotificationListener notificationListener) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type, notificationListener);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    public static void showNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type);
        Notifications.Bus.notify(errorNotification);
    }
}
