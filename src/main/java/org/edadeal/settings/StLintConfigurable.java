package org.edadeal.settings;

import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
import com.intellij.lang.javascript.linter.*;
import com.intellij.openapi.project.Project;
import org.edadeal.StLintBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;


public class StLintConfigurable extends JSLinterConfigurable<StLintState> {
    @NonNls public static final String SETTINGS_JAVA_SCRIPT_LINTERS_STLINT = "settings.javascript.linters.stlint";

    public StLintConfigurable(@NotNull Project project, Class<? extends JSLinterConfiguration<StLintState>> configurationClass, boolean fullModeDialog) {
        super(project, configurationClass, fullModeDialog);
    }

    @NotNull
    @Override
    protected JSLinterView<StLintState> createView() {
        return new NewStLintView(myProject, getDisplayName(), new StLintPanel(getProject(), isFullModeDialog(), false));
    }

    @NotNull
    @Override
    public String getId() {
        return SETTINGS_JAVA_SCRIPT_LINTERS_STLINT;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return StLintBundle.message("settings.javascript.linters.stlint.configurable.name");
    }

    private static class NewStLintView extends NewLinterView<StLintState> {

        private final StLintPanel myPanel;

        NewStLintView(Project project, String displayName, StLintPanel panel) {
            super(project, displayName, panel.createComponent(), "stlint.json");
            myPanel = panel;
        }

        @NotNull
        @Override
        protected StLintState getStateWithConfiguredAutomatically() {
            return StLintState.DEFAULT
                    .withInterpreterRef(NodeJsInterpreterRef.createProjectRef())
                    .withLinterPackage(AutodetectLinterPackage.INSTANCE);
        }

        @Override
        protected void handleEnabledStatusChanged(boolean enabled) {
            myPanel.handleEnableStatusChanged(enabled);
        }

        @Override
        protected void setState(@NotNull StLintState state) {
            myPanel.setState(state);
        }

        @NotNull
        @Override
        protected StLintState getState() {
            return myPanel.getState();
        }
    }
}

