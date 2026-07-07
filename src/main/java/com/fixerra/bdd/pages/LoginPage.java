package com.fixerra.bdd.pages;

import com.fixerra.bdd.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the Fixerra Login Page (mobile OTP flow).
 *
 * The login page takes a 10-digit Aadhaar-linked mobile number and
 * has a "Get Started" button that triggers OTP dispatch.
 * There is no password field — authentication is OTP-based.
 */
public class LoginPage extends BasePage {

    private static final Logger log = LogManager.getLogger(LoginPage.class);

    // -------------------------------------------------------------------------
    // Element Locators — matched against the actual Fixerra app DOM
    // -------------------------------------------------------------------------

    @FindBy(name = "mobileNumber")
    private WebElement mobileInput;

    @FindBy(xpath = "//button[normalize-space()='Get Started']")
    private WebElement getStartedButton;

    @FindBy(css = ".text-redBright")
    private WebElement validationError;

    // Page identity elements
    @FindBy(xpath = "//*[normalize-space()='Begin your Investment Journey']")
    private WebElement heading;

    // Dashboard / landing indicator visible after successful OTP login
    @FindBy(css = "[class*='landingPage'], [data-testid='landing'], .home-container")
    private WebElement dashboardIndicator;

    // Locators used for explicit wait / presence checks
    private static final By MOBILE_INPUT_LOCATOR  = By.name("mobileNumber");
    private static final By GET_STARTED_LOCATOR   = By.xpath("//button[normalize-space()='Get Started']");
    private static final By VALIDATION_LOCATOR    = By.cssSelector(".text-redBright");
    private static final By HEADING_LOCATOR       = By.xpath("//*[normalize-space()='Begin your Investment Journey']");
    private static final By DASHBOARD_LOCATOR     = By.cssSelector("[class*='landingPage'], .home-container");

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    public void open() {
        String url = ConfigReader.get("base.url");
        log.info("Opening login page at {}", url);
        navigateTo(url);
        // Wait for mobile input to confirm the page loaded
        waitForVisible(MOBILE_INPUT_LOCATOR);
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    public void enterMobileNumber(String mobile) {
        log.info("Entering mobile number: {}", mobile);
        waitForClickable(mobileInput).clear();
        mobileInput.sendKeys(mobile);
    }

    public void clickGetStarted() {
        log.info("Clicking Get Started button");
        click(getStartedButton);
    }

    public void clickGetStartedWithoutInput() {
        log.info("Clicking Get Started without entering mobile");
        click(getStartedButton);
    }

    // -------------------------------------------------------------------------
    // State / Assertion helpers
    // -------------------------------------------------------------------------

    public boolean isLoginPageDisplayed() {
        try {
            waitForVisible(MOBILE_INPUT_LOCATOR);
            return mobileInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isHeadingDisplayed() {
        return isElementPresent(HEADING_LOCATOR);
    }

    public boolean isGetStartedEnabled() {
        try {
            return getStartedButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidationErrorDisplayed() {
        return isElementPresent(VALIDATION_LOCATOR);
    }

    public String getValidationErrorText() {
        log.info("Reading validation error text");
        return getText(waitForVisible(VALIDATION_LOCATOR));
    }

    public boolean isDashboardDisplayed() {
        try {
            waitForVisible(DASHBOARD_LOCATOR);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getMobileInputValue() {
        return mobileInput.getAttribute("value");
    }
}
