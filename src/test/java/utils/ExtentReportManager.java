package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager {
    private static ExtentReports extent;
    private static final String REPORT_DIR = "target/extent-reports/";
    
    private ExtentReportManager() {
        // Private constructor to prevent instantiation
    }
    
    public static synchronized ExtentReports getReportInstance() {
        if (extent == null) {
            createReportDir();
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String reportName = "TestReport_" + timeStamp;
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_DIR + reportName + ".html")
                    .viewConfigurer()
                    .viewOrder()
                    .as(new ViewName[] {
                            ViewName.DASHBOARD,
                            ViewName.TEST,
                            ViewName.AUTHOR,
                            ViewName.DEVICE,
                            ViewName.EXCEPTION,
                            ViewName.LOG
                    })
                    .apply();

            // Configure the report appearance
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("Cucumber Automation Test Report");
            sparkReporter.config().setReportName("Website Navigation Test Execution Report");
            sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");

            // Create and customize ExtentReports
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Browser", System.getProperty("browser", "Chrome"));
            extent.setSystemInfo("Environment", "Test");
        }
        return extent;
    }

    private static void createReportDir() {
        File directory = new File(REPORT_DIR);
        if (!directory.exists()) {
            boolean dirCreated = directory.mkdirs();
            if (!dirCreated) {
                System.err.println("Failed to create report directory: " + REPORT_DIR);
            }
        }
    }
}
