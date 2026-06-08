package com.campingmanager.restaurant.repository;

import com.campingmanager.restaurant.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    boolean existsByNumber(int number);
}
