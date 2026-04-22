package com.fooddelivery.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderFlowTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Full E2E: Login → Browse → Add to Cart → Checkout → Place Order")
    void fullE2EOrderFlow() {
        // 1. Login
        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("email")).sendKeys("admin@foodapp.com");
        driver.findElement(By.id("password")).sendKeys("Admin@123!");
        driver.findElement(By.id("login-btn")).click();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlToBe(BASE_URL + "/"));

        // 2. Browse Restaurants
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.className("view-rest")));
        List<WebElement> rests = driver.findElements(By.className("view-rest"));
        assertThat(rests).isNotEmpty();
        
        // Click the first restaurant
        rests.get(0).click();

        // 3. Add to Cart
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.cssSelector(".add-to-cart-btn")));
        driver.findElement(By.cssSelector(".add-to-cart-btn")).click();

        // Should be on cart page now
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/cart"));
        assertThat(driver.getCurrentUrl()).contains("/cart");

        // 4. Checkout
        driver.findElement(By.id("checkout-link")).click();

        // Fill checkout details
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.id("address")));
        driver.findElement(By.id("address")).sendKeys("123 Test Street");
        // Payment method defaults to CASH_ON_DELIVERY based on HtmlTemplates
        driver.findElement(By.id("place-order-btn")).click();

        // 5. Place order
        // Should end up on Order detail page
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.id("order-status")));
        assertThat(driver.findElement(By.id("order-status")).getText()).isNotEmpty();
        assertThat(driver.getCurrentUrl()).contains("/orders/");
    }

    @Test
    @DisplayName("Empty cart checkout blocked")
    void emptyCartCheckoutBlocked() {
        // Login
        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("email")).sendKeys("admin@foodapp.com");
        driver.findElement(By.id("password")).sendKeys("Admin@123!");
        driver.findElement(By.id("login-btn")).click();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(2))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlToBe(BASE_URL + "/"));

        // Go to cart (empty)
        driver.get(BASE_URL + "/checkout");
        
        // Application currently redirects empty checkout back to /cart?msg=Cart+is+empty
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(2))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/cart"));
        assertThat(driver.findElement(By.id("msg")).getText()).contains("Cart is empty");
        assertThat(driver.getCurrentUrl()).contains("/cart");
    }
}
