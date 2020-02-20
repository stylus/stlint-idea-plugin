package org.edadeal.utils;

import com.google.common.base.Joiner;
import com.intellij.openapi.util.SystemInfo;

import java.io.File;


public final class NodeFinder {
    private NodeFinder() {
    }

    public static String getBinName(String baseBinName) {
        return SystemInfo.isWindows ? baseBinName + ".cmd" : baseBinName;
    }

    public static File resolvePath(File root, String first, String second, String third) {
        String path = buildPath(first, second, third);

        if (path.endsWith(String.valueOf(File.separatorChar))) {
            path = path.substring(0, path.length() - 1);
        }

        return new File(root, path);
    }

    public static String buildPath(String first, String second, String third) {
        return Joiner.on(File.separatorChar).join(first, second, third);
    }
}