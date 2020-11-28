package com.mycompany.myapp.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String ANONYMOUS_USER = "anonymoususer";

    public static final String HEX_COLOR_REGEX = "^#(?:[0-9a-fA-F]{3}){1,2}$";
    public static final String IMAGE_COMPANY_FOLDER_PATH = "/Users/hugo/companies/";

    private Constants() {
    }
}
