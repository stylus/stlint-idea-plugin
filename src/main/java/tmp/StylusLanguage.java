package tmp;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class StylusLanguage extends Language {
    public static final StylusLanguage INSTANCE = new StylusLanguage();

    private StylusLanguage() {
        super(StylusLanguage.INSTANCE, "Stylus", new String[]{"text/stylus"});
    }

    @NotNull
    public String getDeclarationsTerminator() {

        return "";
    }

    public boolean isIndentBased() {
        return true;
    }
}