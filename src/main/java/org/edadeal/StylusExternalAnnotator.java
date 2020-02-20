package org.edadeal;

import com.intellij.lang.annotation.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.edadeal.utils.CreatePropertyQuickFix;
import org.jetbrains.annotations.NotNull;


import java.util.Collection;

public class StylusExternalAnnotator extends ExternalAnnotator<StylusExternalAnnotator.CollectedInfo, Collection<Error>> {
    static class CollectedInfo {
        final @NotNull Document document;
        final @NotNull PsiFile file;

        CollectedInfo(final @NotNull Document document, final @NotNull PsiFile file) {
            this.document = document;
            this.file = file;
        }
    }

    private static final Logger log = Logger.getInstance(ExternalAnnotator.class);

    public CollectedInfo collectInformation(@NotNull PsiFile file) {
        final VirtualFile vfile = file.getVirtualFile();

        if (vfile == null) {
            log.info("Missing vfile for " + file);
            return null;
        }

        // collect the document here because doAnnotate has no read access to the file document manager
        final Document document = FileDocumentManager.getInstance().getDocument(vfile);

        if (document == null) {
            log.info("Missing document");
            return null;
        }

        return new CollectedInfo(document, file);
    }

    public CollectedInfo collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
        if (isNotStylusFile(file)) {
            return null;
        }

        return collectInformation(file);
    }

    /**
     * @see ExternalAnnotator https://upsource.jetbrains.com/idea-ce/file/HEAD/platform/analysis-api/src/com/intellij/lang/annotation/ExternalAnnotator.java
     */
    public Collection<Error> doAnnotate(CollectedInfo collectedInfo) {
        PsiFile file = collectedInfo.file;
        if (isNotStylusFile(file)) {
            return null;
        }

        log.info("running Stylus Linter external annotator for " + collectedInfo);

        return TypeCheck.errors(collectedInfo.file, collectedInfo.document);
    }

    public void apply(@NotNull final PsiFile file, final Collection<Error> annotationResult, @NotNull final AnnotationHolder holder) {
        log.info("applying Stylus Linter external annotator results for " + file);

        System.out.println("External also work stlint");

        for (final Error error: annotationResult) {
            if (error.fix() != null) {
                holder.createErrorAnnotation(error.range(), error.message()).registerFix(
                        new CreatePropertyQuickFix(error.fix(), error.range())
                );
            } else {
                holder.createErrorAnnotation(error.range(), error.message());
            }
        }
    }

    private static boolean isNotStylusFile(PsiFile file) {
        return !file.getFileType().getDefaultExtension().equals("styl");
    }
}