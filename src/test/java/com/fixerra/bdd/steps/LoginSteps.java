package com.fixerra.bdd.steps;

import com.fixerra.bdd.pages.LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for login.feature (Fixerra mobile OTP flow).
 */
public class LoginSteps {

    private static final Logger log = LogManager.getLogger(LoginSteps.class);

    // LoginPage is re-created per scenario because Hooks spins up a fresh browser each time.
    private final LoginPage loginPage = new LoginPage();

    // -------------------------------------------------------------------------
    // Given
    // -------------------------------------------------------------------------

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        log.info("Step: navigating to login page");
        loginPage.open();
        assertTrue(loginPage.isLoginPageDisplayed(),
                "Login page mobile input should be visible after navigation");
    }

    // -------------------------------------------------------------------------
    // When
    // -------------------------------------------------------------------------

    @When("the user enters mobile number {string}")
    public void theUserEntersMobileNumber(String mobile) {
        log.info("Step: entering mobile number '{}'", mobile);
        loginPage.enterMobileNumber(mobile);
    }

    @When("the user clicks Get Started")
    public void theUserClicksGetStarted() {
        log.info("Step: clicking Get Started");
        loginPage.clickGetStarted();
    }

    @When("the user clicks Get Started without entering a mobile number")
    public void theUserClicksGetStartedWithoutEnteringAMobileNumber() {
        log.info("Step: clicking Get Started without a mobile number");
        loginPage.clickGetStartedWithoutInput();
    }

    // -------------------------------------------------------------------------
    // Then — Assertions
    // -------------------------------------------------------------------------

    @Then("the login page heading should be displayed")
    public void theLoginPageHeadingShouldBeDisplayed() {
        log.info("Step: asserting heading is visible");
        assertTrue(loginPage.isHeadingDisplayed(),
                "The login page heading 'Begin your Investment Journey' should be visible");
    }

    @Then("the Get Started button should be present")
    public void theGetStartedButtonShouldBePresent() {
        log.info("Step: asserting Get Started button is present");
        assertTrue(loginPage.isGetStartedEnabled(),
                "Get Started button should be present on the login page");
    }

    @Then("the Get Started button should be enabled")
    public void theGetStartedButtonShouldBeEnabled() {
        log.info("Step: asserting Get Started button is enabled");
        assertTrue(loginPage.isGetStartedEnabled(),
                "Get Started button should be enabled after entering a valid mobile number");
    }

    @Then("a validation error should be displayed")
    public void aValidationErrorShouldBeDisplayed() {
        log.info("Step: asserting validation error is shown");
        assertTrue(loginPage.isValidationErrorDisplayed(),
                "A validation error (.text-redBright) should be visible");
    }

    @And("the validation error should contain {string}")
    public void theValidationErrorShouldContain(String expectedText) {
        log.info("Step: asserting validation error contains '{}'", expectedText);
        String actual = loginPage.getValidationErrorText();
        assertTrue(actual.toLowerCase().contains(expectedText.toLowerCase()),
                String.format("Expected validation error to contain '%s' but got: '%s'",
                        expectedText, actual));
    }

    @Then("the user should be redirected to the dashboard")
    public void theUserShouldBeRedirectedToTheDashboard() {
        log.info("Step: asserting dashboard is displayed");
        assertTrue(loginPage.isDashboardDisplayed(),
                "Dashboard / landing page should be visible after successful OTP login");
    }
}
