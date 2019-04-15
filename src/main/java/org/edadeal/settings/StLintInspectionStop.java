package org.edadeal.settings;

import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.javascript.JSBundle;
import com.intellij.lang.javascript.linter.JSLinterInspection;
import com.intellij.lang.javascript.linter.JSLinterWithInspectionExternalAnnotator;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.edadeal.StLintBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;


public final class StLintInspectionStop extends JSLinterInspection {
    public boolean useSeverityFromConfigFile = true;

    @Override
    protected JSLinterWithInspectionExternalAnnotator getExternalAnnotatorForBatchInspection() {
        return null;
    }

    @NotNull
    @Override
    protected HighlightSeverity chooseSeverity(@NotNull HighlightSeverity fromError, @NotNull HighlightSeverity inspectionSeverity) {
        return useSeverityFromConfigFile ? fromError : inspectionSeverity;
    }

    @Override
    public JComponent createOptionsPanel() {
        return createOptionPanelForConfigFileOption("useSeverityFromConfigFile");
    }

    @NotNull
    @Override
    public SuppressQuickFix[] getBatchSuppressActions(@Nullable PsiElement element) {
        return SuppressQuickFix.EMPTY_ARRAY;
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return StLintBundle.message("stlint.framework.title");
    }

    @NotNull
    @Override
    public String getGroupDisplayName() {
        return JSBundle.message("typescript.inspection.group.name");
    }

    @NotNull
    @Override
    protected List<String> getSettingsPath() {
        return ContainerUtil.newArrayList(
                JSBundle.message("typescript.compiler.configurable.name"),
                getDisplayName()
        );
    }
}
