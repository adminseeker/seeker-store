package com.adminseeker.gatewayserver.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.adminseeker.gatewayserver.proxies.TokenRequest;
import com.adminseeker.gatewayserver.proxies.UserTokenEmail;


@FeignClient("authservice")
public interface AuthServiceRequest {

    @PostMapping("/api/v1/auth/user")
    Optional<UserTokenEmail> getUserEmailFromToken(@RequestBody TokenRequest tokenRequest);

}