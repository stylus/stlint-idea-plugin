package org.edadeal.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(
    name = "org.edadeal.STLintProjectComponent",
    storages = @Storage("stLintPlugin.xml")
)
public class Settings implements PersistentStateComponent<Settings> {
    public String stLintConfigFile = "";
    public String stLintExecutable = "";
    public String nodeInterpreter  = "";

    public boolean treatAllIssuesAsWarnings;
    public boolean pluginEnabled;


    protected Project project;

    public static Settings getInstance(Project project) {
        Settings settings = ServiceManager.getService(project, Settings.class);
        settings.project = project;
        return settings;
    }

    @Nullable
    @Override
    public Settings getState() {
        return this;
    }

    @Override
    public void loadState(Settings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getVersion() {
        return stLintExecutable + stLintConfigFile + treatAllIssuesAsWarnings;
    }
}