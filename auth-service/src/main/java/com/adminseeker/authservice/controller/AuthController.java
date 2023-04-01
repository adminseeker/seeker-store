package com.adminseeker.authservice.controller;

import com.adminseeker.authservice.dto.AuthRequest;
import com.adminseeker.authservice.dto.EmailResponse;
import com.adminseeker.authservice.dto.ErrorResponse;
import com.adminseeker.authservice.dto.Token;
import com.adminseeker.authservice.proxies.User;
import com.adminseeker.authservice.services.AuthService;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    private AuthService service;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user,@RequestHeader Map<String,String> headers){
        try {
            headers.remove("content-length");
            ResponseEntity<Token> tokenEntity = new ResponseEntity<Token>(service.saveUser(user,headers),HttpStatus.CREATED);
            logger.debug("New User Registered!, User Email: "+user.getEmail()+" correlation-id: "+headers.get("correlation-id"));
            return tokenEntity;
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            logger.error("Error registering User Email: "+user.getEmail()+" "+e.getMessage()+" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest,@RequestHeader Map<String,String> headers) {
        try {
            headers.remove("content-length");
            ResponseEntity<Token> tokenEntity = new ResponseEntity<Token>(service.generateToken(authRequest),HttpStatus.CREATED);
            logger.debug("User Logged In!, User Email: "+authRequest.getUsername()+" correlation-id: "+headers.get("correlation-id"));
            return tokenEntity;
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            logger.debug("User Logged In Failed!, User Email: "+authRequest.getUsername()+" " +e.getMessage()+" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<?> getEmailFromToken(@RequestBody Token token,@RequestHeader Map<String,String> headers) {
        try {
            headers.remove("content-length");
            ResponseEntity<EmailResponse> responseEntity = new ResponseEntity<EmailResponse>(service.getEmailFromToken(token),HttpStatus.OK);
            logger.debug("Valid Token!, email:  "+responseEntity.getBody() +  " correlation-id: "+headers.get("correlation-id"));
            return responseEntity;
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            logger.debug("Invalid Token!,"+ e.getMessage()+ " correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.FORBIDDEN);
        }
    }
}
