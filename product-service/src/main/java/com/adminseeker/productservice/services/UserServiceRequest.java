package com.adminseeker.productservice.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.adminseeker.productservice.proxies.User;

@FeignClient(value="user-feign-client",url="http://${USER_SERVICE_URL:localhost:8081}/api/v1/users")
public interface UserServiceRequest {

    @GetMapping("/{userId}")
    Optional<User> getUserById(@PathVariable("userId") Long userId);
}