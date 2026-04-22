package com.fooddelivery.service;

import com.fooddelivery.exception.FoodDeliveryException;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.util.PasswordUtil;
import com.fooddelivery.util.Validator;

import java.util.List;
import java.util.Optional;

/**
 * Service class for user management operations in the Online Food Delivery System.
 * Covers test cases: TC_AUTH_001–TC_AUTH_010, DT_001–DT_009
 */
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ── TC_AUTH_001: Successful user registration ─────────────────────────────

    /**
     * Registers a new customer account.
     *
     * @param name     Full name
     * @param email    Valid email address (unique)
     * @param phone    10-digit phone number
     * @param password Strong password (8-20 chars, uppercase, digit, special char)
     * @return Newly created User
     * @throws FoodDeliveryException if validation fails or email already exists
     */
    public User register(String name, String email, String phone, String password) {
        return registerWithRole(name, email, phone, password, User.Role.CUSTOMER);
    }

    /**
     * Registers a new user with an explicit role.
     */
    public User registerWithRole(String name, String email, String phone,
                                 String password, User.Role role) {
        // Validate inputs (DT_001–DT_009, TC_AUTH_003, TC_AUTH_004)
        Validator.validateNotBlank(name, "name");
        Validator.validateEmail(email);         // DT_001, DT_002
        Validator.validatePhone(phone);         // DT_003, DT_004
        Validator.validatePassword(password);   // TC_AUTH_004

        // TC_AUTH_002: Check for duplicate email
        if (userRepository.existsByEmail(email)) {
            throw FoodDeliveryException.emailAlreadyExists(email);
        }

        String hash = PasswordUtil.hashPassword(password);
        User user = new User(name, email.trim().toLowerCase(), phone.trim(), hash, role);
        userRepository.save(user);
        return user;
    }

    // ── TC_AUTH_005: Successful login ─────────────────────────────────────────

    /**
     * Authenticates a user by email and password.
     *
     * @return Authenticated User
     * @throws FoodDeliveryException on invalid credentials, locked account, etc.
     */
    public User login(String email, String password) {
        Validator.validateNotBlank(email, "email");
        Validator.validateNotBlank(password, "password");

        // TC_AUTH_007: Non-existent email
        User user = userRepository.findByEmail(email)
                .orElseThrow(FoodDeliveryException::invalidCredentials);

        // TC_AUTH_009: Account lockout
        if (user.isLocked()) {
            throw FoodDeliveryException.accountLocked();
        }

        // TC_AUTH_006: Incorrect password
        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            user.incrementFailedAttempts();
            userRepository.save(user);
            throw FoodDeliveryException.invalidCredentials();
        }

        // Reset failed attempts on successful login
        user.resetFailedAttempts();
        userRepository.save(user);
        return user;
    }

    // ── TC_AUTH_008: Password reset flow ─────────────────────────────────────

    /**
     * Initiates password reset — generates and stores a reset token.
     *
     * @param email Email address of the account
     * @return The reset token (would normally be emailed)
     */
    public String initiatePasswordReset(String email) {
        Validator.validateEmail(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> FoodDeliveryException.userNotFound(email));

        String token = PasswordUtil.generateResetToken();
        user.setResetToken(token);
        userRepository.save(user);
        return token;
    }

    /**
     * Completes the password reset using the reset token.
     *
     * @param token       Reset token from email
     * @param newPassword New password to set
     */
    public void completePasswordReset(String token, String newPassword) {
        Validator.validatePassword(newPassword);
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> FoodDeliveryException.invalidInput("token", "Invalid or expired reset token"));

        user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
        user.setResetToken(null);
        user.resetFailedAttempts();
        userRepository.save(user);
    }

    // ── TC_AUTH_010: Profile update ───────────────────────────────────────────

    /**
     * Updates user's profile information.
     */
    public User updateProfile(String userId, String name, String phone) {
        User user = getUserById(userId);
        if (name != null && !name.isBlank()) {
            Validator.validateNotBlank(name, "name");
            user.setName(name.trim());
        }
        if (phone != null && !phone.isBlank()) {
            Validator.validatePhone(phone);
            user.setPhone(phone.trim());
        }
        userRepository.save(user);
        return user;
    }

    // ── Helper / Admin methods ────────────────────────────────────────────────

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> FoodDeliveryException.userNotFound(userId));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deactivateUser(String userId) {
        User user = getUserById(userId);
        user.setActive(false);
        userRepository.save(user);
    }

    public void unlockAccount(String userId) {
        User user = getUserById(userId);
        user.resetFailedAttempts();
        userRepository.save(user);
    }
}
