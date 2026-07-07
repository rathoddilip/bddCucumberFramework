package com.fixerra.bdd.utils;

import com.fixerra.bdd.config.ConfigReader;
import com.fixerra.bdd.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility for capturing and saving browser screenshots.
 * Used primarily in the @After Cucumber hook on scenario failure.
 */
public class ScreenshotUtil {

    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private ScreenshotUtil() {
        // utility class
    }

    /**
     * Captures a screenshot and saves it to the configured screenshots directory.
     *
     * @param scenarioName human-readable scenario name used in the filename
     * @return the absolute path of the saved screenshot, or null if capture failed
     */
    public static String capture(String scenarioName) {
        WebDriver driver;
        try {
            driver = DriverManager.getDriver();
        } catch (IllegalStateException e) {
            log.warn("Cannot capture screenshot – no active WebDriver: {}", e.getMessage());
            return null;
        }

        try {
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            String dir = ConfigReader.get("screenshot.dir", "screenshots");
            Path dirPath = Paths.get(dir);
            Files.createDirectories(dirPath);

            String safeName = sanitize(scenarioName);
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
            String filename = safeName + "_" + timestamp + ".png";

            Path filePath = dirPath.resolve(filename);
            Files.write(filePath, screenshotBytes);

            log.info("Screenshot saved: {}", filePath.toAbsolutePath());
            return filePath.toAbsolutePath().toString();

        } catch (IOException e) {
            log.error("Failed to save screenshot: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Captures the screenshot and returns the raw bytes (useful for attaching to Cucumber reports).
     */
    public static byte[] captureAsBytes() {
        try {
            WebDriver driver = DriverManager.getDriver();
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.warn("Failed to capture screenshot bytes: {}", e.getMessage());
            return new byte[0];
        }
    }

    // -------------------------------------------------------------------------

    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_")
                   .replaceAll("_{2,}", "_")
                   .toLowerCase();
    }
}
