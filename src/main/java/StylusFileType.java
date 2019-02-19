import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class StylusFileType extends LanguageFileType {

    public static final StylusFileType INSTANCE = new StylusFileType();

    private StylusFileType() {
        super(StylusLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Stylus File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Stylus language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "styl";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return StylusIcons.ICON;
    }
}