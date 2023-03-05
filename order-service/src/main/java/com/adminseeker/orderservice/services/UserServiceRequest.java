package com.adminseeker.orderservice.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.adminseeker.orderservice.proxies.UserResponse;

@FeignClient(value="user-feign-client",url="http://${USER_SERVICE_URL}/api/v1/users")
public interface UserServiceRequest {

    @GetMapping("/{userId}")
    Optional<UserResponse> getUserById(@PathVariable("userId") Long userId);
}