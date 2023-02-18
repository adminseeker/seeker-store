package com.adminseeker.inventoryservice.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException{
    
    public DuplicateResourceException(String msg){
        super(msg);
    }

}

