package com.adminseeker.orderservice.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.adminseeker.orderservice.proxies.CartResponse;

@FeignClient(value="cart-feign-client",url="http://localhost:8084/api/v1/cart")
public interface CartServiceRequest {
    
    @GetMapping("/{userId}")
    Optional<CartResponse> getUserCart(@PathVariable("userId") Long userId);
    
}
