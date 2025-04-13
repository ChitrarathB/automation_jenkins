# Cucumber-Selenium Test Framework with Extent Reports - Technical Documentation

## Architecture Overview

This project implements a comprehensive test automation framework using Cucumber, Selenium WebDriver, and Extent Reports, with a focus on generating rich visual reports even in headless environments.

### Core Components

1. **Test Layer**
   - **Cucumber BDD** - For behavior-driven test definition
   - **Gherkin Feature Files** - Human-readable test scenarios
   - **Step Definitions** - Java implementations of test steps

2. **Browser Automation**
   - **Selenium WebDriver** - Core browser automation API
   - **HtmlUnit Driver** - Headless browser implementation for Replit compatibility
   - **WebDriver Manager** - Driver lifecycle and configuration management

3. **Reporting System**
   - **Cucumber Reporter** - Native Cucumber HTML and JSON reports
   - **Extent Reports** - Enhanced HTML and PDF reporting
   - **SVG Visual Reporter** - Custom implementation for visual page state capture

## Design Decisions

### 1. Driver Selection and Configuration
The framework supports multiple drivers with environment-based auto-configuration:

#### Environment-Aware Driver Selection
- **Replit Environment**: Automatically uses HtmlUnit or headless Chrome for compatibility
- **Local Environment**: Defaults to Chrome in non-headless mode for better user experience

#### Driver Options
- **Chrome**: Full-featured browser with excellent DevTools and debugging capabilities
- **Firefox**: Alternative browser option for cross-browser testing
- **HtmlUnit**: Lightweight headless browser for speed and reliability in CI environments

#### Configuration System
- **Properties File**: `src/test/resources/driver.properties` for static configuration
- **System Properties**: Runtime configuration via -D command-line arguments
- **Environment Detection**: Automatically adapts to Replit vs. local environments

### 2. SVG-based Visual Reporting
Traditional screenshot capture is limited in HtmlUnit, so we implemented an innovative SVG-based approach:
- **Vector Graphics**: SVG provides sharp, scalable visuals for reports
- **HTML Embedding**: SVG can be embedded directly in both HTML and PDF
- **Structured Data**: Allows for organizing test information visually
- **PDF Compatibility**: Works reliably in PDF output where images can be problematic

### 3. Multi-layered Reporting
We implemented multiple reporting formats for different use cases:
- **Cucumber HTML/JSON**: For integration with CI/CD systems and historical tracking
- **Extent HTML**: For interactive, detailed test analysis
- **Extent PDF**: For formal documentation and sharing with stakeholders
- **Time-stamped Reports**: To preserve execution history

## Implementation Details

### Custom SVG Generation
The `createSvgVisual()` method in `Hooks.java` dynamically generates SVG representations:
```java
private String createSvgVisual(String... lines) {
    StringBuilder svg = new StringBuilder();
    svg.append("<svg xmlns='http://www.w3.org/2000/svg' width='800' height='400'>");
    // Add container rectangle
    svg.append("<rect width='800' height='400' fill='#f8f9fa' stroke='#ddd' stroke-width='2'/>");
    
    // Add header text
    svg.append("<text x='400' y='40' font-family='Arial' font-size='24' text-anchor='middle' fill='#2c3e50'>");
    svg.append(lines[0]);
    svg.append("</text>");
    
    // Add content lines
    int y = 80;
    for (int i = 1; i < lines.length; i++) {
        svg.append("<text x='50' y='" + y + "' font-family='Arial' font-size='16' fill='#333'>");
        svg.append(lines[i]);
        svg.append("</text>");
        y += 30;
    }
    
    svg.append("</svg>");
    return svg.toString();
}
```

### HTML Alternative to Screenshots
For browsers that don't support screenshots, we create HTML-based visual representations:
```java
private void createHtmlScreenshotAlternative(Scenario scenario, String url, String pageTitle, 
                                           String screenshotName, String prefix) {
    String html = "<div style='background:#f5f5f5; border:1px solid #ddd; border-radius:5px; padding:15px;'>" +
        "<h4 style='color:#333; border-bottom:1px solid #ddd; padding-bottom:8px;'>" + prefix + " - " + pageTitle + "</h4>" +
        "<p><strong>URL:</strong> " + url + "</p>" +
        "<p><strong>Time:</strong> " + getCurrentTime() + "</p>" +
        "</div>";
    
    scenario.attach(html.getBytes(), "text/html", screenshotName);
}
```

### Report Paths and Accessibility
To address the issue of timestamped directories making reports hard to find, we implemented a script (`generate_report.sh`) that:
1. Runs the tests using Maven
2. Locates the reports in their timestamped directories
3. Copies them to a fixed, predictable location (`target/reports/`)
4. Provides a summary of available reports

## Configuration Files

### Extent Report Configuration
The `extent.properties` file configures the Extent Report output:
```properties
extent.reporter.spark.start=true
extent.reporter.spark.out=target/extent-reports/spark-report.html

extent.reporter.pdf.start=true
extent.reporter.pdf.out=target/extent-reports/ExtentPdf.pdf

extent.reporter.spark.config=src/test/resources/extent-config.xml
```

### Cucumber Configuration
The `cucumber.properties` file configures Cucumber behavior:
```properties
cucumber.publish.quiet=true
```

## Logging System
Comprehensive logging is implemented in all components:
- **WebDriver Actions**: Browser interactions are logged
- **Test Steps**: Each step execution is recorded
- **Report Generation**: Report creation process is documented
- **Error Handling**: Full error details are captured

## Best Practices

### 1. Test Independence
Each test is designed to be independent, with proper setup and teardown:
- WebDriver is initialized before each scenario
- Browser state is cleaned up after each scenario
- Test data is managed to avoid cross-test dependencies

### 2. Error Handling
Robust error handling is implemented throughout:
- Try-catch blocks around critical operations
- Fallback mechanisms for screenshot capture
- Logging of exceptions for debugging

### 3. Visual Reporting
Strategic capture of page states:
- Before each test step
- After each test step
- Final state with detailed test results

## Future Enhancements

1. **Parallel Execution**: Configure for multi-threaded test execution
2. **Data-Driven Testing**: Enhance for parameterized testing with external data sources
3. **CI/CD Integration**: Add configuration for popular CI/CD platforms
4. **Extended Reporting**: Add more metrics and analytics to the reports
5. **API Testing**: Extend the framework to include REST API testing