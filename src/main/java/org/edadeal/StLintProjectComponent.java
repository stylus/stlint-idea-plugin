package org.edadeal;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.NotNull;

public class StLintProjectComponent implements ProjectComponent {
    final protected Project project;

    public StLintProjectComponent(Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return StLintProjectComponent.class.getName();
    }

}
