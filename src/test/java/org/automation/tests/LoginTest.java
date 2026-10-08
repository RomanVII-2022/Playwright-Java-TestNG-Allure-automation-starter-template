package org.automation.tests;

import org.automation.base.BaseTest;
import org.automation.business.LoginBusiness;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    private LoginBusiness loginBusiness;

    @BeforeMethod(alwaysRun = true)
    public void setupBusinessFlow(){
        this.loginBusiness = new LoginBusiness(getPage());
    }

    @Test(description = "Login to web")
    public void loginWithCorrectCredentialsTest(){
        loginBusiness.loginToWeb(platformConfig.getUrl(), platformConfig.getEmail(), platformConfig.getPassword());
    }
}
