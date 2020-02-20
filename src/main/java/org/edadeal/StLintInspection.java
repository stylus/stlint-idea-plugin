package org.edadeal;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.UnfairLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StLintInspection extends LocalInspectionTool implements BatchSuppressableTool, UnfairLocalInspectionTool {
    @NotNull
    public String getDisplayName() {
        return StLintBundle.message("settings.javascript.linters.stlint.configurable.name");
    }

    @NotNull
    public String getShortName() {
        return StLintBundle.message("settings.javascript.linters.stlint.configurable.short.name");
    }


    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        return ExternalAnnotatorInspectionVisitor.checkFileWithExternalAnnotator(file, manager, isOnTheFly, new StylusExternalAnnotator());
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new ExternalAnnotatorInspectionVisitor(holder, new StylusExternalAnnotator(), isOnTheFly);
    }


    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element) {
        return false;
    }

    @NotNull
    @Override
    public SuppressQuickFix[] getBatchSuppressActions(@Nullable PsiElement element) {
        return new SuppressQuickFix[0];
    }
}
