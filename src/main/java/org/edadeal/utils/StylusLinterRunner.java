package org.edadeal.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public final class StylusLinterRunner {
    private StylusLinterRunner() {
    }

    private static final Logger log = Logger.getInstance(StylusLinterRunner.class);
    private static final int TIME_OUT = (int) TimeUnit.SECONDS.toMillis(120L);
    private static final int FILES_NOT_FOUND = 66;

    public static class ExtraParams {
        @Nullable
        public String command;

        @Nullable
        public Integer offset;

        @Nullable
        public Integer line;
    }

    public static class Params {
        @NotNull
        final String cwd;
        @NotNull
        final String file;
        @NotNull
        final String StylusLinterExe;
        @Nullable
        final String StylusLinterConfig;
        @Nullable
        final String content;
        @Nullable
        final ExtraParams params;

        public Params(
                @NotNull String cwd,
                @NotNull String file,
                @NotNull String StylusLinterExe,
                @Nullable String StylusLinterConfig,
                @Nullable String content,
                @Nullable ExtraParams params
        ) {
            this.cwd = cwd;
            this.file = file;
            this.StylusLinterExe = StylusLinterExe;
            this.StylusLinterConfig = StylusLinterConfig;
            this.content = content;
            this.params = params;
        }
    }

    public static Result runLint(@NotNull String cwd, @NotNull String file, @NotNull String StylusLinterExe) {
        return runLint(new Params(cwd, file, StylusLinterExe, null, null, null));
    }

    public static Result runLint(Params params) {
        Result result = new Result();

        try {
            ProcessOutput out = lint(params);
            result.errorOutput = out.getStderr();

            try {
                if (out.getExitCode() != FILES_NOT_FOUND) {
                    result.output = out.getStdout();
                    result.isOk = true;
                }
            } catch (Exception e) {
                log.error(out.getStdout());
                result.errorOutput = out.getStdout();
            }
        } catch (Exception e) {
            result.errorOutput = e.toString();
        }

        return result;
    }

    public static class Result {
        public boolean isOk = false;
        public String output;
        public String errorOutput;
    }

    @NotNull
    public static ProcessOutput lint(Params params) throws ExecutionException {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine
                .withCharset(StandardCharsets.UTF_8)
                .setWorkDirectory(params.cwd);

        commandLine.setExePath(params.StylusLinterExe);

        commandLine.addParameter(params.file);

        commandLine.addParameter("--reporter");
        commandLine.addParameter("json");

        if (StringUtils.isNotEmpty(params.StylusLinterConfig)) {
            commandLine.addParameter("--config");
            commandLine.addParameter(params.StylusLinterConfig);
        }

        if (StringUtils.isNotEmpty(params.content)) {
            commandLine.addParameter("--content");
            if (SystemUtils.IS_OS_WINDOWS) {
                String sep = "@n@";

                commandLine.addParameter(params.content
                        .replaceAll("\\r\\n", sep)
                        .replaceAll("\\n\\r", sep)
                        .replaceAll("[\\n\\r]", sep)
                );

                commandLine.addParameter("--newline");
                commandLine.addParameter(sep);
            } else {
                commandLine.addParameter(params.content);
            }
        }

        if (params.params != null) {
            if (StringUtils.isNotEmpty(params.params.command)) {
                commandLine.addParameter("--command");
                commandLine.addParameter(params.params.command);
            }

            if (params.params.offset != null) {
                commandLine.addParameter("--offset");
                commandLine.addParameter(params.params.offset + "");
            }

            if (params.params.line != null) {
                commandLine.addParameter("--offsetline");
                commandLine.addParameter(params.params.line + "");
            }
        }

        return NodeRunner.execute(commandLine, TIME_OUT);
    }
}