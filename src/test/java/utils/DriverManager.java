package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * Manages WebDriver instances for the test framework.
 * Supports both local development and Replit environments.
 * Uses driver.properties for configuration.
 */
public class DriverManager {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final Properties driverProps = new Properties();
    
    // Environment detection flag
    private static final boolean IS_REPLIT = System.getenv("REPL_ID") != null;
    
    // Configuration properties with defaults
    private static String browserType = "chrome";
    private static boolean headlessMode = false;
    private static int windowWidth = 1920;
    private static int windowHeight = 1080;
    private static int screenshotInterval = 1;

    static {
        // Load driver configuration
        loadDriverProperties();
    }

    private DriverManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Load driver properties from the configuration file
     */
    private static void loadDriverProperties() {
        try {
            InputStream input = DriverManager.class.getClassLoader().getResourceAsStream("driver.properties");
            
            if (input != null) {
                driverProps.load(input);
                input.close();
                
                // Set properties from configuration
                browserType = System.getProperty("browser", 
                              driverProps.getProperty("browser", "chrome"));
                              
                boolean propHeadless = Boolean.parseBoolean(
                              driverProps.getProperty("headless", "false"));
                headlessMode = Boolean.parseBoolean(System.getProperty("headless", 
                               Boolean.toString(IS_REPLIT || propHeadless)));
                               
                windowWidth = Integer.parseInt(
                              driverProps.getProperty("window.width", "1920"));
                windowHeight = Integer.parseInt(
                               driverProps.getProperty("window.height", "1080"));
                screenshotInterval = Integer.parseInt(
                                    driverProps.getProperty("screenshot.interval", "1"));
                
                System.out.println("Driver properties loaded successfully");
            } else {
                System.out.println("Driver properties file not found, using defaults");
            }
        } catch (IOException e) {
            System.err.println("Error loading driver properties: " + e.getMessage());
        }
    }

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }

    public static void initializeDriver() {
        System.out.println("Environment: " + (IS_REPLIT ? "Replit" : "Local"));
        System.out.println("Browser: " + browserType);
        System.out.println("Headless mode: " + headlessMode);
        System.out.println("Window size: " + windowWidth + "x" + windowHeight);
        
        try {
            switch (browserType.toLowerCase()) {
                case "chrome":
                    initializeChromeDriver();
                    break;
                case "firefox":
                    initializeFirefoxDriver();
                    break;
                case "htmlunit":
                    initializeHtmlUnitDriver();
                    break;
                default:
                    System.out.println("Unsupported browser specified, defaulting to Chrome");
                    initializeChromeDriver();
            }
        } catch (Exception e) {
            System.err.println("Error initializing primary WebDriver: " + e.getMessage());
            System.out.println("Falling back to HtmlUnit driver");
            
            try {
                initializeHtmlUnitDriver();
            } catch (Exception e2) {
                System.err.println("Error initializing fallback WebDriver: " + e2.getMessage());
                e2.printStackTrace();
                throw new RuntimeException("WebDriver initialization failed", e2);
            }
        }
    }
    
    private static void initializeChromeDriver() {
        System.out.println("Initializing ChromeDriver" + (headlessMode ? " in headless mode" : ""));
        
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        // Common options for stability
        options.addArguments("--remote-allow-origins=*");
        
        // Headless mode if required (Replit environment or explicitly set)
        //if (headlessMode) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
        //}
        
        // Extra options for containerized environments like Replit
        if (IS_REPLIT) {
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }
        
        WebDriver chromeDriver = new ChromeDriver(options);
        
        // Set browser window size
        chromeDriver.manage().window().setSize(new Dimension(windowWidth, windowHeight));
        
        // Set timeouts
        chromeDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        chromeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        
        System.out.println("ChromeDriver successfully initialized with window size: " 
                          + chromeDriver.manage().window().getSize());
        driver.set(chromeDriver);
    }
    
    private static void initializeFirefoxDriver() {
        System.out.println("Initializing FirefoxDriver" + (headlessMode ? " in headless mode" : ""));
        
        WebDriverManager.firefoxdriver().setup();
        
        FirefoxOptions options = new FirefoxOptions();
        
        // Headless mode if required
        if (headlessMode) {
            options.addArguments("--headless");
        }
        
        WebDriver firefoxDriver = new FirefoxDriver(options);
        
        // Set browser window size
        firefoxDriver.manage().window().setSize(new Dimension(windowWidth, windowHeight));
        
        // Set timeouts
        firefoxDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        firefoxDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        
        System.out.println("FirefoxDriver successfully initialized with window size: " 
                          + firefoxDriver.manage().window().getSize());
        driver.set(firefoxDriver);
    }
    
    private static void initializeHtmlUnitDriver() {
        System.out.println("Initializing HtmlUnitDriver");
        
        // Create a new HtmlUnit driver with JavaScript enabled
        HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(true);
        htmlUnitDriver.setJavascriptEnabled(true);
        
        // Set browser window size
        htmlUnitDriver.manage().window().setSize(new Dimension(windowWidth, windowHeight));
        
        // Set timeouts
        htmlUnitDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        htmlUnitDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        
        System.out.println("HtmlUnitDriver successfully initialized");
        driver.set(htmlUnitDriver);
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            try {
                System.out.println("Quitting WebDriver");
                driver.get().quit();
                driver.remove();
                System.out.println("WebDriver successfully closed");
            } catch (Exception e) {
                System.err.println("Error quitting WebDriver: " + e.getMessage());
            }
        }
    }
    
    /**
     * Checks if the current WebDriver can take screenshots.
     * This is useful for conditional screenshot logic.
     * 
     * @return true if the driver can take screenshots, false otherwise
     */
    public static boolean canTakeScreenshots() {
        return !(driver.get() instanceof HtmlUnitDriver);
    }
    
    /**
     * Determines if we're running in a Replit environment.
     * 
     * @return true if running in Replit, false otherwise
     */
    public static boolean isReplitEnvironment() {
        return IS_REPLIT;
    }
    
    /**
     * Get the configured screenshot interval in seconds
     * 
     * @return screenshot interval
     */
    public static int getScreenshotInterval() {
        return screenshotInterval;
    }
}
