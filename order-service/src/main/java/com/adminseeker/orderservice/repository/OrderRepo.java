package com.adminseeker.orderservice.repository;


import java.util.Optional;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.adminseeker.orderservice.entities.Order;

public interface OrderRepo extends MongoRepository<Order, String>{
    Optional<List<Order>> findByUserId(Long userId);
    Optional<List<Order>> findByStatus(String status);
}
