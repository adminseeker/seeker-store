package com.adminseeker.orderservice.repository;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.adminseeker.orderservice.entities.Order;

public interface OrderRepo extends MongoRepository<Order, String>{
    Optional<Order> findByUserId(Long userId);
}
