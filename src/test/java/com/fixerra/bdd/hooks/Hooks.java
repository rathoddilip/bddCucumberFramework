package com.fixerra.bdd.hooks;

import com.fixerra.bdd.driver.DriverManager;
import com.fixerra.bdd.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cucumber lifecycle hooks.
 *
 * @BeforeAll — launches the browser once before the entire test suite
 * @AfterAll  — quits the browser once after the entire test suite
 * @Before    — logs scenario start (browser already open)
 * @After     — captures screenshot on failure
 * @AfterStep — attaches a screenshot on failed steps (optional, useful for debugging)
 */
public class Hooks {

    private static final Logger log = LogManager.getLogger(Hooks.class);

    /**
     * Initialises the WebDriver once before all scenarios.
     * The browser stays open for the entire test suite.
     */
    @BeforeAll
    public static void setUpBrowserOnce() {
        log.info("========================================");
        log.info("SUITE START — initialising browser");
        log.info("========================================");
        DriverManager.initDriver();
    }

    /**
     * Quits the WebDriver once after all scenarios have finished.
     */
    @AfterAll
    public static void tearDownBrowserOnce() {
        log.info("========================================");
        log.info("SUITE END — quitting browser");
        log.info("========================================");
        DriverManager.quitDriver();
    }

    /**
     * Logs scenario start before each scenario.
     * Order=1 ensures this runs before any other @Before hooks.
     */
    @Before(order = 1)
    public void logScenarioStart(Scenario scenario) {
        log.info("========================================");
        log.info("START scenario: {}", scenario.getName());
        log.info("Tags: {}", scenario.getSourceTagNames());
        log.info("========================================");
    }

    /**
     * After each scenario:
     * 1. If the scenario failed, capture a screenshot and embed it in the Cucumber report.
     *
     * Order=1 ensures this runs before any other @After hooks.
     */
    @After(order = 1)
    public void logScenarioEnd(Scenario scenario) {
        log.info("Scenario '{}' finished with status: {}", scenario.getName(), scenario.getStatus());

        if (scenario.isFailed()) {
            log.warn("Scenario FAILED — capturing screenshot");
            byte[] screenshot = ScreenshotUtil.captureAsBytes();
            if (screenshot.length > 0) {
                scenario.attach(screenshot, "image/png", "Failure Screenshot: " + scenario.getName());
                log.info("Screenshot embedded in Cucumber report");
            }
            ScreenshotUtil.capture(scenario.getName());
        }

        log.info("========================================");
        log.info("END scenario: {}", scenario.getName());
        log.info("========================================");
    }

    /**
     * Attach a screenshot after each failed step (opt-in; remove if too verbose).
     * Useful during development for pinpointing exactly which step failed.
     */
    @AfterStep
    public void captureScreenshotAfterFailedStep(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = ScreenshotUtil.captureAsBytes();
            if (screenshot.length > 0) {
                scenario.attach(screenshot, "image/png", "Step failure screenshot");
            }
        }
    }
}
