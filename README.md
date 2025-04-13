# Cucumber Selenium Automation Framework with Extent Reports

## Overview
This project is a comprehensive Cucumber-Selenium test automation framework that utilizes the Extent Reports for advanced HTML and PDF reporting. The sample test demonstrates login functionality on a demo website.

## Key Achievements
- **BDD Framework**: Created a complete Behavior-Driven Development framework with clear separation of concerns
- **Headless Testing**: Configured for headless execution in Replit using HtmlUnit driver
- **Comprehensive Reporting**: Integrated multiple report formats (HTML, JSON, PDF) with timestamps and page state capture
- **Robust Error Handling**: Implemented extensive error handling and detailed logging for better debugging
- **Modular Design**: Built with maintainability and extensibility in mind

## Features
- **Cucumber BDD**: Uses Gherkin feature files for readable test scenarios
- **Selenium WebDriver**: For browser automation
- **HtmlUnit Driver**: Headless browser for Replit compatibility
- **Extent Reports**: Generates detailed HTML and PDF reports
- **SVG-based Visual Reporting**: Creates visual representations of page states in reports
- **Multi-format Reports**: Cucumber HTML/JSON and Extent HTML/PDF reports
- **Timestamped Reports**: Preserves test execution history
- **Maven**: For dependency management and test execution

## Project Structure
- **Feature files**: Located in `src/test/resources/features`
- **Step Definitions**: `src/test/java/stepdefinitions`
- **Test Runner**: `src/test/java/runners`
- **WebDriver Management**: `src/test/java/utils`
- **Report Configuration**: `src/test/resources`

## Running the Tests

### Using the Script (Recommended)
Run the tests using our custom script which also copies the reports to an easily accessible location:
```
bash generate_report.sh
```

### Using Maven Directly
Alternatively, run the tests directly using Maven:
```
mvn clean test
```

### Running Locally with Chrome
When running on your local machine, the framework will detect that it's not in Replit and will automatically use Chrome in non-headless mode, allowing you to see the browser in action and capture actual screenshots.

#### Configuration Options
You can customize the browser behavior through system properties:

**Selecting a different browser:**
```
mvn clean test -Dbrowser=firefox
```

**Forcing headless mode even locally:**
```
mvn clean test -Dheadless=true
```

**Using both options:**
```
mvn clean test -Dbrowser=chrome -Dheadless=true
```

#### Local Configuration File
You can also edit the `src/test/resources/driver.properties` file to set your preferred defaults:

```properties
# Available browsers: chrome, firefox, htmlunit
browser=chrome

# true for headless, false for normal browser window
headless=false

# Browser window size
window.width=1920
window.height=1080
```

## Generated Reports
After execution, reports can be found in two locations:

### Easy Access Reports (Recommended)
Reports are automatically copied to a fixed location for easy access:
- **Cucumber HTML Report**: `target/reports/cucumber-report.html`
- **Cucumber JSON Report**: `target/reports/cucumber-report.json`
- **Extent HTML Report**: `target/reports/test-report.html`
- **Extent PDF Report**: `target/reports/test-report.pdf`

### Original Report Locations
Reports are also available in their original locations:
- **Cucumber HTML Report**: `target/cucumber-reports/cucumber-html-report.html`
- **Cucumber JSON Report**: `target/cucumber-reports/cucumber-json-report.json`
- **Extent HTML Report**: `target/extent-reports <timestamp>/target/extent-reports/spark-report.html`
- **Extent PDF Report**: `target/extent-reports <timestamp>/target/extent-reports/ExtentPdf.pdf`

Note: The Extent Reports are generated in timestamped directories to avoid overwriting previous reports.

## Tested Scenario
The automation test performs the following steps:
1. Navigate to "The Internet" demo site
2. Click on the Form Authentication link
3. Verify login page is displayed with appropriate form elements
4. Enter username and password credentials
5. Verify successful login with confirmation message

## Visual Reporting Approach
This framework implements a sophisticated approach to visual reporting that works even with headless browsers:

### SVG-based Visual Representations
- Instead of traditional screenshots (which are limited in headless environments like HtmlUnit), we generate SVG-based visual representations of page states
- These visual elements are embedded directly in both HTML and PDF reports
- The SVG graphics include critical test information such as:
  - Page URL
  - Page title
  - Test step status
  - Timestamp
  - Additional context information

### HTML Enrichment
- HTML reports are enriched with formatted, styled elements
- Color-coded status indicators help quickly identify test results
- Structured information makes reports readable and accessible

### Benefits
- Consistent visual representation across all report formats
- PDF reports with embedded visuals that accurately represent test execution
- Complete test history with timestamped execution details
- Works reliably in headless environments where traditional screenshots aren't available

