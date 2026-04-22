package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a restaurant in the Online Food Delivery System.
 */
public class Restaurant {

    private String id;
    private String ownerId;
    private String name;
    private String cuisine;
    private String address;
    private String phone;
    private double rating;
    private int totalRatings;
    private boolean open;
    private boolean approved;
    private List<MenuItem> menu;
    private String openingHours;
    private double deliveryRadius; // in km

    public Restaurant(String ownerId, String name, String cuisine, String address, String phone) {
        this.id = UUID.randomUUID().toString();
        this.ownerId = ownerId;
        this.name = name;
        this.cuisine = cuisine;
        this.address = address;
        this.phone = phone;
        this.rating = 0.0;
        this.totalRatings = 0;
        this.open = false;
        this.approved = false;
        this.menu = new ArrayList<>();
        this.openingHours = "9:00 AM - 10:00 PM";
        this.deliveryRadius = 5.0;
    }

    // Constructor with explicit ID (for testing)
    public Restaurant(String id, String ownerId, String name, String cuisine,
                      String address, String phone) {
        this(ownerId, name, cuisine, address, phone);
        this.id = id;
    }

    // Getters
    public String getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public String getCuisine() { return cuisine; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public double getRating() { return rating; }
    public int getTotalRatings() { return totalRatings; }
    public boolean isOpen() { return open; }
    public boolean isApproved() { return approved; }
    public List<MenuItem> getMenu() { return menu; }
    public String getOpeningHours() { return openingHours; }
    public double getDeliveryRadius() { return deliveryRadius; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setOpen(boolean open) { this.open = open; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    public void setDeliveryRadius(double deliveryRadius) { this.deliveryRadius = deliveryRadius; }

    public void addMenuItem(MenuItem item) {
        this.menu.add(item);
    }

    public void removeMenuItem(String itemId) {
        this.menu.removeIf(item -> item.getId().equals(itemId));
    }

    public void updateRating(int newRating) {
        double totalScore = this.rating * this.totalRatings + newRating;
        this.totalRatings++;
        this.rating = Math.round((totalScore / this.totalRatings) * 10.0) / 10.0;
    }

    @Override
    public String toString() {
        return String.format("Restaurant{id='%s', name='%s', cuisine='%s', rating=%.1f, open=%s}",
                id, name, cuisine, rating, open);
    }
}
