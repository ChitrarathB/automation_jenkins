package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DriverManager;

import java.time.Duration;

/**
 * Step definitions for website navigation tests using a more automation-friendly site
 */
public class WebsiteNavigationSteps {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public WebsiteNavigationSteps() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        System.out.println("WebsiteNavigationSteps initialized with driver: " + driver.getClass().getName());
    }

    @Given("I navigate to The Internet demo site")
    public void i_navigate_to_demo_site() {
        try {
            System.out.println("Navigating to The Internet demo site...");
            driver.get("https://the-internet.herokuapp.com/");
            
            String pageTitle = driver.getTitle();
            System.out.println("Current page title: " + pageTitle);
            
            // Verify we're on the correct page
            Assertions.assertTrue(
                pageTitle.contains("The Internet") || driver.getPageSource().contains("Welcome to the-internet"), 
                "Expected to be on The Internet demo site, but page title was: " + pageTitle
            );
            
            System.out.println("Successfully navigated to The Internet demo site");
        } catch (Exception e) {
            System.err.println("Error navigating to demo site: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @When("I click on the Form Authentication link")
    public void i_click_on_form_authentication_link() {
        try {
            System.out.println("Clicking on Form Authentication link...");
            
            // Find and click the Form Authentication link
            WebElement formAuthLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.linkText("Form Authentication")));
            
            System.out.println("Found link: " + formAuthLink.getText());
            formAuthLink.click();
            
            System.out.println("Clicked on Form Authentication link");
        } catch (Exception e) {
            System.err.println("Error clicking Form Authentication link: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Then("I should be on the login page")
    public void i_should_be_on_login_page() {
        try {
            System.out.println("Verifying login page is displayed...");
            
            // Wait for login form to be present
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login")));
            
            // Verify login elements are present
            boolean usernameFieldPresent = driver.findElement(By.id("username")).isDisplayed();
            boolean passwordFieldPresent = driver.findElement(By.id("password")).isDisplayed();
            boolean loginButtonPresent = driver.findElement(By.cssSelector("button[type='submit']")).isDisplayed();
            
            System.out.println("Username field present: " + usernameFieldPresent);
            System.out.println("Password field present: " + passwordFieldPresent);
            System.out.println("Login button present: " + loginButtonPresent);
            
            // Verify expected elements are present
            Assertions.assertTrue(usernameFieldPresent, "Username field should be displayed");
            Assertions.assertTrue(passwordFieldPresent, "Password field should be displayed");
            Assertions.assertTrue(loginButtonPresent, "Login button should be displayed");
            
            System.out.println("Successfully verified login page is displayed");
        } catch (Exception e) {
            System.err.println("Error verifying login page: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @When("I enter username {string} and password {string}")
    public void i_enter_credentials(String username, String password) {
        try {
            System.out.println("Entering username and password...");
            
            // Enter username
            WebElement usernameField = driver.findElement(By.id("username"));
            usernameField.clear();
            usernameField.sendKeys(username);
            
            // Enter password
            WebElement passwordField = driver.findElement(By.id("password"));
            passwordField.clear();
            passwordField.sendKeys(password);
            
            // Click login button
            WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
            loginButton.click();
            
            System.out.println("Credentials entered and login button clicked");
        } catch (Exception e) {
            System.err.println("Error entering credentials: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Then("I should see a success message")
    public void i_should_see_success_message() {
        try {
            System.out.println("Verifying success message...");
            
            // Wait for the success message to be visible
            WebElement flashMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("flash")));
            
            String messageText = flashMessage.getText();
            System.out.println("Message displayed: " + messageText);
            
            // Verify the message contains expected text
            Assertions.assertTrue(
                messageText.contains("You logged into a secure area"), 
                "Expected success message, but got: " + messageText
            );
            
            // Verify we're on the secure page
            boolean logoutButtonPresent = driver.findElement(By.cssSelector(".button.secondary")).isDisplayed();
            Assertions.assertTrue(logoutButtonPresent, "Logout button should be displayed after successful login");
            
            System.out.println("Successfully verified login success message");
        } catch (Exception e) {
            System.err.println("Error verifying success message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
