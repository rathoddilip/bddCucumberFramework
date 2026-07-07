package com.fixerra.bdd.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Cucumber JUnit Platform Suite Runner.
 *
 * Run ALL scenarios:
 *   mvn test
 *
 * Run only @smoke scenarios:
 *   mvn test -Dcucumber.filter.tags="@smoke"
 *
 * Run with Firefox:
 *   mvn test -Dbrowser=firefox
 *
 * Run in headless mode:
 *   mvn test -Dheadless=true
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty,"
              + "html:reports/cucumber-report.html,"
              + "json:reports/cucumber.json,"
              + "timeline:reports/timeline"
)
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "com.fixerra.bdd"
)
@ConfigurationParameter(
        key = FILTER_TAGS_PROPERTY_NAME,
        value = "not @wip"
)
@ConfigurationParameter(
        key = FEATURES_PROPERTY_NAME,
        value = "src/test/resources/features"
)
public class TestRunner {
    // JUnit Platform Suite — no code required here.
    // All configuration is declared via annotations above.
}
