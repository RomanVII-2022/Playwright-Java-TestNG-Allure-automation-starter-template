package org.automation.tests;

import org.testng.annotations.Test;

public class FirstTest extends BaseTest{

    @Test
    public void navigateTest(){
        page.navigate("https://playwright.dev");
    }
}
