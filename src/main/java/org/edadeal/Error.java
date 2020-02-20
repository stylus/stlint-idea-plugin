package org.edadeal;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Error {
    @NotNull
    private final String message;

    @NotNull
    String message() {
        return message;
    }

    @Nullable
    private final String fix;

    @Nullable
    String fix() {
        return fix;
    }

    private final @NotNull
    TextRange range;

    @NotNull
    TextRange range() {
        return range;
    }

    public Error(final @NotNull String msg, final @NotNull TextRange textRange, @Nullable String fix) {
        this.message = msg;
        this.fix = fix;
        this.range = textRange;
    }
}