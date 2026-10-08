package org.automation.base;

import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasePage {

    protected Page page;
    protected Logger logger;

    public BasePage(Page page) {
        this.page = page;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    protected void click(String selector) {
        logger.debug("Clicking on element: {}", selector);
        page.click(selector);
    }

    protected void refresh(){
        logger.debug("Refresh current page");
        page.reload();
    }

    protected void fill(String selector, String value) {
        logger.debug("Filling element {} with value: {}", selector, value);
        page.fill(selector, value);
    }

    protected String getText(String selector) {
        logger.debug("Getting text from element: {}", selector);
        return page.textContent(selector);
    }

    protected void navigate(String url) {
        logger.info("Navigating to: {}", url);
        page.navigate(url);
    }

}
