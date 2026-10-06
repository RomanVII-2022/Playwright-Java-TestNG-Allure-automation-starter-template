package org.automation.base;

import com.microsoft.playwright.*;
import org.automation.config.ConfigManager;
import org.automation.config.PlatformConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.Arrays;
import java.util.List;

public class BaseTest {
    private final ThreadLocal<Playwright> playwrightTL = ThreadLocal.withInitial(Playwright::create);
    private final ThreadLocal<Browser> browserTL = new ThreadLocal<>();
    private final ThreadLocal<BrowserContext> contextTL = new ThreadLocal<>();
    private final ThreadLocal<Page> pageTL = new ThreadLocal<>();

    protected PlatformConfig platformConfig;
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected String platformName = System.getProperty("platform", "web");
    protected boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "false"));
    protected double slowMoMs = Double.parseDouble(System.getProperty("slowMo", "50"));

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        logger.info(
                "Initializing Test | Platform: {} | Headless: {} | slowMo: {}",
                platformName,
                isHeadless,
                slowMoMs);

        platformConfig = ConfigManager.getInstance().getPlatformConfig(platformName);

        launchBrowserSession();
    }

    private void launchBrowserSession() {
        browserTL.set(playwrightTL.get().chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(isHeadless)
                .setSlowMo(slowMoMs)
                .setArgs(List.of("--disable-dev-shm-usage", "--no-sandbox"))));

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setPermissions(Arrays.asList("camera", "notifications"));

        contextTL.set(browserTL.get().newContext(contextOptions));

        pageTL.set(contextTL.get().newPage());
    }

    public Page getPage() {
        return pageTL.get();
    }

    public Playwright getPlaywright() {
        return playwrightTL.get();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        Page page = pageTL.get();
        BrowserContext browserContext = contextTL.get();
        Browser browser = browserTL.get();
        Playwright playwright = playwrightTL.get();


        closeBrowserResourcesInternal(page, browserContext, browser, playwright);
    }

    private void closeBrowserResourcesInternal(Page p, BrowserContext c, Browser b, Playwright pw) {
        try {
            if (p != null) {
                p.close();
            }
        } catch (Exception ignored) {
        }
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception ignored) {
        }
        try {
            if (b != null) {
                b.close();
            }
        } catch (Exception ignored) {
        }
        try {
            if (pw != null) {
                pw.close();
            }
        } catch (Exception ignored) {
        }

        pageTL.remove();
        contextTL.remove();
        browserTL.remove();
        playwrightTL.remove();
    }


}
