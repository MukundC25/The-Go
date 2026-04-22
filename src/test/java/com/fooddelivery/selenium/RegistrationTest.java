package com.fooddelivery.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Valid registration success")
    void validRegistration() {
        driver.get(BASE_URL + "/register");
        
        String uniqueEmail = "user_" + UUID.randomUUID().toString() + "@example.com";

        driver.findElement(By.id("name")).sendKeys("Jane Doe");
        driver.findElement(By.id("email")).sendKeys(uniqueEmail);
        driver.findElement(By.id("phone")).sendKeys("9876543210");
        driver.findElement(By.id("password")).sendKeys("Password@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Password@123");
        
        driver.findElement(By.id("register-btn")).click();

        // Should redirect to Home (which has user-info if logged in)
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
            .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.id("user-info")));
        assertThat(driver.findElement(By.id("user-info")).getText()).contains("Jane Doe");
        assertThat(driver.getCurrentUrl()).isEqualTo(BASE_URL + "/");
    }

    @Test
    @DisplayName("Duplicate email rejection")
    void duplicateEmailRejection() {
        // Assume 'admin@foodapp.com' exists from Data Seed
        driver.get(BASE_URL + "/register");

        driver.findElement(By.id("name")).sendKeys("Admin Copy");
        driver.findElement(By.id("email")).sendKeys("admin@foodapp.com");
        driver.findElement(By.id("phone")).sendKeys("9876543210");
        driver.findElement(By.id("password")).sendKeys("Password@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Password@123");
        
        driver.findElement(By.id("register-btn")).click();

        // Should stay on register and show error
        assertThat(driver.findElement(By.id("error-msg")).getText()).contains("already exists");
    }
    
    @Test
    @DisplayName("Password mismatch")
    void passwordMismatch() {
        driver.get(BASE_URL + "/register");

        driver.findElement(By.id("name")).sendKeys("Admin Copy");
        driver.findElement(By.id("email")).sendKeys("admin_fail@foodapp.com");
        driver.findElement(By.id("phone")).sendKeys("9876543210");
        driver.findElement(By.id("password")).sendKeys("Password@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Password@456");
        
        driver.findElement(By.id("register-btn")).click();

        // Should stay on register and show error
        assertThat(driver.findElement(By.id("error-msg")).getText()).contains("Passwords do not match");
    }
}
