package com.adminseeker.authservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextListener;

import com.adminseeker.authservice.proxies.EmailRequest;
import com.adminseeker.authservice.proxies.User;
import com.adminseeker.authservice.services.UserServiceRequest;


import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserServiceRequest userServiceRequest;

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }

    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail(username);
        Map<String, String> headers = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            headers.put(key, value);
        }
        headers.remove("content-length");
        Optional<User> user = userServiceRequest.getUserByEmail(emailRequest,headers);
        return user.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("user not found with name :" + username));
    }

}
