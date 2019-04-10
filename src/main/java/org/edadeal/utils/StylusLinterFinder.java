package org.edadeal.utils;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Set;

public final class StylusLinterFinder {
    @NotNull
    public static List<File> findAllStylusLinterExe(final File projectRoot) {
        Set<File> exes = ContainerUtil.newLinkedHashSet();
        List<File> fromPath = PathEnvironmentVariableUtil.findAllExeFilesInPath(NodeFinder.getBinName("StylusLinter"));
        exes.addAll(fromPath);
        return ContainerUtil.newArrayList(exes);
    }

    public static List<String> searchForLintConfigFiles(final File projectRoot) {
        FilenameFilter filter = (file, name) -> name.equals(StylusLinterConfigFileType.STYLINTRC);
        List<String> files = FileUtils.recursiveVisitor(projectRoot, filter);
        return ContainerUtil.map(files, curFile -> {
            return FileUtils.makeRelative(projectRoot, new File(curFile));
        });
    }
}