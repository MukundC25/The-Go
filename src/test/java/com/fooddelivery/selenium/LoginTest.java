package com.fooddelivery.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

class LoginTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Successful login")
    void successfulLogin() {
        driver.get(BASE_URL + "/login");
        
        driver.findElement(By.id("email")).sendKeys("admin@foodapp.com");
        driver.findElement(By.id("password")).sendKeys("Admin@123!");
        driver.findElement(By.id("login-btn")).click();

        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.id("user-info")));
        } catch (Exception e) {
            System.err.println("LoginTest.successfulLogin FAILED! URL = " + driver.getCurrentUrl());
            System.err.println("PAGE SOURCE: \n" + driver.getPageSource());
            throw e;
        }
        assertThat(driver.findElement(By.id("user-info")).getText()).contains("Admin User");
        assertThat(driver.getCurrentUrl()).isEqualTo(BASE_URL + "/");
    }

    @Test
    @DisplayName("Wrong password error")
    void wrongPasswordError() {
        driver.get(BASE_URL + "/login");
        
        driver.findElement(By.id("email")).sendKeys("admin@foodapp.com");
        driver.findElement(By.id("password")).sendKeys("WrongPass!");
        driver.findElement(By.id("login-btn")).click();

        assertThat(driver.findElement(By.id("error-msg")).getText()).contains("Invalid");
    }

    @Test
    @DisplayName("Non-existent email error")
    void nonExistentEmailError() {
        driver.get(BASE_URL + "/login");
        
        driver.findElement(By.id("email")).sendKeys("nonexistent@foodapp.com");
        driver.findElement(By.id("password")).sendKeys("Admin@123!");
        driver.findElement(By.id("login-btn")).click();

        assertThat(driver.findElement(By.id("error-msg")).getText()).contains("Invalid email or password");
    }
}
