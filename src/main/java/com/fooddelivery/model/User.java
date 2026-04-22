package com.fooddelivery.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a user in the Online Food Delivery System.
 * Supports multiple roles: CUSTOMER, RESTAURANT_OWNER, ADMIN, DELIVERY_PARTNER
 */
public class User {

    public enum Role {
        CUSTOMER, RESTAURANT_OWNER, ADMIN, DELIVERY_PARTNER
    }

    private String id;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;
    private Role role;
    private boolean active;
    private int failedLoginAttempts;
    private boolean locked;
    private String resetToken;
    private LocalDateTime createdAt;

    public User(String name, String email, String phone, String passwordHash, Role role) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = true;
        this.failedLoginAttempts = 0;
        this.locked = false;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with explicit ID (for testing)
    public User(String id, String name, String email, String phone, String passwordHash, Role role) {
        this(name, email, phone, passwordHash, role);
        this.id = id;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public boolean isLocked() { return locked; }
    public String getResetToken() { return resetToken; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setActive(boolean active) { this.active = active; }
    public void setLocked(boolean locked) { this.locked = locked; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }
    public void setRole(Role role) { this.role = role; }

    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.locked = true;
        }
    }

    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.locked = false;
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', email='%s', role=%s, active=%s}",
                id, name, email, role, active);
    }
}
