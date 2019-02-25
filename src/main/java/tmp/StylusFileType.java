package tmp;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.edadeal.StylusLinterIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class StylusFileType extends LanguageFileType {
    static final StylusFileType INSTANCE = new StylusFileType();
    private static final String STYLINTRC_EXT = "styl";

    private StylusFileType() {
        super(StylusLanguage.INSTANCE);
    }

    @NotNull
    public String getName() {
        return "Stylus";
    }

    @NotNull
    public String getDescription() {
        return "Stylus configuration file";
    }

    @NotNull
    public String getDefaultExtension() {
        return STYLINTRC_EXT;
    }

    @NotNull
    public Icon getIcon() {
        return StylusLinterIcons.ICON;
    }
}