package com.adminseeker.cartservice.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.adminseeker.cartservice.proxies.EmailRequest;
import com.adminseeker.cartservice.proxies.User;
import com.adminseeker.cartservice.proxies.UserResponse;

@FeignClient("userservice")
public interface UserServiceRequest {

    @GetMapping("/api/v1/users/{userId}")
    Optional<UserResponse> getUserById(@PathVariable("userId") Long userId);

    @PostMapping("/api/v1/users/inapi/getuser")
    Optional<User> getUserByEmail(@RequestBody EmailRequest emailRequest, @RequestHeader Map<String,String> headers);

}