package com.fooddelivery.junit;

import com.fooddelivery.model.Restaurant;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantServiceTest {

    private RestaurantRepository restRepo;
    private OrderRepository orderRepo;
    private RestaurantService restService;

    @BeforeEach
    void setUp() {
        restRepo = new RestaurantRepository();
        orderRepo = new OrderRepository();
        restService = new RestaurantService(restRepo, orderRepo);
    }

    @Test
    @DisplayName("TC_RES_001: Register testing & Add new menu item")
    void registerAndAddMenuItem() {
        Restaurant r = restService.registerRestaurant("owner1", "Res 1", "Indian", "Add 1", "1234567890");
        restService.approveRestaurant(r.getId());

        restService.addMenuItem(r.getId(), "Item 1", "Desc", 150.0, "Cat 1");
        
        Restaurant fetched = restService.getRestaurantById(r.getId());
        assertThat(fetched.getMenu()).hasSize(1);
        assertThat(fetched.getMenu().get(0).getName()).isEqualTo("Item 1");
    }

    @Test
    @DisplayName("Get Open Restaurants")
    void getOpenRestaurants() {
        Restaurant r1 = restService.registerRestaurant("owner1", "Res 1", "Indian", "Add 1", "1234567890");
        restService.approveRestaurant(r1.getId());
        r1.setOpen(true);
        
        Restaurant r2 = restService.registerRestaurant("owner2", "Res 2", "Italian", "Add 2", "9876543210");
        restService.approveRestaurant(r2.getId());
        r2.setOpen(false); // Closed

        List<Restaurant> openRests = restService.getOpenRestaurants();
        assertThat(openRests).hasSize(1);
        assertThat(openRests.get(0).getId()).isEqualTo(r1.getId());
    }
}
