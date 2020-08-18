package org.edadeal.settings;

import com.intellij.javascript.nodejs.util.JSLinterPackage;
import com.intellij.lang.javascript.linter.JSLinterConfiguration;
import com.intellij.lang.javascript.linter.JSLinterInspection;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.edadeal.StLintInspection;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "StLintConfiguration", storages = @Storage("jsLinters/stlint.xml"))
public class StLintConfiguration extends JSLinterConfiguration<StLintState> {
    private static final String STLINT_ELEMENT_NAME = "stlint";
    private static final String IS_CUSTOM_CONFIG_FILE_USED_ATTRIBUTE_NAME = "use-custom-config-file";
    private static final String CUSTOM_CONFIG_FILE_PATH_ATTRIBUTE_NAME = "custom-config-file-path";

    private final JSLinterPackage myPackage;

    public StLintConfiguration(@NotNull Project project) {
        super(project);
        myPackage = new JSLinterPackage(project, "stlint", true);
    }

    @NotNull
    public static StLintConfiguration getInstance(@NotNull final Project project) {
        return JSLinterConfiguration.getInstance(project, StLintConfiguration.class);
    }

    @Override
    protected void savePrivateSettings(@NotNull StLintState state) {
        storeLinterLocalPaths(state);
    }

    @NotNull
    @Override
    protected StLintState loadPrivateSettings(@NotNull StLintState state) {
        StLintState.Builder builder = new StLintState.Builder(state);
        restoreLinterLocalPaths(builder);
        return builder.build();
    }

    @NotNull
    @Override
    protected Class getInspectionClass() {
        return StLintInspection.class;
    }

    @Nullable
    @Override
    protected Element toXml(@NotNull StLintState state) {
        final Element root = new Element(STLINT_ELEMENT_NAME);
        if (state.isCustomConfigFileUsed()) {
            root.setAttribute(IS_CUSTOM_CONFIG_FILE_USED_ATTRIBUTE_NAME, Boolean.TRUE.toString());
        }
        final String customConfigFilePath = state.getCustomConfigFilePath();
        if (!StringUtil.isEmptyOrSpaces(customConfigFilePath)) {
            root.setAttribute(CUSTOM_CONFIG_FILE_PATH_ATTRIBUTE_NAME, FileUtil.toSystemIndependentName(customConfigFilePath));
        }

        storeLinterLocalPaths(state);
        return root;
    }

    @NotNull
    @Override
    protected StLintState fromXml(@NotNull Element element) {
        final StLintState.Builder builder = new StLintState.Builder();

        builder.setCustomConfigFileUsed(Boolean.parseBoolean(element.getAttributeValue(IS_CUSTOM_CONFIG_FILE_USED_ATTRIBUTE_NAME)));
        String customConfigFilePath = StringUtil.notNullize(element.getAttributeValue(CUSTOM_CONFIG_FILE_PATH_ATTRIBUTE_NAME));
        builder.setCustomConfigFilePath(FileUtil.toSystemDependentName(customConfigFilePath));


        restoreLinterLocalPaths(builder);
        return builder.build();
    }

    private void restoreLinterLocalPaths(StLintState.Builder builder) {
        myPackage.readOrDetect();
        builder.setNodePath(myPackage.getInterpreter());
        builder.setNodePackageRef(myPackage.getPackage());
    }

    private void storeLinterLocalPaths(StLintState state) {
        myPackage.force(state.getInterpreterRef(), state.getNodePackageRef());
    }

    @NotNull
    @Override
    protected StLintState getDefaultState() {
        return StLintState.DEFAULT;
    }
}