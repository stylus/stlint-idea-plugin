package org.edadeal.utils;

import com.intellij.openapi.fileTypes.ExactFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class StylusLinterConfigFileTypeFactory extends FileTypeFactory {
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(StylusLinterConfigFileType.INSTANCE, new ExactFileNameMatcher(StylusLinterConfigFileType.STYLINTRC));
    }
}