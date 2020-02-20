package org.edadeal.utils;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;


public class CreatePropertyQuickFix extends BaseIntentionAction {
    private String fix;
    private TextRange range;
    public CreatePropertyQuickFix(String fix, TextRange range){
        this.fix = fix;
        this.range = range;
    }

    @NotNull
    @Override
    public String getText() {
        return "Fix";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Stylus fixes";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, PsiFile file) throws
            IncorrectOperationException {
        ApplicationManager.getApplication().invokeLater(() -> createProperty(project, editor));
    }

    private void createProperty(final Project project, final Editor editor) {
        final Document document = editor.getDocument();
        WriteCommandAction.writeCommandAction(project).run(() -> document.replaceString(range.getStartOffset(), range.getEndOffset(), this.fix));
    }
}
