package com.adminseeker.authservice.controllers;

import com.adminseeker.authservice.dto.AuthRequest;
import com.adminseeker.authservice.dto.EmailResponse;
import com.adminseeker.authservice.dto.ErrorResponse;
import com.adminseeker.authservice.dto.Token;
import com.adminseeker.authservice.proxies.User;
import com.adminseeker.authservice.services.AuthService;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService service;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestHeader Map<String,String> headers,@RequestBody User user){
        ErrorResponse err =null;
        try {
            headers.remove("content-length");
            ResponseEntity<Token> tokenEntity = new ResponseEntity<Token>(service.saveUser(user,headers),HttpStatus.CREATED);
            return tokenEntity;
        } catch (Exception e) {
            err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader Map<String,String> headers,@RequestBody AuthRequest authRequest) {
        try {
            headers.remove("content-length");
            ResponseEntity<Token> tokenEntity = new ResponseEntity<Token>(service.generateToken(authRequest),HttpStatus.CREATED);
            return tokenEntity;
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<?> getEmailFromToken(@RequestHeader Map<String,String> headers,@RequestBody Token token) {
        try {
            headers.remove("content-length");
            ResponseEntity<EmailResponse> responseEntity = new ResponseEntity<EmailResponse>(service.getEmailFromToken(token),HttpStatus.OK);
            return responseEntity;
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.FORBIDDEN);
        }
    }
}
