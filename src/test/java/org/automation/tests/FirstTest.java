package org.automation.tests;

import org.automation.base.BaseTest;
import org.testng.annotations.Test;

public class FirstTest extends BaseTest {

    @Test
    public void navigateTest(){
        getPage().navigate(platformConfig.getUrl());
    }
}
