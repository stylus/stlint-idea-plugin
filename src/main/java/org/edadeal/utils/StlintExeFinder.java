package org.edadeal.utils;
import com.intellij.openapi.project.Project;
import org.edadeal.Settings;
import java.io.File;

public class StlintExeFinder {
    static public String findPath(Project project) {
        String path = Settings.readPath();

        if (new File(path).exists()) {
            return path;
        }

        String cwd = project.getBasePath();

        File exe = NodeFinder.resolvePath(new File(cwd), "node_modules", ".bin", NodeFinder.getBinName("stlint"));

        if (exe.exists()) {
            return exe.getAbsolutePath();
        }

        return NodeFinder.getBinName("stlint");
    }
}
