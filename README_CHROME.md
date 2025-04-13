# Running Tests with Chrome Browser

This document provides detailed instructions for running the Cucumber-Selenium tests with Chrome browser on your local machine.

## Prerequisites

1. **Java JDK 11** or higher installed
2. **Maven** installed and configured
3. **Chrome browser** installed on your system
4. **Git** to clone the repository

## Setup Instructions

1. Clone the repository to your local machine:
   ```
   git clone <repository-url>
   cd <repository-directory>
   ```

2. Install dependencies:
   ```
   mvn clean install -DskipTests
   ```

## Running Tests with Chrome

### Default Mode (Non-Headless)

By default, when running on a local machine, the tests will use Chrome in non-headless mode, which means you'll see the browser window as tests execute:

```
mvn clean test
```

This provides the benefit of seeing the test steps in real-time and also captures actual screenshots.

### Configuration Options

#### Running with Headless Chrome

If you prefer to run Chrome in headless mode (no browser window):

```
mvn clean test -Dheadless=true
```

#### Specifying Browser Window Size

To customize the browser window size:

```
mvn clean test -Dwindow.width=1366 -Dwindow.height=768
```

#### Using Firefox Instead of Chrome

The framework also supports Firefox:

```
mvn clean test -Dbrowser=firefox
```

## Understanding the Screenshots

When running with Chrome, the framework captures actual screenshots at these points:

1. Before each test step
2. After each test step
3. Final state at the end of the test

These screenshots are embedded in both the HTML and PDF reports.

## Viewing Test Reports

After test execution, you can find the reports in:

```
target/reports/
```

Key report files:
- **test-report.pdf**: PDF report with embedded screenshots
- **test-report.html**: HTML report with embedded screenshots
- **cucumber-report.html**: Standard Cucumber HTML report

## Customizing Default Settings

You can customize the default settings by editing the `src/test/resources/driver.properties` file:

```properties
# Choose browser type: chrome, firefox, or htmlunit
browser=chrome

# Set to true for headless mode, false for normal browser window
headless=false

# Browser window dimensions
window.width=1920
window.height=1080

# Screenshot interval in seconds
screenshot.interval=1
```

## Troubleshooting

### Chrome Driver Issues

If you encounter issues with Chrome driver:

1. **Driver Version Mismatch**: The framework uses WebDriverManager to automatically download the appropriate driver version, but sometimes there might be compatibility issues. Try updating your Chrome browser to the latest version.

2. **Permission Issues**: On Linux/Mac, you might need to set execute permissions:
   ```
   chmod +x ~/.cache/selenium/chromedriver/linux64/xxx.xx.xx/chromedriver
   ```

3. **Chrome Not Found**: Ensure Chrome is installed in the default location or set the path manually.

### Screenshot Not Working

If screenshots are not being captured:

1. Ensure you're not using HtmlUnit driver (`-Dbrowser=htmlunit`), which doesn't support screenshots
2. Check for any browser security settings that might be blocking screenshot capabilities
3. Try running with default settings to see if the issue persists

## Tips for Better Results

1. **Clean Target Directory**: Always use `mvn clean test` instead of just `mvn test` to ensure you start with a clean environment
2. **Close Existing Chrome Instances**: Having too many Chrome instances running can sometimes cause issues
3. **Memory Settings**: If you have a large test suite, consider increasing Maven memory: `export MAVEN_OPTS="-Xmx1024m"`