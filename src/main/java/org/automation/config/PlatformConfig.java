package org.automation.config;

/**
 * Platform configuration class loaded from platform_data.json.
 *
 * <p>Each platform entry contains the URL, credentials, and browser settings
 * for a specific test environment. The ConfigManager loads all entries from
 * the JSON file and provides lookup by platform name.</p>
 *
 * <p><b>Environment Variable Substitution:</b> This class supports placeholders
 * in the format {@code ${ENV_VAR_NAME}} which are replaced with actual
 * environment variable values at load time.</p>
 *
 * @since 1.0.0
 */
public class PlatformConfig {

    private String name;
    private String url;
    private String email;
    private String password;
    private String browser;
    private Boolean headless;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public Boolean isHeadless() {
        return headless;
    }

    public void setHeadless(Boolean headless) {
        this.headless = headless;
    }
}
