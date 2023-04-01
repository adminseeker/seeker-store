package com.adminseeker.authservice.services;

import com.adminseeker.authservice.dto.AuthRequest;
import com.adminseeker.authservice.dto.EmailResponse;
import com.adminseeker.authservice.dto.Token;
import com.adminseeker.authservice.exceptions.LoginError;
import com.adminseeker.authservice.exceptions.ResourceUpdateError;
import com.adminseeker.authservice.proxies.User;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserServiceRequest userServiceRequest;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    public Token saveUser(User user,Map<String,String> headers){
        Token token = new Token();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = userServiceRequest.registerUser(user,headers).orElse(null);
        if(newUser==null) throw new ResourceUpdateError("Register Error!");
        token.setToken(jwtService.generateToken(newUser.getEmail()));
        return token;        
    }

    public Token generateToken(AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            Token token = new Token();
            token.setToken(jwtService.generateToken(authRequest.getUsername()));
            return token;
        } else {
            throw new LoginError("Login Failed!");
        }
           
    }

    public EmailResponse getEmailFromToken(Token token) {
        if(jwtService.validateToken(token.getToken())==null || jwtService.validateToken(token.getToken()).getEmail()==null) throw new LoginError("Invalid Token!");
        return jwtService.validateToken(token.getToken());
    }

}
