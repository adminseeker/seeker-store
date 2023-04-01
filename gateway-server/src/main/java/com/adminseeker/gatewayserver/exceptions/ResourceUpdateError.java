package com.adminseeker.gatewayserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class ResourceUpdateError extends RuntimeException{
    
    public ResourceUpdateError(String msg){
        super(msg);
    }

}

