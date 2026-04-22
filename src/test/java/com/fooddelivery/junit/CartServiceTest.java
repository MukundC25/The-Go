package com.fooddelivery.junit;

import com.fooddelivery.model.Cart;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.service.CartService;
import com.fooddelivery.exception.FoodDeliveryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartServiceTest {

    private RestaurantRepository restRepo;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        restRepo = new RestaurantRepository();
        cartService = new CartService(restRepo);

        Restaurant r = new Restaurant("rest1", "owner1", "Test Rest", "Cuisine", "Address", "1234567890");
        r.setOpen(true);
        restRepo.save(r);
        r.addMenuItem(new MenuItem("item1", "rest1", "Pizza", "Desc", 100.0, "Cat"));
    }

    @Test
    @DisplayName("TC_ORD_001: Add item to cart from restaurant menu")
    void addItemToCart() {
        cartService.addItem("user1", "rest1", "item1", 2);
        Cart cart = cartService.getCart("user1");
        
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("TC_ORD_003: Remove item from cart")
    void removeItemFromCart() {
        cartService.addItem("user1", "rest1", "item1", 2);
        cartService.removeItem("user1", "item1");
        
        Cart cart = cartService.getCart("user1");
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("TC_ORD_004: Apply valid promo code")
    void applyValidPromo() {
        cartService.addItem("user1", "rest1", "item1", 1);
        cartService.applyPromoCode("user1", "WELCOME");
        
        Cart cart = cartService.getCart("user1");
        assertThat(cart.getPromoCode()).isEqualTo("WELCOME");
        assertThat(cart.getDiscount()).isGreaterThan(0);
    }

    @Test
    @DisplayName("TC_ORD_005: Apply invalid promo code")
    void applyInvalidPromo() {
        cartService.addItem("user1", "rest1", "item1", 1);
        assertThatThrownBy(() -> cartService.applyPromoCode("user1", "INVALID"))
                .isInstanceOf(FoodDeliveryException.class);
    }
}
