package system;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n {
    private static final Locale fallbackLocale = Locale.ENGLISH;

    // 10 - Localisation using resource bundle
    private static ResourceBundle getBundle() {
        try {
            return ResourceBundle.getBundle("messages", Locale.getDefault());
        } catch (MissingResourceException e) {
            // If the specific locale is missing, fallback to default
            return ResourceBundle.getBundle("messages", fallbackLocale);
        }
    }

    public static String get(String key) {
        System.out.println("Current locale: " + Locale.getDefault());

        try {
            return getBundle().getString(key);
        } catch (MissingResourceException e) {
            // If the key is missing, return placeholder
            return STR."??\{key}??";
        }
    }
}
