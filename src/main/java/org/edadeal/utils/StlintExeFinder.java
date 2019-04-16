package org.edadeal.utils;
import com.intellij.javascript.nodejs.util.NodePackageRef;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.edadeal.settings.StLintState;

import java.io.File;

public class StlintExeFinder {
    private static final Logger log = Logger.getInstance(StlintExeFinder.class);

    static public String getPath(Project project, StLintState state) {
        NodePackageRef pack = state.getNodePackageRef();

        String packagePath = null;

        if (pack.isConstant()) {
            try {
                packagePath = pack.getConstantPackage().getSystemDependentPath();
            } catch (NullPointerException e) {}
        }

        if (packagePath == null || packagePath.isEmpty()) {
            File tmpDir = NodeFinder.resolvePath(
                    new File(project.getBasePath()),
                    "node_modules",
                   "stlint"
            );

            if (tmpDir.exists()) {
                packagePath = tmpDir.getAbsolutePath();
            }
        }

        if (packagePath == null || packagePath.isEmpty()) {
            return null;
        }

        File packageFile = new File(packagePath);

        File exe = NodeFinder.resolvePath(
                packageFile.getParentFile().getAbsoluteFile(),
                ".bin",
                NodeFinder.getBinName("stlint")
        );

        if (exe.exists()) {
            return exe.getAbsolutePath();
        }

        return null;
    }
}
