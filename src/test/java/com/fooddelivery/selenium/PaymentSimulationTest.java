package com.fooddelivery.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Payment Simulation Tests — validates all payment method UI flows.
 * Covers payment methods: COD, UPI, Credit Card, Net Banking, Wallet.
 */
class PaymentSimulationTest extends BaseSeleniumTest {

    @BeforeEach
    void loginAndAddToCart() {
        // Login
        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("email")).sendKeys("admin@foodapp.com");
        driver.findElement(By.id("password")).sendKeys("Admin@123!");
        driver.findElement(By.id("login-btn")).click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.urlToBe(BASE_URL + "/"));

        // Wait for restaurants to load and click first one
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.presenceOfElementLocated(By.className("view-rest")));
        driver.findElement(By.className("view-rest")).click();

        // Wait for restaurant page and add to cart
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".add-to-cart-btn")));
        driver.findElement(By.cssSelector(".add-to-cart-btn")).click();

        // Wait for redirect to cart
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.urlContains("/cart"));
    }

    @Test
    @DisplayName("PAY_001: Checkout page shows all payment options")
    void checkoutShowsAllPaymentOptions() {
        driver.get(BASE_URL + "/checkout");
        new WebDriverWait(driver, Duration.ofSeconds(3))
            .until(ExpectedConditions.presenceOfElementLocated(By.id("place-order-btn")));

        // Verify all payment radio buttons are present
        assertThat(driver.findElements(By.name("paymentMethod")).size()).isGreaterThanOrEqualTo(4);
        assertThat(driver.findElement(By.id("address")).isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("PAY_002: UPI section appears when UPI is selected")
    void upiSectionAppearsOnSelection() {
        driver.get(BASE_URL + "/checkout");
        new WebDriverWait(driver, Duration.ofSeconds(3))
            .until(ExpectedConditions.presenceOfElementLocated(By.id("upi-section")));

        // Click UPI radio (index 1)
        driver.findElements(By.name("paymentMethod")).get(1).click();
        // UPI section should be visible
        new WebDriverWait(driver, Duration.ofSeconds(2))
            .until(ExpectedConditions.not(
                ExpectedConditions.attributeContains(By.id("upi-section"), "class", "hidden")));
        assertThat(driver.findElement(By.id("upi-section")).isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("PAY_003: Card section appears when Credit Card is selected")
    void cardSectionAppearsOnSelection() {
        driver.get(BASE_URL + "/checkout");
        new WebDriverWait(driver, Duration.ofSeconds(3))
            .until(ExpectedConditions.presenceOfElementLocated(By.id("card-section")));

        driver.findElements(By.name("paymentMethod")).get(2).click();
        new WebDriverWait(driver, Duration.ofSeconds(2))
            .until(ExpectedConditions.not(
                ExpectedConditions.attributeContains(By.id("card-section"), "class", "hidden")));
        assertThat(driver.findElement(By.id("card-section")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.id("card-number")).isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("PAY_004: COD payment completes order successfully")
    void codPaymentCompletesOrder() {
        driver.get(BASE_URL + "/checkout");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("address")));
        } catch (Exception e) {
            System.err.println("codPaymentCompletesOrder FAILED! URL = " + driver.getCurrentUrl());
            System.err.println("codPaymentCompletesOrder PAGE SOURCE: \n" + driver.getPageSource());
            throw e;
        }

        driver.findElement(By.id("address")).sendKeys("Flat 5, MG Road, Mumbai 400001");
        // COD is default selected
        driver.findElement(By.id("place-order-btn")).click();

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.urlContains("/orders/"));
        assertThat(driver.findElement(By.id("order-status")).getText()).isNotEmpty();
    }
}
