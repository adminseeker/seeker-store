package com.adminseeker.orderservice.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.adminseeker.orderservice.proxies.EmailRequest;
import com.adminseeker.orderservice.proxies.User;
import com.adminseeker.orderservice.proxies.UserResponse;

@FeignClient("userservice")
public interface UserServiceRequest {

    @GetMapping("/api/v1/users/{userId}")
    Optional<UserResponse> getUserById(@RequestHeader Map<String,String> headers,@PathVariable("userId") Long userId);

    @PostMapping("/api/v1/users/inapi/getuser")
    Optional<User> getUserByEmail(@RequestBody EmailRequest emailRequest, @RequestHeader Map<String,String> headers);
}