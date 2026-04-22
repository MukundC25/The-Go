package com.fooddelivery.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AdminDashboardTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Admin login and dashboard access")
    void adminDashboardAccess() {
        driver.get(BASE_URL + "/login");
        
        driver.findElement(By.id("email")).sendKeys("admin@foodapp.com");
        driver.findElement(By.id("password")).sendKeys("Admin@123!");
        driver.findElement(By.id("login-btn")).click();

        // Admin has access to dashboard
        driver.get(BASE_URL + "/admin");

        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
            .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.id("admin-stats")));
        assertThat(driver.findElement(By.id("admin-stats")).getText()).contains("Users:");
    }

    @Test
    @DisplayName("Unauthorized access redirect")
    void unauthorizedAccessRedirect() {
        // Register a normal user
        driver.get(BASE_URL + "/register");
        String uniqueEmail = UUID.randomUUID().toString() + "@example.com";
        driver.findElement(By.id("name")).sendKeys("Normal User");
        driver.findElement(By.id("email")).sendKeys(uniqueEmail);
        driver.findElement(By.id("phone")).sendKeys("9876543210");
        driver.findElement(By.id("password")).sendKeys("Password@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Password@123");
        driver.findElement(By.id("register-btn")).click();

        // Ensure we are logged in by checking url
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(2))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlToBe(BASE_URL + "/"));
        assertThat(driver.getCurrentUrl()).isEqualTo(BASE_URL + "/");

        // Try to access admin dashboard
        driver.get(BASE_URL + "/admin");

        // Should return an error message
        assertThat(driver.findElement(By.id("error-heading")).getText()).contains("Error");
        assertThat(driver.findElement(By.id("error-msg")).getText()).contains("Access denied");
    }
}
