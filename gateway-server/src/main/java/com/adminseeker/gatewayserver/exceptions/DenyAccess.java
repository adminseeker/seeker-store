package com.adminseeker.gatewayserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.FORBIDDEN)
public class DenyAccess extends RuntimeException{
    
    public DenyAccess(String msg){
        super(msg);
    }

}

