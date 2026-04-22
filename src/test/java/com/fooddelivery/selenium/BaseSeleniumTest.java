package com.fooddelivery.selenium;

import com.fooddelivery.web.WebApp;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import java.time.Duration;

public abstract class BaseSeleniumTest {

    // Upgraded to static: Shares ONE singular browser window globally across ALL tests
    protected static WebDriver driver; 
    private static boolean singletonBooted = false;
    
    protected static final int PORT = 7777;
    protected static final String BASE_URL = "http://localhost:" + PORT;

    @BeforeAll
    static void classSetup() {
        // Boot the system exactly once. No port collisions, no multiple windows!
        if (!singletonBooted) {
            WebDriverManager.chromedriver().setup();
            WebApp.start(PORT);

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--no-sandbox");

            WebDriver coreDriver = new ChromeDriver(options);
            coreDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            coreDriver.manage().window().maximize();

            // Create a custom logic layer to artificially slow down the robot to human speed!
            WebDriverListener humanizer = new WebDriverListener() {
                @Override
                public void beforeClick(WebElement element) { sleep(1500); } // Wait 1.5s before every click
                @Override
                public void beforeSendKeys(WebElement element, CharSequence... keys) { sleep(1500); } // Wait 1.5s before typing
                @Override
                public void beforeGet(WebDriver driver, String url) { sleep(2000); } // Wait 2s before loading a page
                @Override
                public void beforeFindElement(WebDriver driver, org.openqa.selenium.By locator) { sleep(500); } // Small pause while searching
                
                private void sleep(int ms) {
                    try { Thread.sleep(ms); } catch (Exception e) {}
                }
            };
            
            // Wrap the driver with our slowdown logic
            driver = new EventFiringDecorator<>(humanizer).decorate(coreDriver);
            singletonBooted = true;
            
            // Note: We purposely do NOT attach a driver.quit() shutdown hook 
            // because you specifically requested to never forcefully close it at the end!
        }
    }

    @AfterAll
    static void classTearDown() {
        // Safely overridden to prevent Javalin/Chrome from tearing down natively between test suites
    }

    @BeforeEach
    void setUp() {
        // Purging cookies instantly simulates having a "fresh browser" without naturally closing it
        if (driver != null) {
            driver.manage().deleteAllCookies(); 
        }
    }

    @AfterEach
    void tearDown() {
        // Pause 1 full second after every single test finishes so you can comfortably analyze it
        try { Thread.sleep(1000); } catch (Exception e) {}
    }
}
