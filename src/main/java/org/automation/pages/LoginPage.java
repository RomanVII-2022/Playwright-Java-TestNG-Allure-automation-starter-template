package org.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.automation.base.BasePage;

public class LoginPage extends BasePage {

    private static final String EMAIL_INPUT_SELECTOR = "#email";

    public LoginPage (Page page){
        super(page);
    }

    private Locator emailInputElement(){
        return page.locator(EMAIL_INPUT_SELECTOR);
    }

    private Locator passwordInputElement(){
        return page.getByPlaceholder("Password");
    }

    private Locator submitButtonElement(){
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
    }

    public void navigateToLoginPage(String url){
        navigate(url);
    }

    public void enterEmail(String email){
        emailInputElement().click();
        emailInputElement().fill(email);
    }

    public void enterPassword(String password){
        passwordInputElement().click();
        passwordInputElement().fill(password);
    }

    public void clickSubmitButton(){
        submitButtonElement().click();
    }
}
