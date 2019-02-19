import com.intellij.lang.Language;

public class StylusLanguage extends Language {

    public static final StylusLanguage INSTANCE = new StylusLanguage();

    private StylusLanguage() {
        super("Stylus");
    }
}