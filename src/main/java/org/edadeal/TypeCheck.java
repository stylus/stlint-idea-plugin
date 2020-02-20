package org.edadeal;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.edadeal.settings.StLintConfiguration;
import org.edadeal.settings.StLintState;
import org.edadeal.utils.StlintConfigFinder;
import org.edadeal.utils.StlintExeFinder;
import org.edadeal.utils.StylusLinterRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class TypeCheck {
    private static final Logger log = Logger.getInstance(TypeCheck.class);

    private static final Collection<Error> noProblems = Collections.emptyList();
    private static final Collection<Suggest> noSuggest = Collections.emptyList();

    private static final class TypeDataParams {
        final public Project project;
        final public String exePath;
        final public String path;
        final public String text;

        public TypeDataParams(
                Project project,
                String exePath,
                String path,
                String text
        ) {
            this.text = text;
            this.path = path;
            this.exePath = exePath;
            this.project = project;
        }
    }

    static @Nullable TypeDataParams checkCommon(PsiFile file) {
        final VirtualFile vfile = file.getVirtualFile();

        if (vfile == null) {
            log.info("Missing vfile for " + file);
            return null;
        }

        final Project project = file.getProject();

        if (!StLintConfiguration.getInstance(project).isEnabled()) {
            return null;
        }

        final VirtualFile vparent = vfile.getParent();

        if (vparent == null) {
            log.error("missing vparent for " + file);
            return null;
        }

        final String path = vfile.getCanonicalPath();

        if (path == null) {
            log.error("missing canonical path for " + file);
            return null;
        }

        if (isNotStylusFile(path)) {
            return null;
        }

        final String dir = vparent.getCanonicalPath();

        if (dir == null) {
            log.error("missing canonical dir for " + file);
            return null;
        }

        final String text = file.getText();

        String exePath = StlintExeFinder.getPath(project, getState(project));

        if (exePath == null || exePath.isEmpty()) {
            log.error("StLint not installed");
            return null;
        }

        return new TypeDataParams(project, exePath, path, text);
    }


    static @NotNull Collection<Error> errors(PsiFile file, Document document) {

        log.debug("Stylus Linter checkFile", file);

        final TypeDataParams params = checkCommon(file);

        if (params == null) {
            return noProblems;
        }

        final String stylusOutput = stylusCheck(
                params.project,
                params.exePath,
                params.path,
                params.text,
                null, null, null
        );

        log.debug("stylus output", stylusOutput);

        if (stylusOutput.isEmpty()) {
            return noProblems;
        }

        Output.Response response = null;

        try {
            response = Output.parse(stylusOutput);
        } catch (Exception ignored) {
            log.error(stylusOutput);
        }

        if (response == null || response.passed) {
            log.info("stylus passed");
            return noProblems;
        }

        if (response.errors == null) {
            log.error("stylus failed, but shows no errors");
            return noProblems;
        }

        final Collection<Error> errors = new ArrayList<>();

        for (final Output.Error error: response.errors) {
            final ArrayList<Output.MessagePart> messageParts = error.message;

            if (messageParts == null || messageParts.size() == 0) {
                log.error("stylus missing message in error " + error);
                continue;
            }

            final Output.MessagePart firstPart = messageParts.get(0);


            if (pathIsNotEqual(params.path, firstPart.path)) {
                log.info("skip error because first message part path " + firstPart.path + " does not match file path " + params.path);
                continue;
            }

            final StringBuilder errorMessageBuilder = new StringBuilder(firstPart.descr);

            for (int i = 1; i < messageParts.size(); i++) {
                final Output.MessagePart messagePart = messageParts.get(i);
                if (messagePart.path == null || messagePart.path.isEmpty()) {
                    errorMessageBuilder.append(": ");
                } else {
                    errorMessageBuilder.append(" ");
                }
                errorMessageBuilder.append(messagePart.descr);
            }

            final String errorMessage = errorMessageBuilder.toString();

            log.info("Stylus found error: " + errorMessage);

            for (final Output.MessagePart part: error.message) {
                if (part.path.isEmpty()) {
                    // skip part of error message that has no file/line reference
                    continue;
                }
                if (pathIsNotEqual(params.path, part.path)) {
                    // skip part of error message that refers to content in another file
                    continue;
                }

                final int lineStartOffset = document.getLineStartOffset(remapLine(part.line, document));
                final int lineEndOffset = document.getLineStartOffset(remapLine(part.endline, document));

                log.info("Stylus error for file " + file + " at " + part.line + ":" + part.start + " to " + part.endline + ":" + part.end + " range " + TextRange.create(lineStartOffset + part.start - 1, lineEndOffset + part.end));

                errors.add(new Error(
                    errorMessage,
                    TextRange.create(lineStartOffset + part.start - 1, lineEndOffset + part.end),
                    (part.fix != null ? part.fix.replace : null)
                ));
            }
        }

        if (errors.isEmpty()) {
            return noProblems;
        } else {
            log.info("Stylus inspector found errors " + errors);
            return errors;
        }
    }

    static @NotNull Collection<Suggest> autoCompletes(PsiFile file, Integer offset, Integer line, String text) {

        log.debug("Stylus Linter autoCompletes", file);

        TypeDataParams params = checkCommon(file);

        if (params == null) {
            return noSuggest;
        }

        final String stylusOutput = stylusCheck(
                params.project,
                params.exePath,
                params.path,
                text,
                "autocomplete",
                offset,
                line
        );

        log.debug("stylus output", stylusOutput);

        if (stylusOutput.isEmpty()) {
            return noSuggest;
        }

        Output.Suggestions response = null;

        try {
            response = Output.parseSuggestions(stylusOutput);
        } catch (Exception ignored) {
            log.error(stylusOutput);
        }

        if (response == null || response.suggests == null) {
            return noSuggest;
        }

        return response.suggests;
    }

    private static boolean pathIsNotEqual(String path1, String path2) {
        String nPath1 = Paths.get(path1).toAbsolutePath().toString();
        String nPath2 = Paths.get(path2).toAbsolutePath().toString();

        return !nPath1.equals(nPath2) && !"-".equals(nPath2);
    }

    public static boolean isNotStylusFile(String path) {
        String extension = "";

        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i + 1);
        }

        return !extension.equals("styl");
    }

    private static int remapLine(int stylusLine, Document document) {
        final int lineIndex = stylusLine - 1;
        return Math.max(0, Math.min(lineIndex, document.getLineCount() - 1));
    }

    private static StLintState getState(Project project) {
        return StLintConfiguration.getInstance(project).getExtendedState().getState();
    }

    @NotNull
    private static String stylusCheck(
            Project project,
            @NotNull final String exePath,
            @NotNull final String filePath,
            @NotNull final String content,
            @Nullable final String command,
            @Nullable final Integer offset,
            @Nullable final Integer line
    ) {

        final File file = new File(filePath);

        final File workingDir = file.getParentFile();
        final String cwd = project.getBasePath() != null ? project.getBasePath() : workingDir.getAbsolutePath();

        log.debug("stylusCheck working directory", workingDir);

        StLintState state = getState(project);

        String configPath;
        try {
            assert state.getCustomConfigFilePath() != null;
            configPath = !state.getCustomConfigFilePath().isEmpty() ? state.getCustomConfigFilePath() : StlintConfigFinder.findPath(project, workingDir);
        } catch (NullPointerException e) {
            return "";
        }

        StylusLinterRunner.ExtraParams params = new StylusLinterRunner.ExtraParams();

        params.command = command;
        params.offset = offset;
        params.line = line;

        StylusLinterRunner.Result result = StylusLinterRunner.runLint(
               new StylusLinterRunner.Params(cwd,
                       file.getAbsolutePath(),
                       exePath,
                       configPath,
                       content,
                       params
               )
        );


        final String output = result.output;

        if (!result.isOk) {
            log.error("stylus output was empty.\nWorking directory: " + workingDir
                    + "\nFile: " + filePath
                    + "\nstderr: " + result.errorOutput);
        }

        return output;
    }
}