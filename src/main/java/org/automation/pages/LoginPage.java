package org.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.automation.framework.page.BasePage;

/**
 * Page Object for the login page.
 *
 * <p>Encapsulates all locators, actions, and queries related to the login functionality.
 * Extends {@link BasePage} to inherit common web interaction methods.</p>
 *
 * <p>This class follows the Page Object Model pattern, where each page in the application
 * is represented by a separate class. This makes tests more readable and maintainable —
 * when the UI changes, only this class needs to be updated.</p>
 *
 * @since 1.0.0
 */
public class LoginPage extends BasePage {

    // ==================== Locators ====================

    private static final String EMAIL_INPUT_SELECTOR = "#email";
    private static final String PASSWORD_INPUT_SELECTOR = "#password";
    private static final String ERROR_MESSAGE_SELECTOR = ".error";
    private static final String SUCCESS_MESSAGE_SELECTOR = ".success";
    private static final String LOGO_SELECTOR = "img.logo";

    /**
     * Constructs a LoginPage with the given Playwright Page.
     *
     * @param page the Playwright Page instance
     */
    public LoginPage(Page page) {
        super(page);
    }

    // ==================== Locator Helpers ====================

    private Locator emailInputElement() {
        return page.locator(EMAIL_INPUT_SELECTOR);
    }

    private Locator passwordInputElement() {
        return page.locator(PASSWORD_INPUT_SELECTOR);
    }

    private Locator submitButtonElement() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
    }

    private Locator errorMessageElement() {
        return page.locator(ERROR_MESSAGE_SELECTOR);
    }

    private Locator successMessageElement() {
        return page.locator(SUCCESS_MESSAGE_SELECTOR);
    }

    private Locator logoElement() {
        return page.locator(LOGO_SELECTOR);
    }

    // ==================== Actions ====================

    /**
     * Navigates to the login page.
     *
     * @param url the application URL
     */
    public void navigateToLoginPage(String url) {
        navigate(url);
    }

    /**
     * Navigates to the login page with custom navigation options.
     *
     * @param url     the application URL
     * @param options navigation options
     */
    public void navigateToLoginPage(String url, Page.NavigateOptions options) {
        navigate(url, options);
    }

    /**
     * Enters an email address into the email input field.
     *
     * @param email the email address to enter
     */
    public void enterEmail(String email) {
        logger.info("Entering email: {}", email);
        emailInputElement().click();
        emailInputElement().fill(email);
    }

    /**
     * Enters a password into the password input field.
     *
     * @param password the password to enter
     */
    public void enterPassword(String password) {
        logger.info("Entering password");
        passwordInputElement().click();
        passwordInputElement().fill(password);
    }

    /**
     * Clicks the submit button.
     */
    public void clickSubmitButton() {
        logger.info("Clicking submit button");
        submitButtonElement().click();
    }

    /**
     * Performs a full login sequence: enters credentials and submits.
     *
     * @param email    the user's email
     * @param password the user's password
     */
    public void login(String email, String password) {
        logger.info("Logging in with email: {}", email);
        enterEmail(email);
        enterPassword(password);
        clickSubmitButton();
    }

    // ==================== Queries ====================

    /**
     * Returns the error message if displayed, otherwise null.
     *
     * @return the error message text, or null if no error is shown
     */
    public String getErrorMessage() {
        if (errorMessageElement().isVisible()) {
            return errorMessageElement().textContent();
        }
        return null;
    }

    /**
     * Returns the success message if displayed, otherwise null.
     *
     * @return the success message text, or null if no success is shown
     */
    public String getSuccessMessage() {
        if (successMessageElement().isVisible()) {
            return successMessageElement().textContent();
        }
        return null;
    }

    /**
     * Checks if the logo is displayed on the page.
     *
     * @return true if the logo is visible
     */
    public boolean isLogoDisplayed() {
        return logoElement().isVisible();
    }

    /**
     * Checks if the email input field is visible.
     *
     * @return true if the email input is visible
     */
    public boolean isEmailInputVisible() {
        return emailInputElement().isVisible();
    }

    /**
     * Checks if the password input field is visible.
     *
     * @return true if the password input is visible
     */
    public boolean isPasswordInputVisible() {
        return passwordInputElement().isVisible();
    }

    /**
     * Checks if the submit button is enabled.
     *
     * @return true if the submit button is enabled
     */
    public boolean isSubmitButtonEnabled() {
        return submitButtonElement().isEnabled();
    }

    /**
     * Returns the current value in the email input field.
     *
     * @return the email input value
     */
    public String getEmailInputValue() {
        return emailInputElement().inputValue();
    }

    // ==================== Validation ====================

    /**
     * Waits for the login success message to appear.
     */
    public void waitForLoginSuccess() {
        waitForElementVisible(SUCCESS_MESSAGE_SELECTOR);
    }

    /**
     * Waits for the login error message to appear.
     */
    public void waitForLoginError() {
        waitForElementVisible(ERROR_MESSAGE_SELECTOR);
    }

    /**
     * Waits for the page to fully load.
     */
    public void waitForPageLoad() {
        waitForLoadState();
    }

    /**
     * Validates that the logo is present and visible on the page.
     *
     * @throws AssertionError if the logo is not displayed
     */
    public void validateLogoIsPresent() {
        waitForElementVisible(LOGO_SELECTOR);
        if (!isLogoDisplayed()) {
            throw new AssertionError("Logo is not displayed on the page");
        }
    }
}
