package org.edadeal.settings;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField;
import com.intellij.javascript.nodejs.util.NodePackageField;

import com.intellij.lang.javascript.JSBundle;
import com.intellij.lang.javascript.linter.AutodetectLinterPackage;
import com.intellij.lang.javascript.linter.ui.JSLinterConfigFileTexts;
import com.intellij.lang.javascript.linter.ui.JSLinterConfigFileView;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.ex.SingleConfigurableEditor;
import com.intellij.openapi.project.Project;

import com.intellij.psi.PsiManager;

import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.NotNullProducer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.SwingHelper;
import com.intellij.util.ui.UIUtil;

import com.intellij.webcore.packaging.PackagesNotificationPanel;
import org.apache.commons.lang.StringUtils;

import org.edadeal.STLintProjectComponent;
import org.edadeal.utils.StylusLinterFinder;
import org.edadeal.utils.StylusLinterRunner;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class STLintSettingsPage implements Configurable {
    private static final JSLinterConfigFileTexts CONFIG_TEXTS = getConfigTexts();
    private static final String FIX_IT = "Fix it";
    private static final String HOW_TO_USE_STYLUS_LINT = "How to Use Stylus Linter";
    private static final String HOW_TO_USE_LINK = "https://github.com/stylus/stlint-idea-plugin";
    private final Project project;

    private JCheckBox pluginEnabledCheckbox;
    private JPanel panel;
    private JPanel errorPanel;

    private TextFieldWithHistoryWithBrowseButton stLintConfigFile;

    private NodeJsInterpreterField nodeInterpreterField;
    private NodePackageField stLintExeField;

    private JSLinterConfigFileView myConfigFileView;

//    private final PackagesNotificationPanel packagesNotificationPanel;


    private JRadioButton searchForConfigInRadioButton;
    private JRadioButton useSpecificConfigRadioButton;
    private HyperlinkLabel usageLink;


    private JLabel StLintConfigFilePathLabel;
    private JLabel stLintExeLabel;
    private JLabel nodeInterpreterLabel;

    private JCheckBox treatAllIssuesCheckBox;
    private JLabel versionLabel;

    public STLintSettingsPage(@NotNull final Project project) {
        this.project = project;


        configSTLintConfigField();

        this.stLintExeField = AutodetectLinterPackage.createNodePackageField(ContainerUtil.list(new String[]{"stlint"}), this.nodeInterpreterField, this.myConfigFileView, false);


//        this.packagesNotificationPanel = new PackagesNotificationPanel();
//        errorPanel.add(this.packagesNotificationPanel.getComponent(), BorderLayout.CENTER);
    }

    private void addListeners() {
        useSpecificConfigRadioButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                stLintConfigFile.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        pluginEnabledCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean enabled = e.getStateChange() == ItemEvent.SELECTED;
                setEnabledState(enabled);
            }
        });
        DocumentAdapter docAdp = new DocumentAdapter() {
            protected void textChanged(DocumentEvent e) {
                updateLaterInEDT();
            }
        };

        stLintConfigFile.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
    }

    private void updateLaterInEDT() {
        UIUtil.invokeLaterIfNeeded(new Runnable() {
            public void run() {
                STLintSettingsPage.this.update();
            }
        });
    }

    private void update() {
        ApplicationManager.getApplication().assertIsDispatchThread();
//        validate();
    }

    private void setEnabledState(boolean enabled) {
        searchForConfigInRadioButton.setEnabled(enabled);
        useSpecificConfigRadioButton.setEnabled(enabled);

        stLintConfigFile.setEnabled(enabled && useSpecificConfigRadioButton.isSelected());
        stLintExeField.setEnabled(enabled);
        nodeInterpreterField.setEnabled(enabled);

        stLintExeLabel.setEnabled(enabled);
        StLintConfigFilePathLabel.setEnabled(enabled);
        nodeInterpreterLabel.setEnabled(enabled);


        treatAllIssuesCheckBox.setEnabled(enabled);
    }
