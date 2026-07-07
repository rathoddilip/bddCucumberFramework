package com.fixerra.bdd.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton config reader that loads config.properties from the test classpath.
 * Usage: ConfigReader.get("browser")
 */
public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static final String CONFIG_FILE = "config.properties";
    private static ConfigReader instance;
    private final Properties properties;

    private ConfigReader() {
        properties = new Properties();
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (stream == null) {
                throw new RuntimeException("config.properties not found on classpath. "
                        + "Make sure it exists at src/test/resources/config.properties");
            }
            properties.load(stream);
            log.info("Loaded configuration from {}", CONFIG_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + CONFIG_FILE, e);
        }
    }

    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    /**
     * Returns the value for the given key.
     * System properties (e.g. -Dbrowser=firefox) override file values.
     */
    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }
        String value = getInstance().properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property '" + key + "' not found in " + CONFIG_FILE);
        }
        return value.trim();
    }

    public static String get(String key, String defaultValue) {
        try {
            return get(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
