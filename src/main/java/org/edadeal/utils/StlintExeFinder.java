package org.edadeal.utils;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.edadeal.settings.StLintConfiguration;
import org.edadeal.settings.StLintState;

import java.io.File;

public class StlintExeFinder {
    private static final Logger log = Logger.getInstance(StlintExeFinder.class);

    static public String getPath(Project project) {
        StLintState state = StLintConfiguration.getInstance(project).getExtendedState().getState();
        String packagePath = state.getNodePackageRef().getConstantPackage().getSystemDependentPath();
        File packageFile = new File(packagePath);
        File exe = NodeFinder.resolvePath(
                packageFile.getParentFile().getAbsoluteFile(),
                ".bin",
                NodeFinder.getBinName("stlint")
        );

        if (exe.exists()) {
            return exe.getAbsolutePath();
        }

        return NodeFinder.getBinName("stlint");
    }
}
