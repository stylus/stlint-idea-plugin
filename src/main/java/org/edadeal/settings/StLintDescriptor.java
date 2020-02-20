package org.edadeal.settings;

import com.intellij.javascript.nodejs.PackageJsonData;
import com.intellij.lang.javascript.linter.JSLinterDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ContainerUtil;
import org.edadeal.StLintBundle;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class StLintDescriptor extends JSLinterDescriptor {
    public static final String PACKAGE_NAME = "stlint";

    @NotNull
    @Override
    public String getDisplayName() {
        return StLintBundle.message("settings.javascript.linters.stlint.configurable.name");
    }

    @Override
    public String packageName() {
        return PACKAGE_NAME;
    }

    @Override
    public boolean supportsMultipleRoots() {
        return true;
    }

    @Override
    public boolean hasConfigFiles(@NotNull Project project) {
        return StLintUtil.hasConfigFiles(project);
    }

    @Override
    public boolean enable(@NotNull Project project, Collection<PackageJsonData> packageJsonFiles) {
        // skip if there is typescript-tslint-plugin
        if (ContainerUtil.or(packageJsonFiles, data ->
                data.isDependencyOfAnyType(StLintUtil.TYPESCRIPT_PLUGIN_OLD_PACKAGE_NAME) ||
                        data.isDependencyOfAnyType(StLintUtil.TYPESCRIPT_PLUGIN_PACKAGE_NAME))) {
            return false;
        }
        return super.enable(project, packageJsonFiles);
    }

    @NotNull
    @Override
    public Class<StLintConfiguration> getConfigurationClass() {
        return StLintConfiguration.class;
    }
}
