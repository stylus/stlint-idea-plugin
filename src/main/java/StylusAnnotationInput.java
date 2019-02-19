import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;


class StylusAnnotationInput {
    public final Project project;

    final String fileContent;
    final EditorColorsScheme colorsScheme;
    final PsiFile psiFile;

    StylusAnnotationInput(Project project, PsiFile psiFile, String fileContent, EditorColorsScheme colorsScheme) {
        this.project = project;
        this.psiFile = psiFile;
        this.fileContent = fileContent;
        this.colorsScheme = colorsScheme;
    }
}
