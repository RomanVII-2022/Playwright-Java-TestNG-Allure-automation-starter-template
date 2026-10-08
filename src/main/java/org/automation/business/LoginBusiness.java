package org.automation.business;

import com.microsoft.playwright.Page;
import org.automation.pages.LoginPage;

public class LoginBusiness {
    private final LoginPage loginPage;

    public LoginBusiness(Page page){
        loginPage = new LoginPage(page);
    }

    public void loginToWeb(String url, String email, String password){
        loginPage.navigateToLoginPage(url);
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickSubmitButton();
    }
}
