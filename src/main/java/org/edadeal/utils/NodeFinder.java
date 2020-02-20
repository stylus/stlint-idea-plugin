package org.edadeal.utils;

import com.google.common.base.Joiner;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.Nullable;

import java.io.File;


public final class NodeFinder {

    private NodeFinder() {
    }

    public static String getBinName(String baseBinName) {
        return SystemInfo.isWindows ? baseBinName + ".cmd" : baseBinName;
    }

    public static File resolvePath(File root, @Nullable String first, @Nullable String second, String... rest) {
        String path = buildPath(first, second, rest);

        return new File(root, path);
    }

    public static String buildPath(@Nullable String first, @Nullable String second, @Nullable String... rest) {
        assert rest != null;
        return Joiner.on(File.separatorChar).join(first, second, (Object) rest);
    }
}