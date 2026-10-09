package org.automation.business;

import com.microsoft.playwright.Page;
import org.automation.config.PlatformConfig;
import org.automation.pages.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Business layer class for login operations.
 *
 * <p>This class orchestrates page interactions to perform business-level login operations.
 * It encapsulates the sequence of steps needed to log in, making tests simpler and more
 * readable by hiding implementation details.</p>
 *
 * <p><b>Design:</b> The business layer receives a {@link PlatformConfig} instance and
 * a Playwright {@link Page}. It uses credentials from the platform config
 * rather than accepting them as method parameters, following the principle of least knowledge.
 * The business layer creates the {@link LoginPage} internally, maintaining the abstraction.</p>
 *
 * <p><b>Usage:</b> In your test classes, instantiate this class with the platform config
 * and the current page, then call business methods like {@link #login()}.</p>
 *
 * @since 1.0.0
 */
public class LoginBusiness {

    private final LoginPage loginPage;
    private final PlatformConfig platformConfig;
    private static final Logger logger = LoggerFactory.getLogger(LoginBusiness.class);

    /**
     * Constructs a LoginBusiness with the given platform configuration and page.
     *
     * @param platformConfig the platform configuration containing URL and credentials
     * @param page           the Playwright Page instance to create the LoginPage from
     */
    public LoginBusiness(PlatformConfig platformConfig, Page page) {
        this.platformConfig = platformConfig;
        this.loginPage = new LoginPage(page);
    }

    /**
     * Performs login using credentials from the platform configuration.
     *
     * <p>This method navigates to the application URL, waits for the page to load,
     * and submits the login credentials. It uses the URL, email, and password
     * from the {@link PlatformConfig} instance provided at construction.</p>
     */
    public void login() {
        logger.info("Performing login for user: {}", platformConfig.getEmail());
        loginPage.navigateToLoginPage(platformConfig.getUrl());
        loginPage.waitForPageLoad();
        loginPage.login(platformConfig.getEmail(), platformConfig.getPassword());
        logger.info("Login operation completed");
    }

    /**
     * Performs login and waits for the success message to appear.
     *
     * @return true if login was successful (success message appeared), false otherwise
     */
    public boolean loginAndWaitForSuccess() {
        login();
        try {
            loginPage.waitForLoginSuccess();
            logger.info("Login successful for user: {}", platformConfig.getEmail());
            return true;
        } catch (Exception e) {
            logger.warn("Login failed for user: {}", platformConfig.getEmail(), e);
            return false;
        }
    }

    /**
     * Attempts login and returns the error message if displayed.
     *
     * @return the error message text, or null if no error is shown
     */
    public String getLoginError() {
        login();
        return loginPage.getErrorMessage();
    }

    /**
     * Returns the platform configuration used by this business object.
     *
     * @return the PlatformConfig instance
     */
    public PlatformConfig getPlatformConfig() {
        return platformConfig;
    }
}
