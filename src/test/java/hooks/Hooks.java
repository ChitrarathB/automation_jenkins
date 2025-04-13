package hooks;

import io.cucumber.java.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import utils.DriverManager;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Hooks for Cucumber test execution.
 * Handles WebDriver initialization and screenshot capture.
 */
public class Hooks {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Before
    public void setUp(Scenario scenario) {
        System.out.println("Starting scenario: " + scenario.getName());
        // Initialize the WebDriver
        DriverManager.initializeDriver();
        
        // Add test start information
        scenario.attach(
            ("<div style='background:#f8f9fa; padding:15px; border-radius:5px; border:1px solid #ddd;'>" +
            "<h3 style='color:#2c3e50;'>Test Started</h3>" +
            "<p><strong>Scenario:</strong> " + scenario.getName() + "</p>" +
            "<p><strong>Time:</strong> " + getCurrentTime() + "</p>" +
            "</div>").getBytes(),
            "text/html", 
            "scenario_start_info"
        );
    }
    
    @BeforeStep
    public void beforeStep(Scenario scenario) {
        System.out.println("Executing step in scenario: " + scenario.getName());
        // Take screenshot before each step
        takeScreenshot(scenario, "Before_Step");
    }
    
    @AfterStep
    public void afterStep(Scenario scenario) {
        System.out.println("Completed step with status: " + scenario.getStatus());
        // Take screenshot after each step
        takeScreenshot(scenario, "After_Step");
    }

    @After
    public void tearDown(Scenario scenario) {
        System.out.println("Scenario " + scenario.getName() + " ended with status: " + scenario.getStatus());
        
        // Take final screenshot
        takeFinalScreenshot(scenario);
        
        // Quit the driver
        DriverManager.quitDriver();
    }

