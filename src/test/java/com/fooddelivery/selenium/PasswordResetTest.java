package com.fooddelivery.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC_AUTH_008: Password Reset Flow
 * Validates the forgot-password / reset-password full flow.
 */
class PasswordResetTest extends BaseSeleniumTest {

    @Test
    @DisplayName("TC_AUTH_008-A: Forgot password page loads")
    void forgotPasswordPageLoads() {
        driver.get(BASE_URL + "/forgot-password");
        assertThat(driver.findElement(By.id("send-reset-btn")).isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("TC_AUTH_008-B: Invalid email shows error")
    void invalidEmailShowsError() {
        driver.get(BASE_URL + "/forgot-password");
        driver.findElement(By.id("reset-email")).sendKeys("nonexistent@nowhere.com");
        driver.findElement(By.id("send-reset-btn")).click();

        // Should show error message (user not found)
        new WebDriverWait(driver, Duration.ofSeconds(3))
            .until(ExpectedConditions.presenceOfElementLocated(By.id("reset-msg")));
        assertThat(driver.findElement(By.id("reset-msg")).getText()).containsIgnoringCase("error");
    }

    @Test
    @DisplayName("TC_AUTH_008-C: Valid email shows reset link")
    void validEmailShowsResetLink() {
        driver.get(BASE_URL + "/forgot-password");
        driver.findElement(By.id("reset-email")).sendKeys("admin@foodapp.com");
        driver.findElement(By.id("send-reset-btn")).click();

        new WebDriverWait(driver, Duration.ofSeconds(3))
            .until(ExpectedConditions.presenceOfElementLocated(By.id("reset-msg")));
        String msg = driver.findElement(By.id("reset-msg")).getText();
        assertThat(msg).containsIgnoringCase("reset");
    }

    @Test
    @DisplayName("TC_AUTH_008-D: Reset with mismatching passwords shows error")
    void mismatchedPasswordsShowError() {
        driver.get(BASE_URL + "/reset-password");
        driver.findElement(By.id("new-password")).sendKeys("NewPass@123!");
        driver.findElement(By.id("confirm-password")).sendKeys("DifferentPass@99!");
        driver.findElement(By.id("reset-btn")).click();

        new WebDriverWait(driver, Duration.ofSeconds(3))
            .until(ExpectedConditions.presenceOfElementLocated(By.id("error-msg")));
        assertThat(driver.findElement(By.id("error-msg")).getText()).containsIgnoringCase("match");
    }
}
