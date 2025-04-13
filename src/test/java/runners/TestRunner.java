package runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "stepdefinitions,hooks")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/cucumber-html-report.html, json:target/cucumber-reports/cucumber-json-report.json, com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:")
//@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@test")
public class TestRunner {
    // This class is empty, all configuration is done through annotations
    // For report generation, use the ScreenshotReportGenerator utility class
}
