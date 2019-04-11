package org.edadeal.settings;

import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
import com.intellij.javascript.nodejs.util.NodePackage;
import com.intellij.lang.javascript.linter.AutodetectLinterPackage;
import com.intellij.lang.javascript.linter.JSLinterConfigurable;
import com.intellij.lang.javascript.linter.JSLinterView;
import com.intellij.lang.javascript.linter.NewLinterView;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.util.text.SemVer;
import org.edadeal.StLintBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Irina.Chernushina on 6/3/2015.
 */
public class StLintConfigurable extends JSLinterConfigurable<StLintState> {
    @NonNls public static final String SETTINGS_JAVA_SCRIPT_LINTERS_STLINT = "settings.linters.stlint";

    public StLintConfigurable(@NotNull Project project) {
        super(project, StLintConfiguration.class, false);
    }

    public StLintConfigurable(@NotNull Project project, boolean fullModeDialog) {
        super(project, StLintConfiguration.class, fullModeDialog);
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
        return StLintBundle.message("settings.javascript.linters.tslint.configurable.name");
    }

    @Override
    public void apply() throws ConfigurationException {
        super.apply();
        final StLintState state = getExtendedState(StLintConfiguration.class).getState();
        NodePackage nodePackage = state.getNodePackageRef().getConstantPackage();
        if (nodePackage != null && !nodePackage.isEmptyPath()) {
            if (!checkPackageVersionForJs(nodePackage.getVersion())) {
                throw new ConfigurationException("Linting JavaScript is not supported for this version of TSLint.");
            }
        }
    }

    private static boolean checkPackageVersionForJs(@Nullable SemVer semVer) {
        return semVer != null && semVer.getMajor() >= 4;
    }

    private static class NewStLintView extends NewLinterView<StLintState> {

        private final StLintPanel myPanel;

        NewStLintView(Project project, String displayName, StLintPanel panel) {
            super(project, displayName, panel.createComponent(), "tslint.json");
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

