package com.adminseeker.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.UNAUTHORIZED)
public class LoginError extends RuntimeException{
    
    public LoginError(String msg){
        super(msg);
    }

}

