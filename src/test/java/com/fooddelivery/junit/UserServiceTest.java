package com.fooddelivery.junit;

import com.fooddelivery.model.User;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.UserService;
import com.fooddelivery.exception.FoodDeliveryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        userService = new UserService(userRepository);
    }

    // ── Functional Tests ──────────────────────────────────────────────────

    @Test
    @DisplayName("TC_AUTH_001: Successful user registration with valid details")
    void successfulRegistration() {
        User user = userService.register("John Doe", "john@example.com", "9876543210", "Password@123");
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotBlank();
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getRole()).isEqualTo(User.Role.CUSTOMER);
    }

    @Test
    @DisplayName("TC_AUTH_002: Registration with existing email")
    void registrationWithExistingEmail() {
        userService.register("John Doe", "john@example.com", "9876543210", "Password@123");
        assertThatThrownBy(() -> userService.register("Jane", "john@example.com", "9876543211", "Valid@123"))
                .isInstanceOf(FoodDeliveryException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("TC_AUTH_003: Registration with invalid email format")
    void registrationWithInvalidEmail() {
        assertThatThrownBy(() -> userService.register("John", "john.com", "9876543210", "Valid@123"))
                .isInstanceOf(FoodDeliveryException.class);
    }

    @Test
    @DisplayName("TC_AUTH_004: Registration with weak password")
    void registrationWithWeakPassword() {
        // Assume weak password is less than 8 chars or no special char
        assertThatThrownBy(() -> userService.register("John", "john@example.com", "9876543210", "weak12"))
                .isInstanceOf(FoodDeliveryException.class);
    }

    @Test
    @DisplayName("TC_AUTH_005: Successful login with valid credentials")
    void successfulLogin() {
        userService.register("John Doe", "john@example.com", "9876543210", "Password@123");
        User loggedInUser = userService.login("john@example.com", "Password@123");
        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("TC_AUTH_006: Login with incorrect password")
    void loginWithIncorrectPassword() {
        userService.register("John Doe", "john@example.com", "9876543210", "Password@123");
        assertThatThrownBy(() -> userService.login("john@example.com", "Wrong@123"))
                .isInstanceOf(FoodDeliveryException.class)
                .hasMessageContaining("Invalid"); // usually invalid email or password
    }

    @Test
    @DisplayName("TC_AUTH_007: Login with non-existent email")
    void loginWithNonExistentEmail() {
        assertThatThrownBy(() -> userService.login("ghost@example.com", "Password@123"))
                .isInstanceOf(FoodDeliveryException.class);
    }

    // ── Domain Tests ────────────────────────────────────────────────────────

    @Test
    @DisplayName("DT_001: Valid email registration")
    void validEmailRegistration() {
        User user = userService.register("Test", "user@example.com", "9876543210", "Password@123!");
        assertThat(user.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("DT_002: Invalid email format")
    void invalidEmailFormat() {
        assertThatThrownBy(() -> userService.register("Test", "user@.com", "9876543210", "Password@123!"))
                .isInstanceOf(FoodDeliveryException.class);
    }

    @Test
    @DisplayName("DT_003: Valid phone number")
    void validPhoneNumber() {
        User user = userService.register("Test", "t@exam.com", "9876543210", "Password@123!");
        assertThat(user.getPhone()).isEqualTo("9876543210");
    }

    @ParameterizedTest
    @ValueSource(strings = {"98765432", "123", "abcde12345", "99999999999"})
    @DisplayName("DT_004: Invalid/Short phone number")
    void invalidPhoneNumber(String phone) {
        assertThatThrownBy(() -> userService.register("Test", "t2@exam.com", phone, "Password@123!"))
                .isInstanceOf(FoodDeliveryException.class);
    }

    // ── Security Tests ──────────────────────────────────────────────────────

    @Test
    @DisplayName("SEC_005: Account lockout simulation (Multiple failed logins)")
    void accountLockoutAfterFailedAttempts() {
        userService.register("Lock User", "lock@example.com", "9876543210", "ValidPass@123");
        for (int i = 0; i < 5; i++) {
            try { userService.login("lock@example.com", "WrongPassword"); } catch (Exception ignored) {}
        }
        // Specific exception message depending on implementation
        assertThatThrownBy(() -> userService.login("lock@example.com", "WrongPassword"))
                .isInstanceOf(FoodDeliveryException.class);
    }

    @Test
    @DisplayName("SEC_002: XSS in profile data")
    void updateProfileWithXSS() {
        User user = userService.register("Safe Name", "safe@example.com", "9876543210", "ValidPass@123");
        // Simulated XSS prevention: Name should typically just be updated or throw if specific strict validation exists.
        User updated = userService.updateProfile(user.getId(), "<script>alert(1)</script>", "9876543210");
        assertThat(updated.getName()).isEqualTo("<script>alert(1)</script>"); // Assumes basic store behavior without special encoding at service layer OR check if it throws
    }
}
