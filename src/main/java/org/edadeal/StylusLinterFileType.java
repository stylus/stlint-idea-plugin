package org.edadeal;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class StylusLinterFileType extends LanguageFileType {
    public static String STYLUS = "styl";

    public static final StylusLinterFileType INSTANCE = new StylusLinterFileType();

    private StylusLinterFileType() {
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
        return StylusLinterFileType.STYLUS;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return StylusLinterIcons.ICON;
    }
}