package com.fooddelivery.repository;

import com.fooddelivery.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for User entities.
 * Thread-safe using ConcurrentHashMap.
 */
public class UserRepository {

    private final Map<String, User> store = new ConcurrentHashMap<>();

    public void save(User user) {
        store.put(user.getId(), user);
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return store.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    public Optional<User> findByResetToken(String token) {
        return store.values().stream()
                .filter(u -> token.equals(u.getResetToken()))
                .findFirst();
    }

    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<User> findByRole(User.Role role) {
        return store.values().stream()
                .filter(u -> u.getRole() == role)
                .collect(Collectors.toList());
    }

    public boolean existsByEmail(String email) {
        return store.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    public boolean delete(String id) {
        return store.remove(id) != null;
    }

    public int count() {
        return store.size();
    }

    /** Clears all records — used in tests only. */
    public void clear() {
        store.clear();
    }
}