//
//    private void validate() {
//        Validator validator = new Validator();
//        if (!ValidationUtils.validatePath(project, stLintExeField.getChildComponent().getText(), false)) {
//            validator.add(stLintExeField.getChildComponent().getTextEditor(), "Path to St Lint exe is invalid {{LINK}}", FIX_IT);
//        }
//        if (!ValidationUtils.validatePath(project, stLintConfigFile.getChildComponent().getText(), true)) {
//            validator.add(stLintConfigFile.getChildComponent().getTextEditor(), "Path to St Lint config is invalid {{LINK}}", FIX_IT); //Please correct path to
//        }
//        if (validator.hasErrors()) {
//            versionLabel.setText("n.a.");
//        } else {
//            updateVersion();
//        }
//        packagesNotificationPanel.processErrors(validator);
//    }

    private StylusLinterRunner.StylusLinterSettings settings;

    private void updateVersion() {
//        String stExe = stLintExeField.getSelected().toString();
//
//        if (settings != null &&
//                settings.StylusLinterExe.equals(stExe) &&
//                settings.cwd.equals(project.getBasePath())) {
//            return;
//        }
//
//        if (StringUtils.isEmpty(stExe)) {
//            return;
//        }
//
//        getVersion(stExe, project.getBasePath());
    }

    private void getVersion(String stExe, String cwd) {
        if (StringUtils.isEmpty(stExe)) {
            return;
        }

        settings = StylusLinterRunner.buildSettings(cwd, null, stExe, null);
        settings.StylusLinterExe = stExe;
        settings.cwd = cwd;

        try {
            versionLabel.setText(StylusLinterRunner.runVersion(settings));
        } catch (Exception e) {
            versionLabel.setText("error");
            e.printStackTrace();
        }
    }

    private void configSTLintConfigField() {
        TextFieldWithHistory textFieldWithHistory = stLintConfigFile.getChildComponent();
        textFieldWithHistory.setHistorySize(-1);
        textFieldWithHistory.setMinimumAndPreferredWidth(0);

        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, new NotNullProducer<List<String>>() {
            @NotNull
            public List<String> produce() {
                return StylusLinterFinder.searchForLintConfigFiles(getProjectPath());
            }
        });

        SwingHelper.installFileCompletionAndBrowseDialog(project, stLintConfigFile, "Select Stylus Linter Config", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }


    @Nls
    @Override
    public String getDisplayName() {
        return "Stylus Linter";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        loadSettings();

        addListeners();

        return panel;
    }

    @Override
    public boolean isModified() {
        return pluginEnabledCheckbox.isSelected() != getSettings().pluginEnabled
                || treatAllIssuesCheckBox.isSelected() != getSettings().treatAllIssuesAsWarnings
                || !getLintConfigFile().equals(getSettings().stLintConfigFile);
    }

    private String getLintConfigFile() {
        return useSpecificConfigRadioButton.isSelected() ? stLintConfigFile.getChildComponent().getText() : "";
    }

    @Override
    public void apply() throws ConfigurationException {
        saveSettings();
        PsiManager.getInstance(project).dropResolveCaches();
    }

    private void saveSettings() {
        Settings settings = getSettings();
        settings.pluginEnabled = pluginEnabledCheckbox.isSelected();
        settings.stLintExecutable = stLintExeField.getSelected().getSystemIndependentPath();
        settings.nodeInterpreter = nodeInterpreterField.getInterpreter().toString();
        settings.stLintConfigFile = getLintConfigFile();

        settings.treatAllIssuesAsWarnings = treatAllIssuesCheckBox.isSelected();
        project.getComponent(STLintProjectComponent.class).validateSettings();
        DaemonCodeAnalyzer.getInstance(project).restart();
    }

    private void loadSettings() {
        Settings settings = getSettings();
        pluginEnabledCheckbox.setSelected(settings.pluginEnabled);


        stLintConfigFile.getChildComponent().setText(settings.stLintConfigFile);
//        nodeInterpreterField.getChildComponent().setText(settings.nodeInterpreter);

        boolean hasConfig = StringUtils.isNotEmpty(settings.stLintConfigFile);
        searchForConfigInRadioButton.setSelected(!hasConfig);
        useSpecificConfigRadioButton.setSelected(hasConfig);
        stLintConfigFile.setEnabled(hasConfig);
        treatAllIssuesCheckBox.setSelected(settings.treatAllIssuesAsWarnings);
        setEnabledState(settings.pluginEnabled);
    }

    @Override
    public void reset() {
        loadSettings();
    }

    @Override
    public void disposeUIResources() {
    }

    protected Settings getSettings() {
        return Settings.getInstance(project);
    }

    private void createUIComponents() {
        usageLink = SwingHelper.createWebHyperlink(HOW_TO_USE_STYLUS_LINT, HOW_TO_USE_LINK);
        nodeInterpreterField = new NodeJsInterpreterField(project, false);

        myConfigFileView = new JSLinterConfigFileView(project, CONFIG_TEXTS, (FileType)null);

        stLintExeField = AutodetectLinterPackage.createNodePackageField(
                ContainerUtil.list(new String[]{"stlint"}),
                this.nodeInterpreterField, myConfigFileView, false
        );
    }

    public void showSettings() {
        String dimensionKey = ShowSettingsUtilImpl.createDimensionKey(this);
        SingleConfigurableEditor singleConfigurableEditor = new SingleConfigurableEditor(project, this, dimensionKey, false);
        singleConfigurableEditor.show();
    }

    private File getProjectPath() {
        if (project.isDefault()) {
            return null;
        }

        return new File(project.getBaseDir().getPath());
    }

    private static JSLinterConfigFileTexts getConfigTexts() {
        return new JSLinterConfigFileTexts(JSBundle.message("javascript.linter.configurable.config.autoSearch.title", new Object[0]), "When linting a TypeScript file, TSLint looks for tslint.json or tslint.yaml starting from the file's folder and then moving up to the filesystem root or in the user's home directory.", "Select TSLint configuration file (*.json|*.yaml)");
    }
}
