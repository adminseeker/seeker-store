package com.adminseeker.userservice.entities;

import java.util.Map;

import lombok.Data;

@Data
public class LogUserResponse {

    private Map<String,String> headers;
    private UserResponseWithPassword body;
    private String statusCode;
    private int statusCodeValue;
}
