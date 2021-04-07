package com.mycompany.myapp.config;

import java.util.Arrays;
import java.util.List;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final List<String> AVAILABLE_LANGUAGES = Arrays.asList("en", "fr");

    public static final String HEX_COLOR_REGEX = "^#(?:[0-9a-fA-F]{3}){1,2}$";

    public static final String LOGO_BUCKET = "logo";

    private Constants() {
    }
}
