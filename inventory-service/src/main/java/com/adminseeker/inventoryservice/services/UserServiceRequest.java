package com.adminseeker.inventoryservice.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.adminseeker.inventoryservice.proxies.EmailRequest;
import com.adminseeker.inventoryservice.proxies.User;

@FeignClient("userservice")
public interface UserServiceRequest {

    @GetMapping("/api/v1/users/{userId}")
    Optional<User> getUserById(@PathVariable("userId") Long userId, @RequestHeader Map<String,String> headers);

    @GetMapping("/api/v1/users/inapi/{userId}")
    Optional<User> getUserByIdPublic(@PathVariable("userId") Long userId, @RequestHeader Map<String,String> headers);

    @PostMapping("/api/v1/users/inapi/getuser")
    Optional<User> getUserByEmail(@RequestBody EmailRequest emailRequest, @RequestHeader Map<String,String> headers);
}