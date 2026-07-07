package com.fixerra.bdd.pages;

import com.fixerra.bdd.config.ConfigReader;
import com.fixerra.bdd.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base class for all Page Objects.
 * Provides reusable wait, click, type, and visibility helpers.
 */
public abstract class BasePage {

    private static final Logger log = LogManager.getLogger(BasePage.class);
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        int explicitWait = ConfigReader.getInt("explicit.wait");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
        PageFactory.initElements(driver, this);
    }

    // -------------------------------------------------------------------------
    // Wait helpers
    // -------------------------------------------------------------------------

    protected WebElement waitForVisible(By locator) {
        log.debug("Waiting for element to be visible: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(WebElement element) {
        log.debug("Waiting for element to be clickable");
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement waitForClickable(By locator) {
        log.debug("Waiting for element to be clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected boolean waitForInvisible(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected boolean isElementPresent(By locator) {
        try {
            return !driver.findElements(locator).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Action helpers
    // -------------------------------------------------------------------------

    protected void click(WebElement element) {
        waitForClickable(element).click();
        log.debug("Clicked element");
    }

    protected void type(WebElement element, String text) {
        waitForClickable(element);
        element.clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into element", text);
    }

    protected String getText(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.getText().trim();
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
