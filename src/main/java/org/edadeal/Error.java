package org.edadeal;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

public class Error {
    private final @NotNull String message;
    @NotNull String message() {
        return message;
    }

    private String fix = null;

    String fix() {
        return fix;
    }

    private final @NotNull TextRange range;
    @NotNull TextRange range() {
        return range;
    }

    public Error(final @NotNull String msg, final @NotNull TextRange textRange, String fix) {
        this.message = msg;
        this.fix = fix;
        this.range = textRange;
    }
    public Error(final @NotNull String msg, final @NotNull TextRange textRange) {
        this.message = msg;
        this.range = textRange;
    }
}