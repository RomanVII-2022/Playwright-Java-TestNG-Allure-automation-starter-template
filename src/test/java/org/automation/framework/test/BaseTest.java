package org.automation.framework.test;

import com.microsoft.playwright.*;
import org.automation.config.ConfigManager;
import org.automation.config.PlatformConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.util.Arrays;
import java.util.List;

/**
 * Base test class providing Playwright browser lifecycle management with dynamic browser selection.
 *
 * <p>This class handles:</p>
 * <ul>
 *   <li>Browser initialization and teardown</li>
 *   <li>Browser context and page creation</li>
 *   <li>Dynamic browser selection (Chromium, Firefox, WebKit)</li>
 *   <li>Platform configuration loading</li>
 *   <li>Screenshot capture on test failures</li>
 *   <li>Allure integration for reporting</li>
 * </ul>
 *
 * <p><b>Browser Selection:</b> Override via system property {@code -Dbrowser=firefox|chromium|webkit}
 * or via {@code testng.xml} parameters. Default is Chromium.</p>
 *
 * <p><b>Usage:</b> Extend this class in your test classes and call {@code getPage()} to get
 * the current Playwright Page instance.</p>
 *
 * @since 1.0.0
 */
public class BaseTest {

    private final ThreadLocal<Playwright> playwrightTL = ThreadLocal.withInitial(Playwright::create);
    private final ThreadLocal<Browser> browserTL = new ThreadLocal<>();
    private final ThreadLocal<BrowserContext> contextTL = new ThreadLocal<>();
    private final ThreadLocal<Page> pageTL = new ThreadLocal<>();

    /** The loaded platform configuration for the current test. */
    protected PlatformConfig platformConfig;

    /** Logger shared across all test classes. */
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    // Configuration properties (defaults resolved in setUp)
    protected String platformName = "web";
    protected BrowserType browserType;
    protected boolean isHeadless = false;
    protected double slowMoMs = 0.0;
    protected String browserName = "chromium";

    /**
     * Sets up the browser session before each test method.
     *
     * <p>Configuration is resolved in the following order of priority:</p>
     * <ol>
     *   <li>TestNG parameters from {@code testng.xml} (if provided)</li>
     *   <li>Maven system properties (e.g., {@code -Dbrowser=firefox})</li>
     *   <li>Default values defined in the class</li>
     * </ol>
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "platform", "headless", "slowMo"})
    public void setUp(@Optional String browser,
                      @Optional String platform,
                      @Optional String headless,
                      @Optional String slowMo) {
        // Resolve browser: TestNG param > system property > default
        this.browserName = resolveString(browser, "browser", "chromium").toLowerCase();

        // Resolve platform: TestNG param > system property > default
        this.platformName = resolveString(platform, "platform", "web");

        // Resolve headless: TestNG param > system property > default
        this.isHeadless = Boolean.parseBoolean(resolveString(headless, "headless", "false"));

        // Resolve slowMo: TestNG param > system property > default
        this.slowMoMs = Double.parseDouble(resolveString(slowMo, "slowMo", "0"));

        logger.info(
                "Initializing Test | Browser: {} | Platform: {} | Headless: {} | SlowMo: {}ms",
                browserName,
                platformName,
                isHeadless,
                slowMoMs);

        // Resolve browser type from property
        resolveBrowserType();

        // Load platform configuration
        platformConfig = ConfigManager.getInstance().getPlatformConfig(platformName);

        // Launch browser session
        launchBrowserSession();
    }

    /**
     * Tears down the browser session after each test method.
     *
     * <p>Captures a screenshot on failure and attaches it to the Allure report.
     * Closes the page, context, and browser — but keeps Playwright alive
     * for subsequent tests on the same thread.</p>
     *
     * @param result the TestNG test result
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        // Capture screenshot on test failure
        if (result.getStatus() == ITestResult.FAILURE) {
            captureScreenshotOnFailure(result);
        }

        // Close page, context, and browser — but NOT Playwright itself
        // Playwright is closed in @AfterSuite to allow multiple tests per thread
        closePage();
        closeContext();
        closeBrowser();

        // Clean up thread locals (except Playwright which is reused)
        pageTL.remove();
        contextTL.remove();
        browserTL.remove();
    }

    /**
     * Closes the Playwright instance after all tests in the suite have completed.
     * This should only be called once at the end of the test suite.
     */
    @org.testng.annotations.AfterSuite
    public void afterSuite() {
        closePlaywright();
        playwrightTL.remove();
    }

