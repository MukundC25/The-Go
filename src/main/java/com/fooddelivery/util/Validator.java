package com.fooddelivery.util;

import com.fooddelivery.exception.FoodDeliveryException;

/**
 * Utility class for validating input fields in the Online Food Delivery System.
 * Covers: email, phone, password, PIN code, quantity, price (as per test report DT_001-DT_009).
 */
public class Validator {

    // Email: standard RFC-5321 pattern, 1–254 characters
    private static final java.util.regex.Pattern EMAIL_PATTERN =
            java.util.regex.Pattern.compile(
                    "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    // Phone: exactly 10 digits (India standard)
    private static final java.util.regex.Pattern PHONE_PATTERN =
            java.util.regex.Pattern.compile("^[0-9]{10}$");

    // PIN code: exactly 6 digits
    private static final java.util.regex.Pattern PIN_PATTERN =
            java.util.regex.Pattern.compile("^[0-9]{6}$");

    // Password: 8-20 chars, at least one uppercase, one digit, one special char
    private static final java.util.regex.Pattern PASSWORD_PATTERN =
            java.util.regex.Pattern.compile(
                    "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$");

    private Validator() { /* utility class - no instantiation */ }

    // ── Email ─────────────────────────────────────────────────────────────────

    /**
     * TC_AUTH_003, DT_001, DT_002 — Validates email format.
     *
     * @throws FoodDeliveryException if email is null, blank, or malformed
     */
    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw FoodDeliveryException.invalidInput("email", "Email cannot be empty");
        }
        if (email.length() > 254) {
            throw FoodDeliveryException.invalidInput("email",
                    "Email exceeds maximum length of 254 characters");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw FoodDeliveryException.invalidInput("email",
                    "'" + email + "' is not a valid email address");
        }
    }

    // ── Phone ─────────────────────────────────────────────────────────────────

    /**
     * DT_003, DT_004 — Validates 10-digit Indian phone number.
     *
     * @throws FoodDeliveryException if phone is null, blank, or not exactly 10 digits
     */
    public static void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw FoodDeliveryException.invalidInput("phone", "Phone number cannot be empty");
        }
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw FoodDeliveryException.invalidInput("phone",
                    "Phone number must be exactly 10 digits");
        }
    }

    // ── Password ──────────────────────────────────────────────────────────────

    /**
     * TC_AUTH_004 — Validates password strength.
     * Must be 8-20 chars with uppercase, digit, and special character.
     *
     * @throws FoodDeliveryException if password is too weak or out of range
     */
    public static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw FoodDeliveryException.invalidInput("password", "Password cannot be empty");
        }
        if (password.length() < 8) {
            throw FoodDeliveryException.invalidInput("password",
                    "Password must be at least 8 characters long");
        }
        if (password.length() > 20) {
            throw FoodDeliveryException.invalidInput("password",
                    "Password must not exceed 20 characters");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw FoodDeliveryException.invalidInput("password",
                    "Password must contain at least one uppercase letter, one digit, and one special character (@$!%*?&)");
        }
    }

    // ── PIN Code ──────────────────────────────────────────────────────────────

    /**
     * DT_008, DT_009 — Validates 6-digit Indian PIN code.
     *
     * @throws FoodDeliveryException if PIN is not exactly 6 digits
     */
    public static void validatePinCode(String pin) {
        if (pin == null || pin.isBlank()) {
            throw FoodDeliveryException.invalidInput("PIN code", "PIN code cannot be empty");
        }
        if (!PIN_PATTERN.matcher(pin.trim()).matches()) {
            throw FoodDeliveryException.invalidInput("PIN code",
                    "PIN code must be exactly 6 digits");
        }
    }

    // ── Quantity ──────────────────────────────────────────────────────────────

    /**
     * DT_005, DT_006, DT_007 — Validates item quantity (1–100).
     *
     * @throws FoodDeliveryException if quantity is out of range
     */
    public static void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw FoodDeliveryException.invalidInput("quantity",
                    "Minimum quantity is 1");
        }
        if (quantity > 100) {
            throw FoodDeliveryException.invalidInput("quantity",
                    "Maximum quantity is 100 per order");
        }
    }

    // ── Price ─────────────────────────────────────────────────────────────────

    /**
     * Validates item price (0.01–99999.99).
     *
     * @throws FoodDeliveryException if price is out of valid range
     */
    public static void validatePrice(double price) {
        if (price < 0.01) {
            throw FoodDeliveryException.invalidInput("price",
                    "Price must be at least ₹0.01");
        }
        if (price > 99999.99) {
            throw FoodDeliveryException.invalidInput("price",
                    "Price cannot exceed ₹99,999.99");
        }
    }

    // ── General ───────────────────────────────────────────────────────────────

    /**
     * Validates that a string is not null or blank.
     */
    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw FoodDeliveryException.invalidInput(fieldName,
                    fieldName + " cannot be empty");
        }
    }

    /**
     * Returns true if the given email matches the valid pattern.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Returns true if the given phone is valid.
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isBlank()) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Returns true if the given password meets strength requirements.
     */
    public static boolean isStrongPassword(String password) {
        if (password == null) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
