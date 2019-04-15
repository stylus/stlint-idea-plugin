package org.edadeal.settings;

import com.intellij.javascript.nodejs.PackageJsonData;
import com.intellij.lang.javascript.linter.JSLinterConfiguration;
import com.intellij.lang.javascript.linter.JSLinterDescriptor;
import com.intellij.lang.javascript.linter.JSLinterGuesser;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.ContainerUtil;
import org.edadeal.StLintBundle;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

import static com.intellij.lang.javascript.linter.JSLinterConfigFileUtil.findDistinctConfigInContentRoots;


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

    @Override
    public void importSettings(@NotNull Project project, @NotNull JSLinterGuesser.EnableCase enableCase) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        VirtualFile config = findDistinctConfigInContentRoots(project, Arrays.asList(StLintUtil.CONFIG_FILE_NAMES));
        if (config == null) return;

        PsiFile file = PsiManager.getInstance(project).findFile(config);

        if (file == null) {
            return;
        }
    }

    @NotNull
    @Override
    public Class<? extends JSLinterConfiguration> getConfigurationClass() {
        return StLintConfiguration.class;
    }
}
