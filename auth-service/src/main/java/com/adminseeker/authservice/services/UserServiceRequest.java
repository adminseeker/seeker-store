package com.adminseeker.authservice.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.adminseeker.authservice.proxies.EmailRequest;
import com.adminseeker.authservice.proxies.User;

@FeignClient("userservice")
public interface UserServiceRequest {

    @PostMapping("/api/v1/users/inapi/getuser")
    Optional<User> getUserByEmail(@RequestBody EmailRequest request, @RequestHeader Map<String,String> headers);

    @PostMapping("/api/v1/users")
    Optional<User> registerUser(@RequestBody User user, @RequestHeader Map<String,String> headers);
}