package com.fooddelivery.model;

import java.util.UUID;

/**
 * Represents a menu item belonging to a restaurant in the Online Food Delivery System.
 */
public class MenuItem {

    private String id;
    private String restaurantId;
    private String name;
    private String description;
    private double price;
    private String category;
    private boolean available;
    private String imageUrl;

    public MenuItem(String restaurantId, String name, String description, double price, String category) {
        this.id = UUID.randomUUID().toString();
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = true;
        this.imageUrl = "";
    }

    // Constructor with explicit ID (for testing)
    public MenuItem(String id, String restaurantId, String name, String description,
                    double price, String category) {
        this(restaurantId, name, description, price, category);
        this.id = id;
    }

    // Getters
    public String getId() { return id; }
    public String getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return available; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return String.format("MenuItem{id='%s', name='%s', price=%.2f, available=%s}",
                id, name, price, available);
    }
}
