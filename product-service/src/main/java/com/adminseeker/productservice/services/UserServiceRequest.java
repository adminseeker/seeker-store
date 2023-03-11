package com.adminseeker.productservice.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.adminseeker.productservice.proxies.User;

@FeignClient("userservice")
public interface UserServiceRequest {

    @GetMapping("/api/v1/users/{userId}")
    Optional<User> getUserById(@PathVariable("userId") Long userId);
}