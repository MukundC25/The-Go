package com.fooddelivery.repository;

import com.fooddelivery.model.Restaurant;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for Restaurant entities.
 */
public class RestaurantRepository {

    private final Map<String, Restaurant> store = new ConcurrentHashMap<>();

    public void save(Restaurant restaurant) {
        store.put(restaurant.getId(), restaurant);
    }

    public Optional<Restaurant> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Restaurant> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Restaurant> findApproved() {
        return store.values().stream()
                .filter(Restaurant::isApproved)
                .collect(Collectors.toList());
    }

    public List<Restaurant> findOpen() {
        return store.values().stream()
                .filter(r -> r.isApproved() && r.isOpen())
                .collect(Collectors.toList());
    }

    public List<Restaurant> findByCuisine(String cuisine) {
        if (cuisine == null || cuisine.isBlank()) return findApproved();
        return store.values().stream()
                .filter(r -> r.isApproved() &&
                        r.getCuisine().equalsIgnoreCase(cuisine.trim()))
                .collect(Collectors.toList());
    }

    public List<Restaurant> findByMinRating(double minRating) {
        return store.values().stream()
                .filter(r -> r.isApproved() && r.getRating() >= minRating)
                .collect(Collectors.toList());
    }

    public List<Restaurant> search(String query) {
        if (query == null || query.isBlank()) return findApproved();
        String q = query.toLowerCase().trim();
        return store.values().stream()
                .filter(r -> r.isApproved() &&
                        (r.getName().toLowerCase().contains(q) ||
                                r.getCuisine().toLowerCase().contains(q)))
                .collect(Collectors.toList());
    }

    public List<Restaurant> findByOwnerId(String ownerId) {
        return store.values().stream()
                .filter(r -> r.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public boolean delete(String id) {
        return store.remove(id) != null;
    }

    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}
