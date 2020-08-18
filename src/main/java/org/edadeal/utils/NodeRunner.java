package org.edadeal.utils;

import com.google.common.base.Charsets;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;


public final class NodeRunner {
    private NodeRunner() {
    }

    @NotNull
    public static ProcessOutput execute(@NotNull GeneralCommandLine commandLine, int timeoutInMilliseconds) throws ExecutionException {
        String command = commandLine.getCommandLineString();

        Process process = commandLine.createProcess();

        OSProcessHandler processHandler = new OSProcessHandler(process, command, Charsets.UTF_8);

        final ProcessOutput output = new ProcessOutput();

        processHandler.addProcessListener(new ProcessAdapter() {
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                if (outputType.equals(ProcessOutputTypes.STDERR)) {
                    output.appendStderr(event.getText());
                } else if (!outputType.equals(ProcessOutputTypes.SYSTEM)) {
                    output.appendStdout(event.getText());
                }
            }
        });

        processHandler.startNotify();

        if (processHandler.waitFor(timeoutInMilliseconds)) {
            output.setExitCode(processHandler.getExitCode());
        } else {
            processHandler.destroyProcess();
            output.setTimeout();
        }

        if (output.isTimeout()) {
            throw new ExecutionException("Command '" + commandLine.getCommandLineString() + "' is timed out.");
        }

        return output;
    }
}