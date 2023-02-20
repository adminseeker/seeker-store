package com.adminseeker.cartservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.adminseeker.cartservice.entities.Cart;

public interface CartRepo extends MongoRepository<Cart, String>{
    Optional<Cart> findByUserId(Long userId);
}
