package com.adminseeker.orderservice.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.adminseeker.orderservice.proxies.Cart;
import com.adminseeker.orderservice.proxies.CartRequest;

import com.adminseeker.orderservice.proxies.CartResponse;

@FeignClient("cartservice")
public interface CartServiceRequest {
    
    @GetMapping("/api/v1/cart/{userId}")
    Optional<CartResponse> getUserCart(@PathVariable("userId") Long userId);

    @DeleteMapping("/api/v1/cart")
    Optional<Cart> clearUserCart(@RequestBody CartRequest cartRequest);
    
}
