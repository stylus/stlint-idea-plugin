package org.edadeal.utils;

import com.google.common.base.Joiner;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.EnvironmentUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NodeFinder {
    private static final Pattern NVM_NODE_DIR_NAME_PATTERN = Pattern.compile("^v?(\\d+)\\.(\\d+)\\.(\\d+)$");
    public static final String NODE_MODULES = "node_modules";

    private NodeFinder() {
    }

    public static String getBinName(String baseBinName) {
        return SystemInfo.isWindows ? baseBinName + ".cmd" : baseBinName;
    }

    @NotNull
    public static List<File> searchNodeModulesBin(String exeFileName) {
        Set<File> interpreters = ContainerUtil.newLinkedHashSet();
        List<File> fromPath = PathEnvironmentVariableUtil.findAllExeFilesInPath(exeFileName);
        List<File> nvmInterpreters = listNodeInterpretersFromNvm(exeFileName);
        List<File> brewInterpreters = listNodeInterpretersFromHomeBrew(exeFileName);
        interpreters.addAll(fromPath);
        interpreters.removeAll(nvmInterpreters);
        interpreters.removeAll(brewInterpreters);
        interpreters.addAll(nvmInterpreters);
        interpreters.addAll(brewInterpreters);
        return ContainerUtil.newArrayList(interpreters);
    }

    @NotNull
    public static List<File> searchAllScopesForBin(File projectRoot, String exeFileName) {
        List<File> globalJscsBin = searchNodeModulesBin(exeFileName);
        File file = resolvePath(projectRoot, NODE_MODULES, ".bin", exeFileName);
        if (file.exists()) {
            globalJscsBin.add(file);
        }

        return globalJscsBin;
    }

    public static File resolvePath(File root, @Nullable String first, @Nullable String second, String... rest) {
        String path = buildPath(first, second, rest);
        return new File(root, path);
    }

    public static String buildPath(@Nullable String first, @Nullable String second, String... rest) {
        return Joiner.on(File.separatorChar).join(first, second, (Object[]) rest);
    }

    @NotNull
    private static List<File> listNodeInterpretersFromNvm(String exeFileName) {
        String nvmDirPath = EnvironmentUtil.getValue("NVM_DIR");
        if (StringUtil.isEmpty(nvmDirPath)) {
            return Collections.emptyList();
        }
        File nvmDir = new File(nvmDirPath);
        if (nvmDir.isDirectory() && nvmDir.isAbsolute()) {
            return listNodeInterpretersFromVersionDir(nvmDir, exeFileName);
        }
        return Collections.emptyList();
    }

    private static List<File> listNodeInterpretersFromHomeBrew(String exeFileName) {
        return listNodeInterpretersFromVersionDir(new File("/usr/local/Cellar/node"), exeFileName);
    }

    private static List<File> listNodeInterpretersFromVersionDir(@NotNull File parentDir, String exeFileName) {
        if (!parentDir.isDirectory()) {
            return Collections.emptyList();
        }

        File[] dirs = parentDir.listFiles((dir, name) -> NodeFinder.structureNodeVersionStr(name) != null);

        if (dirs == null || dirs.length == 0) {
            return Collections.emptyList();
        }

        Arrays.sort(dirs, (dir1, dir2) -> {
            int[] v1 = NodeFinder.structureNodeVersionStr(dir1.getName());
            int[] v2 = NodeFinder.structureNodeVersionStr(dir2.getName());
            if (v1 != null && v2 != null) {
                for (int i = 0; i < v1.length; i++) {
                    if (i < v2.length) {
                        int cmp = v2[i] - v1[i];
                        if (cmp != 0) {
                            return cmp;
                        }
                    }
                }
            }
            return dir1.getName().compareTo(dir2.getName());
        });

        List<File> interpreters = ContainerUtil.newArrayListWithCapacity(dirs.length);

        for (File dir : dirs) {
            File interpreter = new File(dir, "bin" + File.separator + exeFileName);
            if (interpreter.isFile() && interpreter.canExecute()) {
                interpreters.add(interpreter);
            }
        }

        return interpreters;
    }

    @Nullable
    private static int[] structureNodeVersionStr(@NotNull String name) {
        Matcher matcher = NVM_NODE_DIR_NAME_PATTERN.matcher(name);
        if (matcher.matches() && matcher.groupCount() == 3) {
            try {
                return new int[]{Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3))};
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static File findExeInProjectBin(File projectRoot, String exe) {
        File file = NodeFinder.resolvePath(projectRoot, NodeFinder.NODE_MODULES, ".bin", exe);
        if (file.exists()) {
            return file;
        }
        return null;
    }
}