package com.adminseeker.userservice.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.CONFLICT)
public class DuplicateEmailException extends RuntimeException{
    
    public DuplicateEmailException(String msg){
        super(msg);
    }

}

