package com.adminseeker.orderservice.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.adminseeker.orderservice.proxies.Cart;

import com.adminseeker.orderservice.proxies.CartResponse;

@FeignClient("cartservice")
public interface CartServiceRequest {
    
    @GetMapping("/api/v1/cart")
    Optional<CartResponse> getUserCart(@RequestHeader Map<String,String> headers);

    @DeleteMapping("/api/v1/cart")
    Optional<Cart> clearUserCart(@RequestHeader Map<String,String> headers);
    
}