    @AfterAll
    public static void afterAll() {
        // Ensure all drivers are closed
        DriverManager.quitDriver();
        System.out.println("Test execution completed - all drivers have been closed");
        
        // Find and log the path to the reports
        System.out.println("\n==== REPORTS LOCATION ====");
        try {
            // Find the latest extent-reports directory
            File targetDir = new File("target");
            if (targetDir.exists()) {
                File[] extentDirs = targetDir.listFiles(f -> f.isDirectory() && f.getName().startsWith("extent-reports"));
                if (extentDirs != null && extentDirs.length > 0) {
                    // Sort by last modified to get the most recent
                    Arrays.sort(extentDirs, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
                    File latestDir = extentDirs[0];
                    
                    System.out.println("Report Directory: " + latestDir.getAbsolutePath());
                    
                    // Look for the PDF report
                    File pdfReport = new File(latestDir, "ExtentPdf.pdf");
                    if (pdfReport.exists()) {
                        System.out.println("PDF Report: " + pdfReport.getAbsolutePath() + " (Size: " + pdfReport.length() + " bytes)");
                    } else {
                        System.out.println("PDF Report not found in the expected location");
                        // Try to find any PDF file
                        findFilesByExtension(latestDir, ".pdf").forEach(file -> 
                            System.out.println("Found PDF: " + file.getAbsolutePath()));
                    }
                    
                    // Look for HTML report
                    File htmlReport = new File(latestDir, "target/extent-reports/spark-report.html");
                    if (htmlReport.exists()) {
                        System.out.println("HTML Report: " + htmlReport.getAbsolutePath());
                    } else {
                        System.out.println("HTML Report not found in the expected location");
                        // Try to find any HTML file
                        findFilesByExtension(latestDir, ".html").forEach(file -> 
                            System.out.println("Found HTML: " + file.getAbsolutePath()));
                    }
                } else {
                    System.out.println("No extent-reports directory found");
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding reports: " + e.getMessage());
        }
        System.out.println("=========================\n");
    }
    
    /**
     * Helper method to find files by extension
     */
    private static List<File> findFilesByExtension(File directory, String extension) {
        List<File> result = new ArrayList<>();
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        result.addAll(findFilesByExtension(file, extension));
                    } else if (file.getName().endsWith(extension)) {
                        result.add(file);
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Takes a screenshot and attaches it to the scenario
     */
    private void takeScreenshot(Scenario scenario, String prefix) {
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            try {
                String currentUrl = driver.getCurrentUrl();
                String pageTitle = driver.getTitle();
                
                // Create a descriptive name for the screenshot
                String screenshotName = prefix + "_" + scenario.getName().replaceAll("\\s+", "_");
                
                if (driver instanceof TakesScreenshot && DriverManager.canTakeScreenshots()) {
                    // Try to take actual screenshot for browsers that support it
                    try {
                        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                        scenario.attach(screenshot, "image/png", screenshotName);
                        System.out.println("Screenshot captured: " + screenshotName);
                        
                        // For enhanced reporting, include an HTML caption below the screenshot
                        String caption = "<div style='background:#f8f9fa; padding:10px; border-radius:3px; margin-top:5px;'>" +
                            "<p><strong>URL:</strong> " + currentUrl + "</p>" +
                            "<p><strong>Title:</strong> " + pageTitle + "</p>" +
                            "<p><small>Time: " + getCurrentTime() + "</small></p>" +
                            "</div>";
                        scenario.attach(caption.getBytes(), "text/html", screenshotName + "_details");
                        
                    } catch (Exception e) {
                        System.err.println("Failed to capture screenshot: " + e.getMessage());
                        createHtmlScreenshotAlternative(scenario, currentUrl, pageTitle, screenshotName, prefix);
                    }
                } else {
                    // For HtmlUnit or other drivers that can't take screenshots, 
                    // create a visual HTML representation instead
                    createHtmlScreenshotAlternative(scenario, currentUrl, pageTitle, screenshotName, prefix);
                }
                
            } catch (Exception e) {
                System.err.println("Error capturing screenshot: " + e.getMessage());
            }
        }
    }
    
    /**
     * Takes the final screenshot with more detailed information
     */
    private void takeFinalScreenshot(Scenario scenario) {
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            try {
                String currentUrl = driver.getCurrentUrl();
                String pageTitle = driver.getTitle();
                String screenshotName = "Final_State_" + scenario.getName().replaceAll("\\s+", "_");
                
                // Create an SVG-based image that looks better in PDF
                String svgImage = createSvgVisual(
                    "FINAL STATE - " + scenario.getStatus(),
                    "URL: " + currentUrl, 
                    "Title: " + pageTitle,
                    "Status: " + scenario.getStatus(),
                    "Time: " + getCurrentTime()
                );
                
                // Encode the SVG as a base64 data URL
                String base64Svg = Base64.getEncoder().encodeToString(svgImage.getBytes());
                String dataUrl = "data:image/svg+xml;base64," + base64Svg;
                
                // Create HTML that will render as an image in the PDF
                String html = "<div style='text-align:center;'><img src='" + dataUrl + "' width='700' height='350'/></div>";
                scenario.attach(html.getBytes(), "text/html", screenshotName);
                
                // Also add a final summary in text format
                String finalSummary = "<div style='background:#e8f4f8; padding:15px; border-radius:5px; border:1px solid #bcd; margin:20px 0;'>" +
                    "<h3 style='color:#245; border-bottom:1px solid #bcd; padding-bottom:8px;'>Test Completed: " + scenario.getStatus() + "</h3>" +
                    "<p><strong>URL:</strong> " + currentUrl + "</p>" +
                    "<p><strong>Page Title:</strong> " + pageTitle + "</p>" +
                    "<p><strong>Time:</strong> " + getCurrentTime() + "</p>" +
                    "<p><strong>Final Status:</strong> <span style='color:" + 
                    (scenario.isFailed() ? "red" : "green") + ";font-weight:bold;'>" + 
                    scenario.getStatus() + "</span></p>" +
                    "</div>";
                
                scenario.attach(finalSummary.getBytes(), "text/html", "final_summary");
                
            } catch (Exception e) {
                System.err.println("Error capturing final screenshot: " + e.getMessage());
            }
        }
    }
    
    /**
     * Creates an HTML alternative to actual screenshots for HtmlUnit
     */
    private void createHtmlScreenshotAlternative(Scenario scenario, String url, String pageTitle, 
                                                String screenshotName, String prefix) {
        // Create a visual HTML representation that will look better than plain text
        String html = "<div style='background:#f5f5f5; border:1px solid #ddd; border-radius:5px; padding:15px; margin:10px 0;'>" +
            "<h4 style='color:#333; border-bottom:1px solid #ddd; padding-bottom:8px;'>" + prefix + " - " + pageTitle + "</h4>" +
            "<p><strong>URL:</strong> " + url + "</p>" +
            "<p><strong>Time:</strong> " + getCurrentTime() + "</p>" +
            "</div>";
        
        scenario.attach(html.getBytes(), "text/html", screenshotName);
    }
    
    /**
     * Creates an SVG visual representation that will display well in PDF
     */
    private String createSvgVisual(String... lines) {
        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns='http://www.w3.org/2000/svg' width='800' height='400'>");
        svg.append("<rect width='800' height='400' fill='#f8f9fa' stroke='#ddd' stroke-width='2'/>");
        
        // Add a header
        svg.append("<text x='400' y='40' font-family='Arial' font-size='24' text-anchor='middle' fill='#2c3e50'>");
        svg.append(lines[0]);
        svg.append("</text>");
        
        // Add lines of text
        int y = 80;
        for (int i = 1; i < lines.length; i++) {
            svg.append("<text x='50' y='" + y + "' font-family='Arial' font-size='16' fill='#333'>");
            svg.append(lines[i]);
            svg.append("</text>");
            y += 30;
        }
        
        // Add a border
        svg.append("<rect width='796' height='396' x='2' y='2' fill='none' stroke='#3498db' stroke-width='2'/>");
        
        svg.append("</svg>");
        return svg.toString();
    }
    
    /**
     * Helper to get current formatted time
     */
    private String getCurrentTime() {
        return FORMATTER.format(LocalDateTime.now());
    }
}
