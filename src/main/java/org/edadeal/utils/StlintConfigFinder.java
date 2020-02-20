package org.edadeal.utils;

import com.intellij.openapi.project.Project;

import java.io.File;

public class StlintConfigFinder {
    static public String findPath(Project project, File workingDir) {
        String cwd = project.getBasePath();

        File localConfig = NodeFinder.resolvePath(workingDir, StylusLinterConfigFileType.STYLINTRC, "", "");

        if (localConfig.exists()) {
            return localConfig.getAbsolutePath();
        }

        assert cwd != null;
        File globalConfig = NodeFinder.resolvePath(new File(cwd), StylusLinterConfigFileType.STYLINTRC, "", "");

        if (globalConfig.exists()) {
            return globalConfig.getAbsolutePath();
        }

        return null;
    }
}
