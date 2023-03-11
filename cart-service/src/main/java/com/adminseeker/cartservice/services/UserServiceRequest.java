package com.adminseeker.cartservice.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.adminseeker.cartservice.proxies.UserResponse;

@FeignClient("userservice")
public interface UserServiceRequest {

    @GetMapping("/api/v1/users/{userId}")
    Optional<UserResponse> getUserById(@PathVariable("userId") Long userId);
}