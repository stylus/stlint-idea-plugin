package org.edadeal.settings;

import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField;
import com.intellij.javascript.nodejs.util.NodePackageField;
import com.intellij.javascript.nodejs.util.NodePackageRef;
import com.intellij.lang.javascript.JSBundle;
import com.intellij.lang.javascript.linter.AutodetectLinterPackage;
import com.intellij.lang.javascript.linter.ui.JSLinterConfigFileTexts;
import com.intellij.lang.javascript.linter.ui.JSLinterConfigFileView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.SwingHelper;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collections;

public final class StLintPanel {
    private static final JSLinterConfigFileTexts CONFIG_TEXTS = getConfigTexts();

    private final JSLinterConfigFileView myConfigFileView;
    private final boolean myFullModeDialog;
    private final boolean myAddLeftIndent;
    private final NodeJsInterpreterField myNodeInterpreterField;
    private final NodePackageField myNodePackageField;

    public StLintPanel(@NotNull Project project, boolean fullModeDialog, boolean addLeftIndent) {
        myConfigFileView = new JSLinterConfigFileView(project, CONFIG_TEXTS, null);
        myFullModeDialog = fullModeDialog;
        myAddLeftIndent = addLeftIndent;
        myConfigFileView.setAdditionalConfigFilesProducer(() -> StLintUtil.findAllConfigsInScope(project));
        myNodeInterpreterField = new NodeJsInterpreterField(project, false);
        myNodePackageField = AutodetectLinterPackage.createNodePackageField(
                Collections.singletonList(StLintUtil.PACKAGE_NAME),
                myNodeInterpreterField, myConfigFileView
        );
    }


    @NotNull
    public JComponent createComponent() {

        final FormBuilder nodeFieldsWrapperBuilder = FormBuilder.createFormBuilder()
                .setHorizontalGap(UIUtil.DEFAULT_HGAP)
                .setVerticalGap(UIUtil.DEFAULT_VGAP);

        if (myAddLeftIndent) {
            nodeFieldsWrapperBuilder.setFormLeftIndent(UIUtil.DEFAULT_HGAP);
        }

        nodeFieldsWrapperBuilder.addLabeledComponent("&Node interpreter:", myNodeInterpreterField)
                .addLabeledComponent("StLint package:", myNodePackageField);

        FormBuilder builder = FormBuilder.createFormBuilder()
                .setHorizontalGap(UIUtil.DEFAULT_HGAP)
                .setVerticalGap(UIUtil.DEFAULT_VGAP);

        if (myAddLeftIndent) {
            builder.setFormLeftIndent(UIUtil.DEFAULT_HGAP);
        }

        JPanel panel = builder.addComponent(nodeFieldsWrapperBuilder.getPanel())
                .addComponent(myConfigFileView.getComponent())
                .addSeparator(4)
                .addVerticalGap(4)
                .getPanel();

        final JPanel centerPanel = SwingHelper.wrapWithHorizontalStretch(panel);

        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        return centerPanel;
    }

    public void handleEnableStatusChanged(boolean enabled) {
        NodePackageRef selectedRef = myNodePackageField.getSelectedRef();
        if (selectedRef == AutodetectLinterPackage.INSTANCE) {
            myConfigFileView.setEnabled(false);
        }

        myConfigFileView.onEnabledStateChanged(enabled);
    }

    @NotNull
    public StLintState getState() {
        final StLintState.Builder builder = new StLintState.Builder()
                .setNodePath(myNodeInterpreterField.getInterpreterRef())
                .setNodePackageRef(myNodePackageField.getSelectedRef())
                .setCustomConfigFileUsed(myConfigFileView.isCustomConfigFileUsed())
                .setCustomConfigFilePath(myConfigFileView.getCustomConfigFilePath());


        return builder.build();
    }

    public void setState(@NotNull StLintState state) {
        myNodeInterpreterField.setInterpreterRef(state.getInterpreterRef());
        myNodePackageField.setSelectedRef(state.getNodePackageRef());

        myConfigFileView.setCustomConfigFileUsed(state.isCustomConfigFileUsed());
        myConfigFileView.setCustomConfigFilePath(StringUtil.notNullize(state.getCustomConfigFilePath()));

        resizeOnSeparateDialog();
    }

    private void resizeOnSeparateDialog() {
        if (myFullModeDialog) {
            myNodeInterpreterField.setPreferredWidthToFitText();
            myConfigFileView.setPreferredWidthToComponents();
        }
    }

    private static JSLinterConfigFileTexts getConfigTexts() {
        return new JSLinterConfigFileTexts(
                JSBundle.message("javascript.linter.configurable.config.autoSearch.title"),
                "When linting a Stylus file, StLint looks for stlint.json or stlint.yaml " +
                        "starting from the file's folder and then moving up to the filesystem root" +
                        " or in the user's home directory.",
                "Select StLint configuration file (*.json|*.yaml)");
    }
}
