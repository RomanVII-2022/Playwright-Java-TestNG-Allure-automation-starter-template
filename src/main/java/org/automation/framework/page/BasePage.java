package org.automation.framework.page;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.List;

/**
 * Base page class providing common web interaction methods.
 * All page objects should extend this class.
 *
 * <p>This class encapsulates Playwright Page interactions and provides
 * a consistent API for navigation, locators, actions, queries, and waits.</p>
 *
 * @since 1.0.0
 */
public class BasePage {

    protected final Page page;
    protected final Logger logger;

    /**
     * Constructs a BasePage with the given Playwright Page.
     *
     * @param page the Playwright Page instance to interact with
     */
    public BasePage(Page page) {
        this.page = page;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    // ==================== Navigation ====================

    /**
     * Navigates to the specified URL and waits for the page to load.
     *
     * @param url the URL to navigate to
     */
    protected void navigate(String url) {
        logger.info("Navigating to: {}", url);
        page.navigate(url);
        page.waitForLoadState();
    }

    /**
     * Navigates to the specified URL with custom navigation options.
     *
     * @param url    the URL to navigate to
     * @param options navigation options (timeout, waitUntil, etc.)
     */
    protected void navigate(String url, Page.NavigateOptions options) {
        logger.info("Navigating to url: {}", url);
        page.navigate(url, options);
    }

    /**
     * Refreshes the current page and waits for it to load.
     */
    protected void refresh() {
        logger.debug("Refreshing current page");
        page.reload();
        page.waitForLoadState();
    }

    /**
     * Returns the current URL of the page.
     *
     * @return the current page URL
     */
    protected String getCurrentUrl() {
        return page.url();
    }

    /**
     * Returns the title of the current page.
     *
     * @return the page title
     */
    protected String getPageTitle() {
        return page.title();
    }

    // ==================== Locators ====================

    /**
     * Creates a locator using a CSS selector or XPath.
     *
     * @param selector the selector string
     * @return a Playwright Locator
     */
    protected Locator locator(String selector) {
        return page.locator(selector);
    }

    /**
     * Creates a locator that matches elements by their visible text.
     *
     * @param text the text content to match
     * @return a Playwright Locator
     */
    protected Locator getByText(String text) {
        return page.getByText(text);
    }

    /**
     * Creates a locator that matches elements by their placeholder text.
     *
     * @param placeholder the placeholder text to match
     * @return a Playwright Locator
     */
    protected Locator getByPlaceholder(String placeholder) {
        return page.getByPlaceholder(placeholder);
    }

    /**
     * Creates a locator that matches elements by their accessible label.
     *
     * @param label the accessible label to match
     * @return a Playwright Locator
     */
    protected Locator getByLabel(String label) {
        return page.getByLabel(label);
    }

    /**
     * Creates a locator that matches elements by their ARIA role.
     *
     * @param role    the ARIA role (e.g., BUTTON, LINK, HEADING)
     * @param options additional matching options
     * @return a Playwright Locator
     */
    protected Locator getByRole(AriaRole role, Page.GetByRoleOptions options) {
        return page.getByRole(role, options);
    }

    /**
     * Creates a locator that matches elements by their data-testid attribute.
     *
     * @param testId the test ID value
     * @return a Playwright Locator
     */
    protected Locator getByTestId(String testId) {
        return page.getByTestId(testId);
    }

    // ==================== Actions ====================

    /**
     * Clicks an element identified by a CSS selector.
     *
     * @param selector the CSS selector of the element
     */
    protected void click(String selector) {
        logger.debug("Clicking on: {}", selector);
        page.locator(selector).click();
    }

    /**
     * Clicks a Playwright Locator.
     *
     * @param locator the Locator to click
     */
    protected void click(Locator locator) {
        logger.debug("Clicking on element: {}", locator.toString());
        locator.click();
    }

    /**
     * Double-clicks an element identified by a CSS selector.
     *
     * @param selector the CSS selector of the element
     */
    protected void doubleClick(String selector) {
        logger.debug("Double clicking on element: {}", selector);
        page.locator(selector).dblclick();
    }

    /**
     * Right-clicks an element identified by a CSS selector.
     *
     * @param selector the CSS selector of the element
     */
    protected void rightClick(String selector) {
        logger.debug("Right clicking on element: {}", selector);
        page.locator(selector).click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
    }

    /**
     * Fills an input field identified by a CSS selector.
     *
     * @param selector the CSS selector of the input
     * @param value    the value to fill
     */
    protected void fill(String selector, String value) {
        logger.debug("Filling element {} with value: {}", selector, value);
        page.locator(selector).fill(value);
    }

    /**
     * Fills a Playwright Locator.
     *
     * @param locator the Locator to fill
     * @param value   the value to fill
     */
    protected void fill(Locator locator, String value) {
        logger.debug("Filling element with value: {}", value);
        locator.fill(value);
    }

    /**
     * Presses a keyboard key on an element identified by a CSS selector.
     *
     * @param selector the CSS selector of the element
     * @param key      the key to press (e.g., "Enter", "Tab")
     */
    protected void press(String selector, String key) {
        logger.debug("Pressing key '{}' on element: {}", key, selector);
        page.locator(selector).press(key);
    }

    /**
     * Clears the value of an input field.
     *
     * @param selector the CSS selector of the input
     */
    protected void clear(String selector) {
        logger.debug("Clearing element: {}", selector);
        page.locator(selector).clear();
    }

    /**
     * Selects an option in a dropdown by its value.
     *
     * @param selector the CSS selector of the select element
     * @param value    the value of the option to select
     */
    protected void selectOption(String selector, String value) {
        logger.debug("Selecting option '{}' in element: {}", value, selector);
        page.locator(selector).selectOption(value);
    }

    /**
     * Selects multiple options in a dropdown.
     *
     * @param selector the CSS selector of the select element
     * @param options  the SelectOption objects to select
     */
    protected void selectOption(String selector, SelectOption... options) {
        logger.debug("Selecting option in element: {}", selector);
        page.locator(selector).selectOption(options);
    }

    // ==================== Queries ====================

    /**
     * Returns the text content of an element.
     *
     * @param selector the CSS selector of the element
     * @return the text content, or null if the element is not found
     */
    protected String getText(String selector) {
        logger.debug("Getting text from element: {}", selector);
        return page.locator(selector).textContent();
    }

    /**
     * Returns the value of a specified attribute.
     *
     * @param selector  the CSS selector of the element
     * @param attribute the attribute name
     * @return the attribute value, or null if not present
     */
    protected String getAttributeValue(String selector, String attribute) {
        logger.debug("Getting attribute '{}' from element: {}", attribute, selector);
        return page.locator(selector).getAttribute(attribute);
    }

    /**
     * Returns the current input value of a form element.
     *
     * @param selector the CSS selector of the input
     * @return the input value
     */
    protected String getInputValue(String selector) {
        logger.debug("Getting input value from element: {}", selector);
        return page.locator(selector).inputValue();
    }

    /**
     * Checks if an element is visible.
     *
     * @param selector the CSS selector of the element
     * @return true if the element is visible
     */
    protected boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    /**
     * Checks if an element is enabled.
     *
     * @param selector the CSS selector of the element
     * @return true if the element is enabled
     */
    protected boolean isEnabled(String selector) {
        return page.locator(selector).isEnabled();
    }

    /**
     * Checks if a checkbox or radio button is checked.
     *
     * @param selector the CSS selector of the element
     * @return true if the element is checked
     */
    protected boolean isChecked(String selector) {
        return page.locator(selector).isChecked();
    }

    /**
     * Checks if an element is displayed (alias for isVisible).
     *
     * @param selector the CSS selector of the element
     * @return true if the element is displayed
     */
    protected boolean isDisplayed(String selector) {
        return page.locator(selector).isVisible();
    }

    /**
     * Returns the number of elements matching the selector.
     *
     * @param selector the CSS selector
     * @return the count of matching elements
     */
    protected int getElementCount(String selector) {
        return page.locator(selector).count();
    }

    // ==================== Wait Operations ====================

    /**
     * Waits for an element to become visible.
     *
     * @param selector the CSS selector of the element
     */
    protected void waitForElementVisible(String selector) {
        logger.debug("Waiting for element to be visible: {}", selector);
        page.locator(selector).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    /**
     * Waits for an element to become hidden.
     *
     * @param selector the CSS selector of the element
     */
    protected void waitForElementHidden(String selector) {
        logger.debug("Waiting for element to be hidden: {}", selector);
        page.locator(selector).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
    }

    /**
     * Waits for an element to become enabled (uses VISIBLE state as proxy).
     *
     * @param selector the CSS selector of the element
     */
    protected void waitForElementEnabled(String selector) {
        logger.debug("Waiting for element to be enabled: {}", selector);
        page.locator(selector).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    /**
     * Waits for the URL to match the given pattern.
     *
     * @param urlPattern the URL pattern to wait for (supports glob patterns)
     */
    protected void waitForURL(String urlPattern) {
        logger.debug("Waiting for URL matching: {}", urlPattern);
        page.waitForURL(urlPattern);
    }

    /**
     * Waits for the page load state to complete.
     */
    protected void waitForLoadState() {
        page.waitForLoadState();
    }

    /**
     * Pauses execution for the specified duration.
     *
     * @param milliseconds the number of milliseconds to wait
     */
    protected void waitForTimeout(long milliseconds) {
        page.waitForTimeout(milliseconds);
    }

    // ==================== Frames ====================

    /**
     * Returns a FrameLocator for interacting with frames/iframes.
     *
     * @param selector the selector of the frame
     * @return a FrameLocator
     */
    protected FrameLocator getFrameLocator(String selector) {
        return page.frameLocator(selector);
    }

    // ==================== File Operations ====================

    /**
     * Sets files on a file input element.
     *
     * @param selector the CSS selector of the file input
     * @param filePath the path to the file(s) to set
     */
    protected void setFileInput(String selector, String filePath) {
        logger.debug("Setting file input: {}", filePath);
        page.locator(selector).setInputFiles(Paths.get(filePath));
    }

    // ==================== JavaScript ====================

    /**
     * Evaluates a JavaScript expression in the page context.
     *
     * @param expression the JavaScript expression to evaluate
     * @return the result of the evaluation
     */
    protected Object evaluate(String expression) {
        return page.evaluate(expression);
    }

    // ==================== Browser Context ====================

    /**
     * Grants permissions to the browser context.
     *
     * @param permissions the permissions to grant (e.g., "geolocation", "notifications")
     */
    protected void grantPermissions(String... permissions) {
        logger.debug("Granting permissions: {}", String.join(", ", permissions));
        page.context().grantPermissions(java.util.Arrays.asList(permissions));
    }

    /**
     * Sets the geolocation for the browser context.
     *
     * @param latitude  the latitude coordinate
     * @param longitude the longitude coordinate
     */
    protected void setGeolocation(double latitude, double longitude) {
        logger.debug("Setting geolocation: {}, {}", latitude, longitude);
        page.context().setGeolocation(new Geolocation(latitude, longitude));
    }

    /**
     * Toggles offline mode for the browser context.
     *
     * @param offline true to enable offline mode
     */
    protected void setOffline(boolean offline) {
        logger.debug("Setting offline mode: {}", offline);
        page.context().setOffline(offline);
    }

    /**
     * Sets a cookie in the browser context.
     *
     * @param name   the cookie name
     * @param value  the cookie value
     * @param url    the URL scope for the cookie
     */
    protected void setCookie(String name, String value, String url) {
        logger.debug("Setting cookie: {} = {}", name, value);
        Cookie cookie = new Cookie(name, value).setUrl(url);
        page.context().addCookies(List.of(cookie));
    }

    /**
     * Clears all cookies from the browser context.
     */
    protected void clearCookies() {
        logger.debug("Clearing all cookies");
        page.context().clearCookies();
    }

    // ==================== Screenshots ====================

    /**
     * Takes a full-page screenshot and saves it to the specified file.
     *
     * @param filename the filename to save the screenshot to
     */
    protected void takeScreenshot(String filename) {
        try {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setFullPage(true)
                    .setPath(Paths.get("screenshots/" + filename)));
            logger.info("Screenshot saved: {}", filename);
        } catch (Exception e) {
            logger.warn("Failed to take screenshot", e);
        }
    }

    // ==================== Tabs ====================

    /**
     * Opens a new tab and navigates to the specified URL.
     *
     * @param url the URL to navigate to in the new tab
     */
    protected void openNewTab(String url) {
        page.context().newPage().navigate(url);
    }

    /**
     * Returns the number of open pages (tabs) in the browser context.
     *
     * @return the number of open pages
     */
    protected int getTabCount() {
        return page.context().pages().size();
    }
}