    /**
     * Resolves the browser type based on the browser system property.
     * Supported values: chromium (default), firefox, webkit.
     */
    private void resolveBrowserType() {
        switch (browserName) {
            case "firefox":
                this.browserType = playwrightTL.get().firefox();
                break;
            case "webkit":
                this.browserType = playwrightTL.get().webkit();
                break;
            case "chromium":
            default:
                this.browserType = playwrightTL.get().chromium();
                break;
        }
    }

    /**
     * Launches the browser session with configured options.
     */
    private void launchBrowserSession() {
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(isHeadless)
                .setSlowMo(slowMoMs)
                .setArgs(Arrays.asList(
                        "--disable-dev-shm-usage",
                        "--no-sandbox",
                        "--disable-setuid-sandbox",
                        "--disable-gpu"
                ));

        browserTL.set(browserType.launch(launchOptions));

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setLocale("en-US")
                .setTimezoneId("America/New_York")
                .setPermissions(List.of("geolocation"));

        contextTL.set(browserTL.get().newContext(contextOptions));

        pageTL.set(contextTL.get().newPage());
    }

    // ==================== Getters ====================

    /**
     * Returns the current Playwright Page instance.
     *
     * @return the Page object
     */
    public Page getPage() {
        return pageTL.get();
    }

    /**
     * Returns the Playwright instance.
     *
     * @return the Playwright object
     */
    public Playwright getPlaywright() {
        return playwrightTL.get();
    }

    /**
     * Returns the Browser instance.
     *
     * @return the Browser object
     */
    public Browser getBrowser() {
        return browserTL.get();
    }

    /**
     * Returns the BrowserContext instance.
     *
     * @return the BrowserContext object
     */
    public BrowserContext getContext() {
        return contextTL.get();
    }

    /**
     * Returns the name of the browser being used.
     *
     * @return the browser name (e.g., "chromium", "firefox", "webkit")
     */
    public String getBrowserName() {
        return browserName;
    }

    // ==================== Screenshot Capture ====================

    /**
     * Captures a full-page screenshot on test failure and attaches it to the Allure report.
     *
     * @param result the TestNG test result containing failure details
     */
    private void captureScreenshotOnFailure(ITestResult result) {
        try {
            Page page = pageTL.get();
            if (page != null) {
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                        .setFullPage(true));

                // Attach screenshot to Allure using lifecycle API
                io.qameta.allure.Allure.getLifecycle().addAttachment(
                    "Screenshot on failure - " + result.getName(),
                    "image/png",
                    "png",
                    screenshot
                );

                logger.info("Screenshot captured for failed test: {}", result.getName());
            }
        } catch (Exception e) {
            logger.warn("Failed to capture screenshot for test: {}", result.getName(), e);
        }
    }

    // ==================== Configuration Helpers ====================

    /**
     * Resolves a string configuration value with fallback to system property and default.
     * Handles the case where both TestNG parameters and system properties may be empty strings.
     *
     * @param value        the value from TestNG parameters
     * @param sysPropName  the system property name to check
     * @param defaultValue the default value if both are empty
     * @return the resolved non-empty string value
     */
    private String resolveString(String value, String sysPropName, String defaultValue) {
        // Check TestNG parameter first
        if (value != null && !value.isBlank()) {
            return value;
        }
        // Check system property
        String sysProp = System.getProperty(sysPropName);
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp;
        }
        // Return default
        return defaultValue;
    }

    // ==================== Cleanup ====================

    /**
     * Closes all browser resources and cleans up thread locals.
     */
    private void closeBrowserResources() {
        closePage();
        closeContext();
        closeBrowser();
        closePlaywright();

        // Clean up thread locals
        pageTL.remove();
        contextTL.remove();
        browserTL.remove();
    }

    private void closePage() {
        try {
            Page page = pageTL.get();
            if (page != null) {
                page.close();
            }
        } catch (Exception e) {
            logger.warn("Error closing page", e);
        }
    }

    private void closeContext() {
        try {
            BrowserContext context = contextTL.get();
            if (context != null) {
                context.close();
            }
        } catch (Exception e) {
            logger.warn("Error closing context", e);
        }
    }

    private void closeBrowser() {
        try {
            Browser browser = browserTL.get();
            if (browser != null) {
                browser.close();
            }
        } catch (Exception e) {
            logger.warn("Error closing browser", e);
        }
    }

    private void closePlaywright() {
        try {
            Playwright playwright = playwrightTL.get();
            if (playwright != null) {
                playwright.close();
            }
        } catch (Exception e) {
            logger.warn("Error closing playwright", e);
        }
    }
}
