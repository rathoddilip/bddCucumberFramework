package com.fixerra.bdd.driver;

import com.fixerra.bdd.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Thread-safe WebDriver manager using ThreadLocal.
 * Supports Chrome, Firefox, and Edge via WebDriverManager.
 * Browser and headless mode are driven by config.properties (overridable via -D system properties).
 */
public class DriverManager {

    private static final Logger log = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    private DriverManager() {
        // utility class — no instantiation
    }

    /**
     * Returns true if a WebDriver is already initialised for the current thread.
     */
    public static boolean isInitialized() {
        return driverThread.get() != null;
    }

    /**
     * Initialises the WebDriver for the current thread.
     * Skips initialisation if a driver is already active on this thread.
     */
    public static void initDriver() {
        if (isInitialized()) {
            log.info("WebDriver already initialised — skipping");
            return;
        }
        String browser = ConfigReader.get("browser", "chrome").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless");
        int pageLoadTimeout = ConfigReader.getInt("page.load.timeout");
        int implicitWait = ConfigReader.getInt("implicit.wait");

        log.info("Initialising {} browser (headless={})", browser, headless);

        WebDriver driver = switch (browser) {
            case "firefox" -> createFirefox(headless);
            case "edge"    -> createEdge(headless);
            default        -> createChrome(headless);
        };

        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

        driverThread.set(driver);
        log.info("{} driver initialised successfully", browser);
    }

    /**
     * Returns the WebDriver for the current thread.
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThread.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialised. "
                    + "Ensure initDriver() is called in the @Before hook.");
        }
        return driver;
    }

    /**
     * Quits the WebDriver for the current thread and cleans up the ThreadLocal.
     * Call in @After hook.
     */
    public static void quitDriver() {
        WebDriver driver = driverThread.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("WebDriver quit successfully");
            } catch (Exception e) {
                log.warn("Exception while quitting WebDriver: {}", e.getMessage());
            } finally {
                driverThread.remove();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Private factory methods
    // -------------------------------------------------------------------------

    private static WebDriver createChrome(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1920,1080"
        );
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefox(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdge(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--window-size=1920,1080"
        );
        return new EdgeDriver(options);
    }
}
