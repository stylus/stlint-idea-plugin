package org.edadeal;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

public final class StLintBundle {

    public static String message(@NotNull String key, @NotNull Object... params) {
        return CommonBundle.message(getBundle(), key, params);
    }

    @NonNls
    public static final String BUNDLE = "org.edadeal.StLintBundle";
    private static Reference<ResourceBundle> ourBundle;

    @NonNls
    public static final String LOG_ID = "#org.edadeal";

    private StLintBundle() {
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = com.intellij.reference.SoftReference.dereference(ourBundle);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            ourBundle = new SoftReference<ResourceBundle>(bundle);
        }
        return bundle;
    }
}
