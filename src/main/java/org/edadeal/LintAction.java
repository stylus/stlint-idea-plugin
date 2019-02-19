package org.edadeal;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import org.edadeal.utils.StylusLinterRunner;

import java.io.*;
import java.util.Arrays;

public class LintAction extends AnAction {
    private static final Logger log = Logger.getInstance(TypeCheck.class);
    public LintAction() {
        super("Hello");
    }

    public void actionPerformed(AnActionEvent e) {
        PsiFile pfile = e.getData(CommonDataKeys.PSI_FILE);
        final File file = new File("/Users/v-chupurnov/WebstormProjects/test/stylus/src/test.styl");

        final File workingDir = file.getParentFile();

        StylusLinterRunner.Result result = StylusLinterRunner.runLint(
            file.getParentFile().getAbsolutePath(),
            file.getAbsolutePath(),
            Settings.readPath(), ""
        );

        String output = result.output;

        if (!result.isOk) {
            output = result.errorOutput;
        }

        Project currentProject = e.getProject();
        String dlgTitle = e.getPresentation().getDescription();

        Messages.showMessageDialog(currentProject, output, dlgTitle, Messages.getInformationIcon());
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = editor.getCaretModel();
        e.getPresentation().setEnabledAndVisible(caretModel.getCurrentCaret().hasSelection());
    }
}