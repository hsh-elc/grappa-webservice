package de.hsh.grappa.lang;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


public class Str {
    static ResourceBundle bundle = PropertyResourceBundle.getBundle("messages");

    public static String get(String key) {
        return bundle.getString(key);
    }

    public static String get(String key, Object... params) {
        return String.format(bundle.getString(key), params);
    }
}
